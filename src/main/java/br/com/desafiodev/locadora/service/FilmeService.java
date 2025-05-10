package br.com.desafiodev.locadora.service;


import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmeService {
    @Autowired
    private FilmeRepository filmeRepository;

    public Filme salvarFilme(Filme filme) {
        boolean existe = filmeRepository.verificaTituloIgnora(filme.getTitulo());
        if (existe) {
            throw new IllegalArgumentException("Já existe um filme cadastrado com este título");
        }
        return filmeRepository.save(filme);
    }
}
