package com.metas.meta_financeira.models;

import java.util.IllformedLocaleException;

public class Integrante {
    /// Atributos
    private String nome;
    private double contribuicao;

    /// Construtor
    public Integrante(String nome, double contribuicaoInicial) {
        this.nome = nome;
        this.contribuicao = contribuicaoInicial;
    }

    /// Métodos públicos
    public void adicionarContribuicao(double valor) {
        if (valor <= 0) {
            throw new IllformedLocaleException("Valor da contribuição não pode ser 0 nem negativo.");
        }
        this.setContribuicao(this.getContribuicao() + valor);
    }

    public double getPercentualDaMeta(double valorTotalMeta) {
        if (valorTotalMeta == 0) {
            return 0;
        }
        return (contribuicao / valorTotalMeta) * 100;
    }

    /// Métodos especiais
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getContribuicao() {
        return contribuicao;
    }

    public void setContribuicao(double contribuicao) {
        this.contribuicao = contribuicao;
    }
}
