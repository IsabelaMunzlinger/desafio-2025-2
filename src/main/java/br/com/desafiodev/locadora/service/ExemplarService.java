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
        // Valida uma quantidade maior que zero para adicionar
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado"));

        // Criação dos exemplares
        List<Exemplar> exemplares = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            Exemplar exemplar = new Exemplar();
            exemplar.setFilme(filme);
            exemplar.setAtivo(ativo != null ? ativo : true); // Se "ativo" estiver marcado, recebe true
            exemplar.setDataCadastro(new Date());
            exemplares.add(exemplar);
        }

        // Conta quantos exemplares estão sendo adicionados como ativos
        long novosAtivos = exemplares.stream().filter(Exemplar::isAtivo).count();

        // Atualiza a quantidade de exemplares disponíveis (somente os ativos)
        Long disponiveis = filme.getExemplaresDisponiveis();
        if (disponiveis == null) disponiveis = 0L;
        filme.setExemplaresDisponiveis(disponiveis + novosAtivos);

        // Salva os exemplares e o filme
        exemplarRepository.saveAll(exemplares); // Salva os exemplares
        filmeRepository.save(filme);            // Atualiza o filme com nova quantidade
    }



    @Transactional
    public void salvar(Exemplar exemplar) {
        // Busca o exemplar existente no banco de dados pelo ID
        Exemplar existente = exemplarRepository.findById(exemplar.getId())
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado com ID: " + exemplar.getId()));

        // Atualiza apenas o status (ativo/inativo)
        existente.setAtivo(exemplar.isAtivo());

        // Salva o exemplar atualizado no banco
        exemplarRepository.save(existente);
    }

    // Retorna todos os exemplares cadastrados no banco
    public List<Exemplar> listarTodos() {
        return exemplarRepository.findAll();
    }

    @Transactional
    public void atualizarExemplar(Long exemplarId, boolean novoStatusAtivo) {
        // Busca o exemplar pelo ID ou lança exceção se não encontrar
        Exemplar exemplar = exemplarRepository.findById(exemplarId)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));

        // Verifica se o exemplar possui alguma locação ainda não devolvida
        boolean possuiLocacaoPendente = exemplar.getLocacoesExemplares().stream()
                .anyMatch(loc -> loc.getLocacao().getDataDevolvido() == null);

        // Não é possível inativar um exemplar que está alugado
        if (!novoStatusAtivo && possuiLocacaoPendente) {
            throw new IllegalArgumentException("Não é possível inativar um exemplar com locações pendentes.");
        }

        // Salva o status anterior para saber se houve alteração
        boolean statusAnterior = exemplar.isAtivo();
        exemplar.setAtivo(novoStatusAtivo);

        // Atualiza a contagem de exemplares disponíveis no filme, se o status foi alterado
        Filme filme = exemplar.getFilme();
        if (statusAnterior != novoStatusAtivo) {
            int ajuste = novoStatusAtivo ? 1 : -1; // Se ativou, soma 1. Se inativou, subtrai 1.
            filme.setExemplaresDisponiveis(filme.getExemplaresDisponiveis() + ajuste);
            filmeRepository.save(filme);
        }

        exemplarRepository.save(exemplar);
    }

    // Somente permite excluir um exemplar se ele não foi alugado nenhuma vez
    @Transactional
    public void excluirExemplar(Long exemplarId) {
        Exemplar exemplar = exemplarRepository.findById(exemplarId)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado"));

        // Se o exemplar já foi alugado alguma vez, não permitir a exclusão
        if (exemplar.getLocacoesExemplares() != null && !exemplar.getLocacoesExemplares().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir um exemplar que já foi alugado.");
        }

        Filme filme = exemplar.getFilme();

        if (exemplar.isAtivo()) {
            Long disponiveis = filme.getExemplaresDisponiveis();
            filme.setExemplaresDisponiveis(Math.max(0, disponiveis - 1));
            filmeRepository.save(filme);
        }

        exemplarRepository.delete(exemplar);
    }

    // Lista os exemplares disponíveis
    public List<Exemplar> listarExemplaresDisponiveisParaLocacao() {
        return exemplarRepository.buscarExemplaresDisponiveisParaLocacao();
    }

    // Busca o exemplar por id
    public Exemplar buscarPorId(Long id) {
        return exemplarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar não encontrado com ID: " + id));
    }

}
