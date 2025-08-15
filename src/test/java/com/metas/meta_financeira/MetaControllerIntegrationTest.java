package com.metas.meta_financeira;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metas.meta_financeira.models.Meta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MetaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCriarAndListarMetas() throws Exception {
        //Criar Meta
        mockMvc.perform(post("/meta")
                    .param("nome", "Viagem")
                    .param("valorTotal", "1000"))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Viagem")));

        //Listar Metas
        mockMvc.perform(get("/meta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Viagem"));
    }

    @Test
    void testBuscarMetaPorNome() throws Exception {
        //Criar Meta
        mockMvc.perform(post("/meta")
                        .param("nome", "Curso")
                        .param("valorTotal", "500"))
                .andExpect(status().isCreated());

        //BuscarMeta
        mockMvc.perform(get("/meta/{nomeMeta}", "Curso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Curso"))
                .andExpect(jsonPath("$.valorTotal").value(500));
    }

    @Test
    void testAdicionarIntegranteEContribuir() throws Exception {
        // Criar meta
        mockMvc.perform(post("/meta")
                        .param("nome", "Festa")
                        .param("valorTotal", "2000"))
                .andExpect(status().isCreated());

        // Adicionar integrante
        mockMvc.perform(post("/meta/{nomeMeta}/integrante", "Festa")
                        .param("nomeIntegrante", "Samuel")
                        .param("valorInicial", "300"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Integrante adicionado")));

        // Contribuir
        mockMvc.perform(post("/meta/{nomeMeta}/contribuir", "Festa")
                        .param("nomeIntegrante", "Samuel")
                        .param("valorContribuicao", "200"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Contribuição adicionada")));

        // Relatório
        mockMvc.perform(get("/meta/{nomeMeta}/relatorio", "Festa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(containsString("Samuel")));
    }

    @Test
    void testExcluirMeta() throws Exception {
        // Criar meta
        mockMvc.perform(post("/meta")
                        .param("nome", "Viagem")
                        .param("valorTotal", "1000"))
                .andExpect(status().isCreated());

        // Excluir meta
        mockMvc.perform(delete("/meta/{nomeMeta}", "Viagem"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Meta excluída")));

        // Verificar se meta foi realmente excluída
        mockMvc.perform(get("/meta/{nomeMeta}", "Viagem"))
                .andExpect(status().isNotFound());
    }
}
