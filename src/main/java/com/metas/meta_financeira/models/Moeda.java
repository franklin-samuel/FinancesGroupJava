package com.metas.meta_financeira.models;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Moeda {

    private BigDecimal valor;
    private static final String UNIDADE_MONETARIA = "R$";
    private static final DecimalFormat FORMATO = new DecimalFormat(UNIDADE_MONETARIA + " #,###,##0.00");

    public Moeda(BigDecimal valor) {
        this.valor = valor != null ? valor : BigDecimal.ZERO;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String formatado() {
        return FORMATO.format(valor);
    }
}
