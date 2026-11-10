package br.com.cashhub.service;

import br.com.cashhub.dto.ResumoFinanceiro;
import br.com.cashhub.dto.ResumoCategoria;

import br.com.cashhub.model.Movimentacao;
import br.com.cashhub.model.TipoMovimentacao;
import br.com.cashhub.repository.MovimentacaoRepository;
import br.com.cashhub.exception.MovimentacaoNaoEncontradaException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository) {

        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public Movimentacao salvar(Movimentacao movimentacao) {
        return movimentacaoRepository.save(movimentacao);
    }

    @Transactional(readOnly = true)
    public List<Movimentacao> listarTodas() {
        return movimentacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiro gerarResumo(
            LocalDate inicio,
            LocalDate fim) {

        List<Movimentacao> movimentacoes = movimentacaoRepository.findByDataBetween(inicio, fim);

        BigDecimal receitas = movimentacoes.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.RECEITA)
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesas = movimentacoes.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.DESPESA)
                .map(Movimentacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = receitas.subtract(despesas);

        return new ResumoFinanceiro(receitas, despesas, saldo);
    }

    @Transactional
    public Movimentacao atualizar(Long id, Movimentacao novosDados) {

        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new MovimentacaoNaoEncontradaException(
                        "Movimentação não encontrada."));

        movimentacao.setDescricao(novosDados.getDescricao());
        movimentacao.setValor(novosDados.getValor());
        movimentacao.setTipo(novosDados.getTipo());
        movimentacao.setCategoria(novosDados.getCategoria());
        movimentacao.setData(novosDados.getData());

        return movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public void excluir(Long id) {

        if (!movimentacaoRepository.existsById(id)) {
            throw new MovimentacaoNaoEncontradaException("Movimentação não encontrada.");
        }

        movimentacaoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ResumoCategoria> gerarResumoPorCategoria(
            LocalDate inicio,
            LocalDate fim) {

        List<Movimentacao> movimentacoes = movimentacaoRepository.findByDataBetween(inicio, fim);

        Map<String, BigDecimal> totaisPorCategoria = movimentacoes.stream()

                .filter(m -> m.getTipo() == TipoMovimentacao.DESPESA)

                .collect(Collectors.groupingBy(
                        Movimentacao::getCategoria,
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Movimentacao::getValor,
                                BigDecimal::add)));

        return totaisPorCategoria.entrySet()
                .stream()
                .map(entry -> new ResumoCategoria(
                        entry.getKey(),
                        entry.getValue()))
                .toList();
    }
}