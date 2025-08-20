package com.metas.meta_financeira;

import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.repositories.UserRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MetaViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRespository userRepository;

    private TestingAuthenticationToken auth;

    @BeforeEach
    void setup() {
        // limpa banco em memória (H2)
        userRepository.deleteAll();

        // simula usuário OAuth2
        Map<String, Object> attributes = Map.of(
                "sub", "123456",
                "email", "teste@teste.com",
                "name", "Usuário Teste",
                "picture", "http://fake.com/img.png"
        );
        DefaultOAuth2User oAuth2User = new DefaultOAuth2User(
                Set.of(new OAuth2UserAuthority(attributes)),
                attributes,
                "sub"
        );
        auth = new TestingAuthenticationToken(oAuth2User, null);
    }

    @Test
    void deveCriarListarEExcluirMetaComSucesso() throws Exception {
        // 1. Criar meta
        mockMvc.perform(post("/meta/criarMeta")
                        .param("nome", "Meta Teste")
                        .param("valorTotal", "1000")
                        .with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meta"));

        // 2. Listar metas (espera ver a página index)
        mockMvc.perform(get("/meta").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("metas"));

        // Pega o usuário criado
        User user = userRepository.findByEmail("teste@teste.com").orElseThrow();
        Long metaId = user.getMetas().get(0).getId();

        // 3. Buscar detalhes da meta
        mockMvc.perform(get("/meta/" + metaId).with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("detalhes-meta"))
                .andExpect(model().attributeExists("meta"))
                .andExpect(model().attributeExists("relatorio"));

        // 4. Adicionar integrante
        mockMvc.perform(post("/meta/" + metaId + "/adicionarIntegrante")
                        .param("nomeIntegrante", "João")
                        .param("contribuicaoInicial", "200")
                        .with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meta/" + metaId));

        // 5. Contribuir
        mockMvc.perform(post("/meta/" + metaId + "/contribuir")
                        .param("nomeIntegrante", "João")
                        .param("valor", "150")
                        .with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meta/" + metaId));

        // 6. Excluir meta
        mockMvc.perform(post("/meta/" + metaId + "/excluir").with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/meta"));
    }
}
