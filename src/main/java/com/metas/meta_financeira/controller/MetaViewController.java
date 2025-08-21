package com.metas.meta_financeira.controller;

import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.services.MetaService;
import com.metas.meta_financeira.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/meta")
public class MetaViewController {

    private static final Logger log = LoggerFactory.getLogger(MetaViewController.class);

    private final MetaService metaService;
    private final UserService userService;

    public MetaViewController(MetaService metaService, UserService userService) {
        this.metaService = metaService;
        this.userService = userService;
    }

    // Listar metas do usuário logado
    @GetMapping
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        try {
            log.info("[LOG] Iniciando listagem de metas");
            User user = extrairOuCriarUser(principal);
            model.addAttribute("metas", metaService.listarMetas(user));
            log.info("[LOG] Listagem de metas concluída com sucesso para o usuário {}", user.getEmail());
            return "index";
        } catch (Exception e) {
            log.error("[LOG] Erro ao listar metas", e);
            return "error"; // página de erro genérica
        }
    }

    // Criar nova meta
    @PostMapping("/criarMeta")
    public String criarMeta(
            @RequestParam String nome,
            @RequestParam double valorTotal,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {
            log.info("[LOG] Criando nova meta: nome={} valorTotal={}", nome, valorTotal);
            User user = extrairOuCriarUser(principal);
            metaService.criarMeta(nome, valorTotal, user);
            log.info("[LOG] Meta criada com sucesso para usuário {}", user.getEmail());
            return "redirect:/";
        } catch (Exception e) {
            log.error("[LOG] Erro ao criar meta: nome={} valorTotal={}", nome, valorTotal, e);
            return "error";
        }
    }

    // Detalhes da meta
    @GetMapping("/{id}")
    public String detalhesMeta(@PathVariable Long id, Model model) {
        try {
            log.info("[LOG] Buscando detalhes da meta id={}", id);
            Meta meta = metaService.buscarMetaPorId(id);
            model.addAttribute("meta", meta);
            model.addAttribute("relatorio", metaService.relatorioContribuicoes(id));
            log.info("[LOG] Detalhes da meta id={} carregados com sucesso", id);
            return "detalhes-meta";
        } catch (Exception e) {
            log.error("[LOG] Erro ao buscar detalhes da meta id={}", id, e);
            return "error";
        }
    }

    // Adicionar integrante
    @PostMapping("/{id}/adicionarIntegrante")
    public String adicionarIntegrante(
            @PathVariable Long id,
            @RequestParam String nomeIntegrante,
            @RequestParam double contribuicaoInicial
    ) {
        try {
            log.info("[LOG] Adicionando integrante '{}' na meta id={} com contribuição inicial={}", nomeIntegrante, id, contribuicaoInicial);
            metaService.adicionarIntegrante(id, nomeIntegrante, contribuicaoInicial);
            log.info("[LOG] Integrante '{}' adicionado com sucesso na meta id={}", nomeIntegrante, id);
            return "redirect:/meta/" + id;
        } catch (Exception e) {
            log.error("[LOG] Erro ao adicionar integrante '{}' na meta id={}", nomeIntegrante, id, e);
            return "error";
        }
    }

    // Contribuir
    @PostMapping("/{id}/contribuir")
    public String contribuir(
            @PathVariable Long id,
            @RequestParam String nomeIntegrante,
            @RequestParam double valor
    ) {
        try {
            log.info("[LOG] Contribuição de valor={} pelo integrante '{}' na meta id={}", valor, nomeIntegrante, id);
            metaService.contribuir(id, nomeIntegrante, valor);
            log.info("[LOG] Contribuição registrada com sucesso na meta id={}", id);
            return "redirect:/meta/" + id;
        } catch (Exception e) {
            log.error("[LOG] Erro ao registrar contribuição de '{}' na meta id={}", nomeIntegrante, id, e);
            return "error";
        }
    }

    // Excluir meta
    @PostMapping("/{id}/excluir")
    public String excluirMeta(@PathVariable Long id) {
        try {
            log.info("[LOG] Excluindo meta id={}", id);
            metaService.excluirMeta(id);
            log.info("[LOG] Meta id={} excluída com sucesso", id);
            return "redirect:/";
        } catch (Exception e) {
            log.error("[LOG] Erro ao excluir meta id={}", id, e);
            return "error";
        }
    }

    public boolean isAuthenticated(OAuth2User principal) {
        if (principal != null) {
            return true;
        } else {
            return false;
        }
    }

    private User extrairOuCriarUser(OAuth2User principal) {
        try {
            log.debug("[LOG] Extraindo dados do usuário OAuth2");
            String oauthId = principal.getAttribute("sub");
            String email = principal.getAttribute("email");
            String name = principal.getAttribute("name");
            String picture = principal.getAttribute("picture");

            User user = userService.buscarOuCriar(oauthId, email, name, picture);
            log.debug("[LOG] Usuário extraído/criado com sucesso: {}", email);
            return user;
        } catch (Exception e) {
            log.error("[LOG] Erro ao extrair ou criar usuário OAuth2", e);
            throw e; // relança para ser tratado nos métodos chamadores
        }
    }
}
