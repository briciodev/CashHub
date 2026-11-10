package br.com.cashhub.controller;

import br.com.cashhub.dto.ResumoCategoria;
import br.com.cashhub.dto.ResumoFinanceiro;
import br.com.cashhub.service.MovimentacaoService;
import br.com.cashhub.service.OllamaService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final OllamaService ollamaService;
    private final MovimentacaoService movimentacaoService;

    public IaController(
            OllamaService ollamaService,
            MovimentacaoService movimentacaoService) {

        this.ollamaService = ollamaService;
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping("/testar")
    public Map<String, String> testarOllama(
            @RequestParam(defaultValue = "Quanto é 2 + 2?") String pergunta) {

        String resposta = ollamaService.gerarResposta(pergunta);

        return Map.of("resposta", resposta);
    }

    @GetMapping("/analisar")
    public Map<String, String> analisarFinancas(
            @RequestParam String mes) {

        YearMonth periodo = YearMonth.parse(mes);

        LocalDate inicio = periodo.atDay(1);
        LocalDate fim = periodo.atEndOfMonth();

        ResumoFinanceiro resumo =
                movimentacaoService.gerarResumo(inicio, fim);

        List<ResumoCategoria> categorias =
                movimentacaoService.gerarResumoPorCategoria(inicio, fim);

        String dadosCategorias;

        if (categorias.isEmpty()) {
            dadosCategorias = "Nenhuma despesa registrada.";
        } else {
            dadosCategorias = String.join("\n",
                    categorias.stream()
                            .map(c -> c.getCategoria() + ": R$ "
                                    + c.getTotal().toPlainString())
                            .toList());
        }

        String prompt = """
                Você é o assistente financeiro do CashHub.

                Analise os dados financeiros do mês %s.

                RECEITAS: R$ %s
                DESPESAS: R$ %s
                SALDO: R$ %s

                DESPESAS POR CATEGORIA:
                %s

                Gere uma análise em português, clara e objetiva.

                Sua resposta deve conter:
                1. Um resumo da situação financeira.
                2. Uma avaliação das despesas.
                3. Identificação das categorias que mais consumiram dinheiro.
                4. Sugestões práticas para melhorar a organização financeira.

                Não invente valores ou informações.
                Utilize somente os dados fornecidos.
                Se não houver movimentações, informe isso claramente.
                Não apresente recomendações de investimentos.
                """.formatted(
                mes,
                resumo.getReceitas().toPlainString(),
                resumo.getDespesas().toPlainString(),
                resumo.getSaldo().toPlainString(),
                dadosCategorias
        );

        String analise = ollamaService.gerarResposta(prompt);

        return Map.of(
                "mes", mes,
                "analise", analise
        );
    }
}


