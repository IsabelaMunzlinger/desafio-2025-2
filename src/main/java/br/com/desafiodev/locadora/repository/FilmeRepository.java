package br.com.desafiodev.locadora.repository;

import br.com.desafiodev.locadora.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {

    // Verifica se já existe um filme com o mesmo título (ignora maiúsculas/minúsculas)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Filme f WHERE LOWER(f.titulo) = LOWER(:titulo)")
    boolean verificaTituloIgnora(String titulo);

    // Busca filmes ativos com pelo menos 1 exemplar disponível
    List<Filme> findByAtivoTrueAndExemplaresDisponiveisGreaterThan(int i);

    //Busca filmes ativos
    List<Filme> findByAtivoTrue();
}
