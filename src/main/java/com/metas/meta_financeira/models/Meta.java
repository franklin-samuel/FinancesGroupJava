package com.metas.meta_financeira.models;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;

@Entity
@Table(name = "meta")
public class Meta  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(name = "valor_atual")
    private double valorAtual;

    @Column(name = "valor_total")
    private double valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Integrante> integrantes = new ArrayList<>();

    public Meta() {}

    /// Construtor
    public Meta(String nome, double valorTotal, User owner) {
        this.nome = nome;
        this.valorTotal = valorTotal;
        this.valorAtual = 0;
        this.owner = owner;
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
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getValorAtual() { return valorAtual; }
    public void setValorAtual(double valorAtual) { this.valorAtual = valorAtual; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public List<Integrante> getIntegrantes() { return integrantes; }
    public void setIntegrantes(List<Integrante> integrantes) { this.integrantes = integrantes; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}
