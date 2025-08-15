package com.metas.meta_financeira.controller;

import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.services.MetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/meta")
public class MetaController {

    private static final Logger logger = LoggerFactory.getLogger(MetaController.class);
    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping
    public ResponseEntity<?> listarMetas() {
        logger.info("[LOG] Requisição recebida para listar metas");
        try {
            List<Meta> metas = metaService.listarMetas();
            logger.info("[LOG] Total de metas encontradas: {}", metas.size());
            return ResponseEntity.ok(metas);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao listar metas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao listar metas.");
        }
    }

    @PostMapping
    public ResponseEntity<?> criarMeta(@RequestParam String nome, @RequestParam double valorTotal) {
        logger.info("[LOG] Requisição recebida para criar meta: nome={}, valorTotal={}", nome, valorTotal);
        try {
            Meta meta = metaService.criarMeta(nome, valorTotal);
            logger.info("[LOG] Meta criada com sucesso: {}", meta);
            return ResponseEntity.status(HttpStatus.CREATED).body(meta);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao criar meta", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao criar meta: " + e.getMessage());
        }
    }

    @GetMapping("/{nomeMeta}")
    public ResponseEntity<?> buscarMetaPorNome(@PathVariable String nomeMeta) {
        logger.info("[LOG] Requisição recebida para buscar meta: {}", nomeMeta);
        try {
            Meta meta = metaService.buscarMetaPorNome(nomeMeta);
            logger.info("[LOG] Meta encontrada: {}", meta);
            return ResponseEntity.ok(meta);
        } catch (IllegalArgumentException e) {
            logger.warn("[LOG] Meta não encontrada: {}", nomeMeta);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("[LOG] Erro ao buscar meta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao buscar meta.");
        }
    }

    @PostMapping("/{nomeMeta}/integrante")
    public ResponseEntity<?> adicionarIntegranteNaMeta(
            @PathVariable String nomeMeta,
            @RequestParam String nomeIntegrante,
            @RequestParam double valorInicial
    ) {
        logger.info("[LOG] Requisição recebida para adicionar integrante '{}' na meta '{}'", nomeIntegrante, nomeMeta);
        try {
            metaService.adicionarIntegrante(nomeMeta, nomeIntegrante, valorInicial);
            logger.info("[LOG] Integrante '{}' adicionado com sucesso à meta '{}'", nomeIntegrante, nomeMeta);
            return ResponseEntity.ok("Integrante adicionado com sucesso!");
        } catch (Exception e) {
            logger.error("[LOG] Erro ao adicionar integrante na meta", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao adicionar integrante: " + e.getMessage());
        }
    }

    @PostMapping("/{nomeMeta}/contribuir")
    public ResponseEntity<?> contribuir(
            @PathVariable String nomeMeta,
            @RequestParam String nomeIntegrante,
            @RequestParam double valorContribuicao
    ) {
        logger.info("[LOG] Requisição recebida para contribuição: meta='{}', integrante='{}', valor={}",
                nomeMeta, nomeIntegrante, valorContribuicao);
        try {
            metaService.contribuir(nomeMeta, nomeIntegrante, valorContribuicao);
            logger.info("[LOG] Contribuição registrada com sucesso");
            return ResponseEntity.ok("Contribuição adicionada.");
        } catch (Exception e) {
            logger.error("[LOG] Erro ao registrar contribuição", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao registrar contribuição: " + e.getMessage());
        }
    }

    @GetMapping("/{nomeMeta}/relatorio")
    public ResponseEntity<?> relatorioContribuicao(@PathVariable String nomeMeta) {
        logger.info("[LOG] Requisição recebida para gerar relatório da meta '{}'", nomeMeta);
        try {
            List<String> relatorio = metaService.relatorioContribuicoes(nomeMeta);
            logger.info("[LOG] Relatório gerado com {} linhas", relatorio.size());
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            logger.error("[LOG] Erro ao gerar relatório", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @DeleteMapping("/{nomeMeta}")
    public ResponseEntity<?> excluirMeta(@PathVariable String nomeMeta) {
        logger.info("[LOG] Requisição recebida para excluir meta '{}'", nomeMeta);
        try {
            metaService.excluirMeta(nomeMeta);
            logger.info("[LOG] Meta '{}' excluída com sucesso", nomeMeta);
            return ResponseEntity.ok("Meta excluída com sucesso.");
        } catch (IllegalArgumentException e) {
            logger.warn("[LOG] Tentativa de excluir meta inexistente: {}", nomeMeta);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("[LOG] Erro ao excluir meta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao excluir meta.");
        }
    }
}
