package com.metas.meta_financeira.controller;

import com.metas.meta_financeira.models.Meta;
import org.springframework.ui.Model;
import com.metas.meta_financeira.services.MetaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//Controlador da Visualização
@Controller
@RequestMapping("/meta")
public class MetaViewController {

    private final MetaService metaService;

    public MetaViewController(MetaService metaService) {
        this.metaService = metaService;
    }

    //Página inicial com lista de metas
    @GetMapping
    public String home(Model model) {
        model.addAttribute("metas", metaService.listarMetas());
        return "index";
    }

    //Criar Meta
    @PostMapping("/criarMeta")
    public String criarMeta(
            @RequestParam String nome,
            @RequestParam double valorTotal,
            Model model
    ) {
        metaService.criarMeta(nome, valorTotal);
        model.addAttribute("metas", metaService.listarMetas());
        return "index";
    }


    //Detalhes de uma meta por Id
    @GetMapping("/{id}")
    public String detalhesMeta(@PathVariable Long id, Model model) {
        Meta meta = metaService.buscarMetaPorId(id);
        model.addAttribute("meta", meta);
        model.addAttribute("relatorio", meta.getRelatorioContribuicoes());
        return "detalhes-meta";
    }

    //Adicionar integrante a uma meta por Id
    @PostMapping("/{id}/add-integrante")
    public String adicionarIntegrante(
            @PathVariable Long id,
            @RequestParam String nomeIntegrante,
            @RequestParam double contribuicaoInicial
    ) {
        Meta meta = metaService.buscarMetaPorId(id);
        metaService.adicionarIntegrante(id, nomeIntegrante, contribuicaoInicial);
        return "redirect:/meta/" + id;
    }

    //Contribuir em uma meta
    @PostMapping("/{id}/contribuir")
    public String contribuir(
            @PathVariable Long id,
            @RequestParam String nomeIntegrante,
            @RequestParam double valor
    ) {
        Meta meta = metaService.buscarMetaPorId(id);
        metaService.contribuir(id, nomeIntegrante, valor);
        return "redirect:/meta/" + id;
    }

    //Excluir uma meta
    @PostMapping("/{id}/excluir")
    public String excluirMeta(@PathVariable Long id) {
        metaService.excluirMeta(id);
        return "redirect:/meta";
    }

}
