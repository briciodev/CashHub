package br.com.cashhub.repository;

import br.com.cashhub.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoRepository
        extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByDataBetween(
            LocalDate inicio,
            LocalDate fim
    );

}