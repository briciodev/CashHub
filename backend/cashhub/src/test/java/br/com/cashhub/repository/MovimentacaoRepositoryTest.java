package br.com.cashhub.repository;

import br.com.cashhub.model.Movimentacao;
import br.com.cashhub.model.TipoMovimentacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MovimentacaoRepositoryTest {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Test
    void deveSalvarUmaMovimentacao() {

        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setDescricao("Salário");
        movimentacao.setValor(new BigDecimal("1500.00"));
        movimentacao.setTipo(TipoMovimentacao.RECEITA);
        movimentacao.setCategoria("Trabalho");
        movimentacao.setData(LocalDate.now());

        Movimentacao movimentacaoSalva =
                movimentacaoRepository.save(movimentacao);

        assertNotNull(movimentacaoSalva.getId());
    }
}
