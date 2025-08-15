package com.metas.meta_financeira.services;
import com.metas.meta_financeira.models.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;

@Service
public class MetaService {

    private final List<Meta> metas = new ArrayList<>();

    public Meta criarMeta(String nome, double valorTotal) {
        Meta meta = new Meta(nome, valorTotal);
        metas.add(meta);
        return meta;
    }

    public List<Meta> listarMetas() {
        return metas;
    }

    public Meta buscarMetaPorNome(String nome) {
        for (Meta meta : metas) {
            if(meta.getNome().equalsIgnoreCase(nome)) {
                return meta;
            }
        }
        throw new IllegalArgumentException("Meta não encontrada: " + nome);
    }

    public void adicionarIntegrante(String nomeMeta, String nomeIntegrante, double contribuicaoInicial) {
        Meta meta = buscarMetaPorNome(nomeMeta);
        Integrante integrante = new Integrante(nomeIntegrante, contribuicaoInicial);
        meta.adicionarIntegrante(integrante);
        meta.adicionarContribuicaoIntegrante(nomeIntegrante, contribuicaoInicial);
    }

    public void contribuir(String nomeMeta, String nomeIntegrante, double valor) {
        Meta meta = buscarMetaPorNome(nomeMeta);
        meta.adicionarContribuicaoIntegrante(nomeIntegrante, valor);
    }

    public List<String> relatorioContribuicoes(String nomeMeta) {
        Meta meta = buscarMetaPorNome(nomeMeta);
        return meta.getRelatorioContribuicoes();
    }

    public void excluirMeta(String nomeMeta) {
        metas.remove(nomeMeta);
    }
}
