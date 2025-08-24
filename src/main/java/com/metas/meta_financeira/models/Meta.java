package com.metas.meta_financeira.models;

import java.math.BigDecimal;
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

    @Column(name = "valor_atual", precision = 19, scale = 2)
    private BigDecimal valorAtual;

    @Column(name = "valor_total", precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusMeta status = StatusMeta.ATIVA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Integrante> integrantes = new ArrayList<>();

    public Meta() {}

    /// Construtor
    public Meta(String nome, BigDecimal valorTotal, User owner) {
        this.nome = nome;
        this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
        this.owner = owner;
    }

    /// Métodos Públicos
    public void adicionarIntegrante(Integrante integrante) {
        integrante.setMeta(this);
        integrantes.add(integrante);
    }

    public void adicionarContribuicaoIntegrante(String nome, BigDecimal valor) {
        if (status != StatusMeta.ATIVA) {
            throw new IllegalStateException("Não é possível contribuir para uma meta já concluída.");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da contribuição deve ser maior que zero.");
        }

        BigDecimal faltante = restanteFaltante();

        BigDecimal valorAdicionado = valor.compareTo(faltante) > 0 ? faltante : valor;

        for(Integrante integrante : integrantes) {
            if (integrante.getNome().equalsIgnoreCase(nome)) {
                integrante.adicionarContribuicao(valorAdicionado);
                this.setValorAtual(this.getValorAtual().add(valorAdicionado));

                if (this.valorAtual.compareTo(this.valorTotal) >= 0) {
                    this.status = StatusMeta.ATINGIDA;
                }
                return;
            }
        }
        throw new IllegalArgumentException("Integrante não encontrado: " + nome);
    }

    public List<String> getRelatorioContribuicoes() {
        List<String> relatorio = new ArrayList<>();
        for(Integrante integrante : integrantes) {
            BigDecimal percentual = integrante.getPercentualDaMeta(valorTotal);
            relatorio.add(
                    String.format("%s contribuiu R$ %.2f (%.2f%% da meta)",
                        integrante.getNome(),
                        integrante.getContribuicao(),
                        percentual.doubleValue())
            );
        }
        return relatorio;
    }

    public BigDecimal restanteFaltante() {
        BigDecimal restante = valorTotal.subtract(valorAtual);
        return restante.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : restante;
    }


    /// Métodos especiais
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getValorAtual() { return valorAtual; }
    public void setValorAtual(BigDecimal valorAtual) { this.valorAtual = valorAtual; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public List<Integrante> getIntegrantes() { return integrantes; }
    public void setIntegrantes(List<Integrante> integrantes) {
        this.integrantes = integrantes != null ? integrantes : new ArrayList<>();
        for (Integrante i : this.integrantes) {
            i.setMeta(this);
        }
    }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public StatusMeta getStatus() { return status; }
    public void setStatus(StatusMeta status) { this.status = status; }
}
