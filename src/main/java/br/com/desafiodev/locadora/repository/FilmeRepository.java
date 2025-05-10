package br.com.desafiodev.locadora.repository;

import br.com.desafiodev.locadora.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {

    // Não permite salvar dois filmes iguais, mesmo título
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Filme f WHERE LOWER(f.titulo) = LOWER(:titulo)")
    boolean verificaTituloIgnora(String titulo);
}