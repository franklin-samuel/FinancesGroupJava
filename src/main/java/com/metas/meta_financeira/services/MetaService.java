package com.metas.meta_financeira.services;

import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.StatusMeta;
import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.repositories.MetaRepository;
import com.metas.meta_financeira.repositories.UserRespository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MetaService {

    private static final Logger logger = LoggerFactory.getLogger(MetaService.class);

    private final MetaRepository metaRepository;
    private final UserRespository userRespository;

    public MetaService(MetaRepository metaRepository, UserRespository userRespository) {
        this.metaRepository = metaRepository;
        this.userRespository = userRespository;
    }

    // Criar Meta
    @Transactional
    public Meta criarMeta(String nome, BigDecimal valorTotal, User owner) {
        try {
            logger.info("[LOG] Criando meta: nome={}, valorTotal={}, owner={}", nome, valorTotal, owner != null ? owner.getEmail() : "null");

            if (owner == null) {
                throw new IllegalArgumentException("O usuário dono da meta não pode ser nulo.");
            }
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("O nome da meta não pode ser vazio.");
            }
            if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor total da meta deve ser maior que zero.");
            }

            Meta meta = new Meta(nome, valorTotal, owner);
            owner.addMeta(meta);

            Meta saved = metaRepository.save(meta);

            logger.info("[LOG] Meta criada com sucesso: id={}, nome={}", saved.getId(), saved.getNome());
            return saved;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao criar meta: nome={}, valorTotal={}", nome, valorTotal, e);
            throw e;
        }
    }

    // Listar metas
    public List<Meta> listarMetas(User owner) {
        try {
            if (owner == null) {
                throw new IllegalArgumentException("Usuário não pode ser nulo ao listar metas.");
            }
            logger.info("[LOG] Listando metas do usuário id={}, email={}", owner.getId(), owner.getEmail());
            List<Meta> metasByOwner = metaRepository.findByOwnerAndStatusIn(owner, List.of(StatusMeta.ATIVA, StatusMeta.ATINGIDA));
            logger.info("[LOG] {} metas encontradas para usuário {}", metasByOwner.size(), owner.getEmail());
            return metasByOwner;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao listar metas", e);
            throw e;
        }
    }

    // Listar metas concluídas/arquivadas
    public List<Meta> listarMetasConcluidas(User owner) {
        try {
            if (owner == null) {
                throw new IllegalArgumentException("Usuário não pode ser nulo ao listar metas.");
            }
            logger.info("[LOG] Listando metas concluídas do usuário id={}, email={}", owner.getId(), owner.getEmail());
            List<Meta> metasConcluidas = metaRepository.findByOwnerAndStatusIn(owner, List.of(StatusMeta.CONCLUIDA));
            logger.info("[LOG] {} metas concluídas para usuário {}", metasConcluidas.size(), owner.getEmail());
            return metasConcluidas;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao listar metas", e);
            throw e;
        }
    }

    // Buscar por ID
    public Meta buscarMetaPorId(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID da meta inválido.");
            }
            logger.info("[LOG] Buscando meta por id={}", id);
            return metaRepository.findById(id).orElseThrow(() -> {
                logger.warn("[LOG] Meta não encontrada com id={}", id);
                return new IllegalArgumentException("Meta não encontrada com id: " + id);
            });
        } catch (Exception e) {
            logger.error("[LOG] Erro ao buscar meta id={}", id, e);
            throw e;
        }
    }

    // Adicionar integrante
    @Transactional
    public void adicionarIntegrante(Long metaId, String nomeIntegrante, BigDecimal contribuicaoInicial) {
        try {
            logger.info("[LOG] Adicionando integrante '{}' na meta id={} com contribuição inicial={}",
                    nomeIntegrante, metaId, contribuicaoInicial);

            if (nomeIntegrante == null || nomeIntegrante.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do integrante não pode ser vazio.");
            }
            if (contribuicaoInicial != null && contribuicaoInicial.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Contribuição inicial não pode ser negativa.");
            }

            Meta meta = buscarMetaPorId(metaId);

            // Verificar se integrante já existe
            boolean exists = meta.getIntegrantes().stream()
                    .anyMatch(i -> i.getNome().equalsIgnoreCase(nomeIntegrante));
            if (exists) {
                throw new IllegalArgumentException("Já existe um integrante com este nome na meta.");
            }

            Integrante integrante = new Integrante(nomeIntegrante, BigDecimal.ZERO);
            integrante.setMeta(meta);
            meta.adicionarIntegrante(integrante);

            if (contribuicaoInicial != null && contribuicaoInicial.compareTo(BigDecimal.ZERO) > 0) {
                meta.adicionarContribuicaoIntegrante(nomeIntegrante, contribuicaoInicial);
            }

            metaRepository.save(meta);
            logger.info("[LOG] Integrante '{}' adicionado com sucesso na meta id={}", nomeIntegrante, metaId);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao adicionar integrante '{}' na meta id={}", nomeIntegrante, metaId, e);
            throw e;
        }
    }

    // Contribuir
    @Transactional
    public void contribuir(Long metaId, String nomeIntegrante, BigDecimal valor) {
        try {
            logger.info("[LOG] Registrando contribuição metaId={}, integrante='{}', valor={}", metaId, nomeIntegrante, valor);

            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O valor da contribuição deve ser maior que zero.");
            }

            Meta meta = buscarMetaPorId(metaId);

            // Verificar se integrante existe antes de contribuir
            boolean integranteExiste = meta.getIntegrantes().stream()
                    .anyMatch(i -> i.getNome().equalsIgnoreCase(nomeIntegrante));
            if (!integranteExiste) {
                throw new IllegalArgumentException("Integrante não encontrado na meta.");
            }

            meta.adicionarContribuicaoIntegrante(nomeIntegrante, valor);
            metaRepository.save(meta);

            logger.info("[LOG] Contribuição registrada com sucesso para integrante='{}', metaId={}", nomeIntegrante, metaId);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao registrar contribuição: integrante='{}', metaId={}, valor={}", nomeIntegrante, metaId, valor, e);
            throw e;
        }
    }

    // Relatório de contribuições
    public List<String> relatorioContribuicoes(Long metaId) {
        try {
            logger.info("[LOG] Gerando relatório da meta id={}", metaId);
            Meta meta = buscarMetaPorId(metaId);
            if (meta == null) {
                throw new IllegalArgumentException("Meta não encontrada para gerar relatório.");
            }
            List<String> relatorio = meta.getRelatorioContribuicoes();
            logger.info("[LOG] Relatório gerado com {} linhas para meta id={}", relatorio.size(), metaId);
            return relatorio;
        } catch (Exception e) {
            logger.error("[LOG] Erro ao gerar relatório da meta id={}", metaId, e);
            throw e;
        }
    }

    // Concluir meta
    @Transactional
    public void concluirMeta(Long id) {
        try {
            logger.info("[LOG] Concluindo meta id={}", id);
            Meta meta = buscarMetaPorId(id);
            meta.concluirMeta();
            logger.info("[LOG] Meta id={} concluída com sucesso", id);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao concluir meta id={}", id);
        }
    }

    // Excluir meta
    @Transactional
    public void excluirMeta(Long id) {
        try {
            logger.info("[LOG] Excluindo meta id={}", id);
            Meta meta = buscarMetaPorId(id);
            metaRepository.delete(meta);
            logger.info("[LOG] Meta id={} excluída com sucesso", id);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao excluir meta id={}", id, e);
            throw e;
        }
    }
}
