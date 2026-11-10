package br.com.cashhub.controller;

import br.com.cashhub.model.Movimentacao;
import br.com.cashhub.service.MovimentacaoService;
import br.com.cashhub.dto.ResumoFinanceiro;
import br.com.cashhub.dto.ResumoCategoria;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(
            MovimentacaoService movimentacaoService) {

        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping
public Movimentacao salvar(
        @Valid @RequestBody Movimentacao movimentacao) {

    return movimentacaoService.salvar(movimentacao);
}

@GetMapping
public List<Movimentacao> listarTodas() {

    return movimentacaoService.listarTodas();
}

    @GetMapping("/resumo")
public ResumoFinanceiro obterResumo(
        @RequestParam String mes) {

    YearMonth anoMes = YearMonth.parse(mes);

    LocalDate inicio = anoMes.atDay(1);
    LocalDate fim = anoMes.atEndOfMonth();

    return movimentacaoService.gerarResumo(inicio, fim);
}

@GetMapping("/resumo/categorias")
public List<ResumoCategoria> obterResumoPorCategoria(
        @RequestParam String mes) {

    YearMonth anoMes = YearMonth.parse(mes);

    LocalDate inicio = anoMes.atDay(1);
    LocalDate fim = anoMes.atEndOfMonth();

    return movimentacaoService.gerarResumoPorCategoria(
            inicio,
            fim
    );
}

@PutMapping("/{id}")
public Movimentacao atualizar(
        @PathVariable Long id,
        @Valid @RequestBody Movimentacao movimentacao) {

    return movimentacaoService.atualizar(id, movimentacao);
}

@DeleteMapping("/{id}")
public void excluir(@PathVariable Long id) {

    movimentacaoService.excluir(id);
}
}
