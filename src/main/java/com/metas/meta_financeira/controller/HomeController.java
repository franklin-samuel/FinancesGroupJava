package com.metas.meta_financeira.controller;

import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.services.MetaService;
import com.metas.meta_financeira.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final MetaService metaService;
    private final UserService userService;

    public HomeController(MetaService metaService, UserService userService) {
        this.metaService = metaService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        try {
            // Verifica se o usuário está logado
            if (principal != null) {
                log.info("[LOG] Usuário autenticado acessando página inicial");
                User user = extrairOuCriarUser(principal);

                // Adiciona dados do usuário logado
                model.addAttribute("isAuthenticated", true);
                model.addAttribute("user", user);
                model.addAttribute("metas", metaService.listarMetas(user));

                log.info("[LOG] Dados carregados para usuário autenticado: {}", user.getEmail());
            } else {
                log.info("[LOG] Usuário não autenticado acessando página inicial");
                model.addAttribute("isAuthenticated", false);
            }

            return "index";
        } catch (Exception e) {
            log.error("[LOG] Erro ao carregar página inicial", e);
            model.addAttribute("error", "Erro ao carregar a página");
            return "index";
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
            throw e;
        }
    }
}