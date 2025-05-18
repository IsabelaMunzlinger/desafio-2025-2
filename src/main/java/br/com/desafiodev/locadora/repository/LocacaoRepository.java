package br.com.desafiodev.locadora.repository;

import br.com.desafiodev.locadora.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
}
