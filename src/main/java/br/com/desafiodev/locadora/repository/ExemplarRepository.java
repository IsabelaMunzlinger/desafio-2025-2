package br.com.desafiodev.locadora.repository;

import br.com.desafiodev.locadora.model.Exemplar;
import br.com.desafiodev.locadora.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    long countByFilme(Filme filme);


    // Busca os exemplares ativos
    @Query("SELECT e FROM Exemplar e JOIN e.filme f " +
            "WHERE e.ativo = true AND f.ativo = true AND f.exemplaresDisponiveis > 0")
    List<Exemplar> buscarExemplaresDisponiveisParaLocacao();

}


