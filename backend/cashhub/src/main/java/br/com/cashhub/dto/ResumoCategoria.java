package br.com.cashhub.dto;

import java.math.BigDecimal;

public class ResumoCategoria {

    private String categoria;
    private BigDecimal total;

    public ResumoCategoria(
            String categoria,
            BigDecimal total) {

        this.categoria = categoria;
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
