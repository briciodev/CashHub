package br.com.cashhub.dto;

import java.math.BigDecimal;

public class ResumoFinanceiro {

    private BigDecimal receitas;
    private BigDecimal despesas;
    private BigDecimal saldo;

    public ResumoFinanceiro(
            BigDecimal receitas,
            BigDecimal despesas,
            BigDecimal saldo) {

        this.receitas = receitas;
        this.despesas = despesas;
        this.saldo = saldo;
    }

    public BigDecimal getReceitas() {
        return receitas;
    }

    public BigDecimal getDespesas() {
        return despesas;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}