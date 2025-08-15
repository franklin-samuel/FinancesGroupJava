package com.metas.meta_financeira.controller;
import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.metas.meta_financeira.services.MetaService;

import java.util.List;

@Controller
@RequestMapping("/meta")
public class MetaController {
    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping("/meta")
    public List<Meta> listarMetas() {
        return metaService.listarMetas();
    }


    @PostMapping("/meta")
    public Meta criarMeta(@RequestParam String nome, @RequestParam double valorTotal) {
        return metaService.criarMeta(nome, valorTotal);
    }

    @GetMapping("/meta/{nomeMeta}")
    public Meta buscarMetaPorNome(@RequestParam String nome) {
        return metaService.buscarMetaPorNome(nome);
    }

    @PostMapping("/{nomeMeta}/integrante")
    public ResponseEntity<String> adicionarIntegranteNaMeta(
            @PathVariable String nomeMeta,
            @RequestParam String nomeIntegrante,
            @RequestParam double valorInicial
    ) {
        metaService.adicionarIntegrante(nomeMeta, nomeIntegrante, valorInicial);
        return ResponseEntity.ok("Integrante adicionado com sucesso!");
    }

    @PostMapping("/{nomeMeta}/contribuir")
    public ResponseEntity<String> contribuir(
            @PathVariable String nomeMeta,
            @RequestParam String nomeIntegrante,
            @RequestParam double valorContribuicao
    ) {
        metaService.contribuir(nomeMeta, nomeIntegrante, valorContribuicao);
        return ResponseEntity.ok("Contribuição adicionada.");
    }

    @GetMapping("/{nomeMeta}/relatorio")
    public ResponseEntity<List<String>> relatorioContribuicao(@PathVariable String nomeMeta) {
        List<String> relatorio = metaService.relatorioContribuicoes(nomeMeta);
        return ResponseEntity.ok(relatorio);
    }

    @DeleteMapping("/{nomeMeta}")
    public ResponseEntity<String> excluirMeta(@PathVariable String nomeMeta) {
        metaService.excluirMeta(nomeMeta);
        return ResponseEntity.ok("Meta excluída");
    }

}
