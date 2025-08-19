package com.metas.meta_financeira.controller;

import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.services.MetaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/meta")
public class MetaViewController {

    private final MetaService metaService;

    public MetaViewController(MetaService metaService) {
        this.metaService = metaService;
    }

    // Listar metas do usuário logado
    @GetMapping
    public String home(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("metas", metaService.listarMetas(user));
        return "index";
    }

    // Criar nova meta
    @PostMapping("/criarMeta")
    public String criarMeta(
            @RequestParam String nome,
            @RequestParam double valorTotal,
            @AuthenticationPrincipal User user
    ) {
        metaService.criarMeta(nome, valorTotal, user);
        return "redirect:/meta";
    }

    // Detalhes da meta
    @GetMapping("/{id}")
    public String detalhesMeta(@PathVariable Long id, Model model) {
        Meta meta = metaService.buscarMetaPorId(id);
        model.addAttribute("meta", meta);
        model.addAttribute("relatorio", metaService.relatorioContribuicoes(id));
        return "detalhes-meta";
    }

    // Adicionar integrante
    @PostMapping("/{id}/adicionarIntegrante")
    public String adicionarIntegrante(
            @PathVariable Long id,
            @RequestParam String nomeIntegrante,
            @RequestParam double contribuicaoInicial
    ) {
        metaService.adicionarIntegrante(id, nomeIntegrante, contribuicaoInicial);
        return "redirect:/meta/" + id;
    }

    // Contribuir
    @PostMapping("/{id}/contribuir")
    public String contribuir(
            @PathVariable Long id,
            @RequestParam String nomeIntegrante,
            @RequestParam double valor
    ) {
        metaService.contribuir(id, nomeIntegrante, valor);
        return "redirect:/meta/" + id;
    }

    // Excluir meta
    @PostMapping("/{id}/excluir")
    public String excluirMeta(@PathVariable Long id) {
        metaService.excluirMeta(id);
        return "redirect:/meta";
    }
}
