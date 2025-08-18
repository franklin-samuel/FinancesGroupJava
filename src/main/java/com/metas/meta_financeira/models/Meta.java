package com.metas.meta_financeira.models;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Meta  {
    private static final AtomicLong COUNTER = new AtomicLong();

    private Long id;
    private String nome;
    private double valorAtual;
    private double valorTotal;
    private List<Integrante> integrantes;

    /// Construtor
    public Meta(String nome, double valorTotal) {
        this.id = COUNTER.incrementAndGet();
        this.nome = nome;
        this.valorAtual = 0;
        this.valorTotal = valorTotal;
        this.integrantes = new ArrayList<>();
    }

    /// Métodos Públicos
    public void adicionarIntegrante(Integrante integrante) {
        integrantes.add(integrante);
    }

    public void adicionarContribuicaoIntegrante(String nome, double valor) {
        for(Integrante integrante : integrantes) {
            if (integrante.getNome().equalsIgnoreCase(nome)) {
                integrante.adicionarContribuicao(valor);
                this.setValorAtual(this.getValorAtual() + valor);
                return;
            }
        }
        throw new IllegalArgumentException("Integrante não encontrado: " + nome);
    }

    public List<String> getRelatorioContribuicoes() {
        List<String> relatorio = new ArrayList<>();
        for(Integrante integrante : integrantes) {
            double percentual = integrante.getPercentualDaMeta(valorTotal);
            relatorio.add(
                    String.format("%s contribuiu R$ %.2f (%.2f%% da meta)",
                        integrante.getNome(),
                        integrante.getContribuicao(),
                        percentual)
            );
        }
        return relatorio;
    }

    public double restanteFaltante() {
        return getValorTotal() - getValorAtual();
    }


    /// Métodos especiais
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<Integrante> getIntegrantes() {
        return integrantes;
    }

    public double getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(double novoValor) {
        this.valorAtual = novoValor;
    }

    public void setIntegrantes(List<Integrante> integrantes) {
        this.integrantes = integrantes;
    }
}
