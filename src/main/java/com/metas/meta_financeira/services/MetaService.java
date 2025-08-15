package com.metas.meta_financeira.services;

import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetaService {

    private static final Logger logger = LoggerFactory.getLogger(MetaService.class);
    private final List<Meta> metas = new ArrayList<>();

    public Meta criarMeta(String nome, double valorTotal) {
        logger.info("[LOG] Criando meta: nome={}, valorTotal={}", nome, valorTotal);
        try {
            if(nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("O nome da meta não pode ser vazio.");
            }
            if(valorTotal <= 0) {
                throw new IllegalArgumentException("O valor total da meta deve ser maior que zero.");
            }
            Meta meta = new Meta(nome, valorTotal);
            metas.add(meta);
            logger.info("[LOG] Meta criada com sucesso: {}", meta);
            return meta;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao criar meta", e);
            throw e;
        }
    }

    public List<Meta> listarMetas() {
        logger.info("[LOG] Listando todas as metas");
        return metas;
    }

    public Meta buscarMetaPorNome(String nome) {
        logger.info("[LOG] Buscando meta pelo nome: {}", nome);
        return metas.stream()
                .filter(meta -> meta.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("[LOG] Meta não encontrada: {}", nome);
                    return new IllegalArgumentException("Meta não encontrada: " + nome);
                });
    }

    public void adicionarIntegrante(String nomeMeta, String nomeIntegrante, double contribuicaoInicial) {
        logger.info("[LOG] Adicionando integrante '{}' na meta '{}' com contribuição inicial {}",
                nomeIntegrante, nomeMeta, contribuicaoInicial);
        try {
            Meta meta = buscarMetaPorNome(nomeMeta);
            if(nomeIntegrante == null || nomeIntegrante.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do integrante não pode ser vazio.");
            }
            if(contribuicaoInicial < 0) {
                throw new IllegalArgumentException("Contribuição inicial não pode ser negativa.");
            }
            Integrante integrante = new Integrante(nomeIntegrante, contribuicaoInicial);
            meta.adicionarIntegrante(integrante);
            meta.adicionarContribuicaoIntegrante(nomeIntegrante, contribuicaoInicial);
            logger.info("[LOG] Integrante '{}' adicionado com sucesso à meta '{}'", nomeIntegrante, nomeMeta);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao adicionar integrante na meta", e);
            throw e;
        }
    }

    public void contribuir(String nomeMeta, String nomeIntegrante, double valor) {
        logger.info("[LOG] Registrando contribuição para meta='{}', integrante='{}', valor={}",
                nomeMeta, nomeIntegrante, valor);
        try {
            if(valor <= 0) {
                throw new IllegalArgumentException("O valor da contribuição deve ser maior que zero.");
            }
            Meta meta = buscarMetaPorNome(nomeMeta);
            meta.adicionarContribuicaoIntegrante(nomeIntegrante, valor);
            logger.info("[LOG] Contribuição registrada com sucesso para '{}'", nomeIntegrante);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao registrar contribuição", e);
            throw e;
        }
    }

    public List<String> relatorioContribuicoes(String nomeMeta) {
        logger.info("[LOG] Gerando relatório de contribuições para meta '{}'", nomeMeta);
        try {
            Meta meta = buscarMetaPorNome(nomeMeta);
            List<String> relatorio = meta.getRelatorioContribuicoes();
            logger.info("[LOG] Relatório gerado com {} linhas", relatorio.size());
            return relatorio;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao gerar relatório de contribuições", e);
            throw e;
        }
    }

    public void excluirMeta(String nomeMeta) {
        logger.info("[LOG] Excluindo meta '{}'", nomeMeta);
        try {
            boolean removed = metas.removeIf(meta -> meta.getNome().equalsIgnoreCase(nomeMeta));
            if(!removed) {
                logger.warn("[LOG] Tentativa de excluir meta inexistente: {}", nomeMeta);
                throw new IllegalArgumentException("Meta não encontrada: " + nomeMeta);
            }
            logger.info("[LOG] Meta '{}' excluída com sucesso", nomeMeta);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao excluir meta", e);
            throw e;
        }
    }
}
