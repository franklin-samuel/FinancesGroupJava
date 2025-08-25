package com.metas.meta_financeira.controller;

import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.Moeda;
import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.services.CertificadoService;
import com.metas.meta_financeira.services.MetaService;
import com.metas.meta_financeira.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@Controller
@RequestMapping("/meta")
public class MetaViewController {

    private static final Logger log = LoggerFactory.getLogger(MetaViewController.class);

    private final MetaService metaService;
    private final UserService userService;
    private final CertificadoService certificadoService;

    public MetaViewController(MetaService metaService, UserService userService, CertificadoService certificadoService) {
        this.metaService = metaService;
        this.userService = userService;
        this.certificadoService = certificadoService;
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
            return "error";
        }
    }

    // Página de histórico(arquivadas)
    @GetMapping("/arquivadas")
    public String arquivadas(Model model, @AuthenticationPrincipal OAuth2User principal) {
        try {
            log.info("[LOG] Iniciando listagem de metas arquivadas");
            User user = extrairOuCriarUser(principal);
            model.addAttribute("metasConcluidas", metaService.listarMetasConcluidas(user));
            log.info("[LOG] Listagem de metas concluídas para usuário {}", user.getEmail());
            return "metas-arquivadas";
        } catch (Exception e) {
            log.error("[LOG] Erro ao listar metas concluídas", e);
            return "error";
        }
    }

    // Concluir meta -> Mostrar página de parabéns
    @PostMapping("/{id}/concluir")
    public String concluirMeta(@PathVariable Long id) {
        try {
            log.info("[LOG] Concluindo meta {}...", id);
            metaService.concluirMeta(id);
            return "redirect:/meta/" + id + "?concluida=true";
        } catch (Exception e) {
            log.error("Não foi possível concluír a meta {}", id);
            throw e;
        }
    }

    // Criar nova meta
    @PostMapping("/criarMeta")
    public String criarMeta(
            @RequestParam String nome,
            @RequestParam BigDecimal valorTotal,
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

            // Valores formatados
            model.addAttribute("meta", meta);
            model.addAttribute("valorTotal", new Moeda(meta.getValorTotal()).formatado());
            model.addAttribute("valorAtual", new Moeda(meta.getValorAtual()).formatado());
            model.addAttribute("restante", new Moeda(meta.restanteFaltante()).formatado());

            // Contribuições individuais formatadas
            List<String> relatorioFormatado = meta.getIntegrantes().stream()
                    .map(i -> String.format("%s contribuiu %s (%.2f%% da meta)",
                            i.getNome(),
                            new Moeda(i.getContribuicao()).formatado(),
                            i.getPercentualDaMeta(meta.getValorTotal()).doubleValue()))
                    .collect(Collectors.toList());

            model.addAttribute("relatorio", relatorioFormatado);

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
            @RequestParam BigDecimal contribuicaoInicial
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
            @RequestParam BigDecimal valor
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

    @GetMapping("/{id}/certificado")
    public ResponseEntity<byte[]> gerarCertificado(@PathVariable Long id) {
        try {
            Meta meta = metaService.buscarMetaPorId(id);
            byte[] certificado = certificadoService.gerarCertificado(meta);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado_" + meta.getNome() + ".jpg")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(certificado);
        } catch (Exception e) {
            log.error("[LOG] Erro ao gerar certificado da meta id={}", id, e);
            return ResponseEntity.internalServerError().build();
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

    // Verifica se usuário está autenticado
    public boolean isAuthenticated(OAuth2User principal) {
        return principal != null;
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
