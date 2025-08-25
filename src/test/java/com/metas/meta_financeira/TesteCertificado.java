package com.metas.meta_financeira;

import com.metas.meta_financeira.models.Integrante;
import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.User;
import com.metas.meta_financeira.services.CertificadoService;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class TesteCertificado {
    public static void main(String[] args) throws Exception {
        User user = new User();
        user.setEmail("teste@teste.com");

        Meta meta = new Meta("Meta de Teste", new BigDecimal("1234.56"), user);
        meta.setCriadaEm(LocalDateTime.now().minusDays(10));
        meta.setConcluidaEm(LocalDateTime.now());

        Integrante i1 = new Integrante("Alice", BigDecimal.ZERO);
        Integrante i2 = new Integrante("Bob", BigDecimal.ZERO);
        meta.setIntegrantes(Arrays.asList(i1, i2));

        CertificadoService service = new CertificadoService();
        byte[] bytes = service.gerarCertificado(meta);

        try (FileOutputStream fos = new FileOutputStream("certificado-teste.jpg")) {
            fos.write(bytes);
        }

        System.out.println("Certificado gerado: certificado-teste.jpg");
    }
}