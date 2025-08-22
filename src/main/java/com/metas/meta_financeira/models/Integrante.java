package com.metas.meta_financeira.models;

import java.math.BigDecimal;
import java.util.IllformedLocaleException;
import jakarta.persistence.*;

@Entity
@Table(name = "integrante")
public class Integrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private BigDecimal contribuicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_id")
    private Meta meta;

    public Integrante() {}

    /// Construtor
    public Integrante(String nome, BigDecimal contribuicaoInicial) {
        this.nome = nome;
        this.contribuicao = contribuicaoInicial != null ? contribuicaoInicial : BigDecimal.ZERO;
    }

    /// Métodos públicos
    public void adicionarContribuicao(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllformedLocaleException("Valor da contribuição não pode ser 0 nem negativo.");
        }
        this.setContribuicao(this.getContribuicao().add(valor));
    }

    public BigDecimal getPercentualDaMeta(BigDecimal valorTotalMeta) {
        if (valorTotalMeta == null || valorTotalMeta.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return this.getContribuicao()
                .divide(valorTotalMeta, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /// Métodos especiais
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getContribuicao() { return contribuicao; }
    public void setContribuicao(BigDecimal contribuicao) {
        this.contribuicao = contribuicao != null ? contribuicao : BigDecimal.ZERO;
    }
    public Meta getMeta() { return meta; }
    public void setMeta(Meta meta) { this.meta = meta; }
}
