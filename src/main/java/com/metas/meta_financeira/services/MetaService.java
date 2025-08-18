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

    // Criar meta
    public Meta criarMeta(String nome, double valorTotal) {
        logger.info("[LOG] Criando meta: nome={}, valorTotal={}", nome, valorTotal);

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da meta não pode ser vazio.");
        }
        if (valorTotal <= 0) {
            throw new IllegalArgumentException("O valor total da meta deve ser maior que zero.");
        }

        Meta meta = new Meta(nome, valorTotal);
        metas.add(meta);

        logger.info("[LOG] Meta criada com sucesso: id={}, nome={}", meta.getId(), meta.getNome());
        return meta;
    }

    // Listar metas
    public List<Meta> listarMetas() {
        logger.info("[LOG] Listando todas as metas (total={})", metas.size());
        return metas;
    }

    // Buscar por ID
    public Meta buscarMetaPorId(Long id) {
        logger.info("[LOG] Buscando meta por id={}", id);
        return metas.stream()
                .filter(meta -> meta.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("[LOG] Meta não encontrada com id={}", id);
                    return new IllegalArgumentException("Meta não encontrada com id: " + id);
                });
    }

    // Adicionar integrante
    public void adicionarIntegrante(Long metaId, String nomeIntegrante, double contribuicaoInicial) {
        logger.info("[LOG] Adicionando integrante '{}' na meta id={} com contribuição inicial={}",
                nomeIntegrante, metaId, contribuicaoInicial);

        if (nomeIntegrante == null || nomeIntegrante.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do integrante não pode ser vazio.");
        }
        if (contribuicaoInicial < 0) {
            throw new IllegalArgumentException("Contribuição inicial não pode ser negativa.");
        }

        Meta meta = buscarMetaPorId(metaId);

        Integrante integrante = new Integrante(nomeIntegrante, 0);
        meta.adicionarIntegrante(integrante);

        if (contribuicaoInicial > 0) {
            meta.adicionarContribuicaoIntegrante(nomeIntegrante, contribuicaoInicial);
        }

        logger.info("[LOG] Integrante '{}' adicionado na meta id={}", nomeIntegrante, metaId);
    }

    // Contribuir
    public void contribuir(Long metaId, String nomeIntegrante, double valor) {
        logger.info("[LOG] Registrando contribuição metaId={}, integrante='{}', valor={}",
                metaId, nomeIntegrante, valor);

        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da contribuição deve ser maior que zero.");
        }

        Meta meta = buscarMetaPorId(metaId);
        meta.adicionarContribuicaoIntegrante(nomeIntegrante, valor);

        logger.info("[LOG] Contribuição registrada com sucesso para integrante='{}', metaId={}",
                nomeIntegrante, metaId);
    }

    // Relatório de contribuições
    public List<String> relatorioContribuicoes(Long metaId) {
        logger.info("[LOG] Gerando relatório da meta id={}", metaId);

        Meta meta = buscarMetaPorId(metaId);
        List<String> relatorio = meta.getRelatorioContribuicoes();

        logger.info("[LOG] Relatório gerado com {} linhas para meta id={}", relatorio.size(), metaId);
        return relatorio;
    }

    // Excluir meta
    public void excluirMeta(Long id) {
        logger.info("[LOG] Excluindo meta id={}", id);

        boolean removed = metas.removeIf(meta -> meta.getId().equals(id));
        if (!removed) {
            logger.warn("[LOG] Tentativa de excluir meta inexistente id={}", id);
            throw new IllegalArgumentException("Meta não encontrada com id: " + id);
        }

        logger.info("[LOG] Meta id={} excluída com sucesso", id);
    }
}
