package br.com.desafiodev.locadora.repository;

import br.com.desafiodev.locadora.model.LocacaoExemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocacaoExemplarRepository extends JpaRepository<LocacaoExemplar, Long> {
}
