package br.com.desafiodev.locadora.service;

import br.com.desafiodev.locadora.model.Exemplar;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.repository.ExemplarRepository;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExemplarService {

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    @Transactional
    public void adicionarExemplares(Long filmeId, int quantidade, Boolean ativo) {
        // Validações
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));

        // Contagem dos exemplares ativos do filme
        long totalAtuais = exemplarRepository.countByFilme(filme);
        if (totalAtuais + quantidade > 10) {
            throw new IllegalArgumentException("Não é permitido mais de 10 exemplares.");
        }

        // Criação dos exemplares
        List<Exemplar> exemplares = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Exemplar exemplar = new Exemplar();
            exemplar.setFilme(filme);
            exemplar.setAtivo(ativo != null ? ativo : true);
            exemplar.setDataCadastro(new Date());
            exemplares.add(exemplar);
        }

        // Atualiza a quantidade de exemplares disponíveis
        filme.setExemplaresDisponiveis(totalAtuais + quantidade);

        // Salva os exemplares e o filme
        exemplarRepository.saveAll(exemplares); // Salva os exemplares
        filmeRepository.save(filme); // Atualiza o filme com a nova quantidade de exemplares
    }

}
