package com.metas.meta_financeira.controller;
import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/meta")
public class MetaController {
    private Meta meta;

    public MetaController() {
        Meta meta = new Meta("Viagem para praia", 2000);
        meta.adicionarIntegrante(new Integrante("Samuel", 500));
        meta.adicionarIntegrante(new Integrante("Maria", 250));
    }

    @GetMapping
    public String verMeta(Model model) {

    }

    @PostMapping
    public String contribuir(@RequestParam String nomeIntegrante, @RequestParam double valorContribuicao) {
            
    }
}
