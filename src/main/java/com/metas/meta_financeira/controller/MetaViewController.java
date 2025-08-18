package com.metas.meta_financeira.controller;

import org.springframework.ui.Model;
import com.metas.meta_financeira.services.MetaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//Controlador da Visualização
@Controller
@RequestMapping("/meta")
public class MetaViewController {

    private final MetaService metaService;

    public MetaViewController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("metas", metaService.listarMetas());
        return "index";
    }

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

}
