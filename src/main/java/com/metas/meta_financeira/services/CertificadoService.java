package com.metas.meta_financeira.services;

import com.metas.meta_financeira.models.Meta;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CertificadoService {

    public static final String TEMPLATE_PATH = "FinTrack.jpg";

    public byte[] gerarCertificado(Meta meta) throws IOException {
        BufferedImage template = ImageIO.read(
                getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH)
        );
        Graphics2D g2d = template.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);

        Font fonteValor = new Font("SansSerif", Font.BOLD, 165);
        Font fonteNome = new Font("SansSerif", Font.BOLD, 85);
        Font fonteIntegrantes = new Font("SansSerif", Font.PLAIN, 30);
        Font fonteDatas = new Font("SansSerif", Font.PLAIN, 28);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String dataCriacao = meta.getCriadaEm().format(formatter);
        String dataConclusao = meta.getConcluidaEm().format(formatter);

        // Valor da meta
        g2d.setFont(fonteValor);
        String valor = "R$ " + String.format("%,.2f", meta.getValorTotal());
        desenharCentralizado(g2d, valor, template.getWidth(), 525);

        // Nome da meta
        g2d.setFont(fonteNome);
        desenharCentralizado(g2d, meta.getNome(), template.getWidth(), 670);

        // Lista de integrantes
        g2d.setFont(fonteIntegrantes);
        List<String> nomes = meta.getIntegrantes().stream().map(i -> i.getNome()).toList();
        String integrantes = String.join(", ", nomes);
        desenharCentralizado(g2d, integrantes, template.getWidth(), 860);

        // Datas
        g2d.setFont(fonteDatas);
        g2d.drawString(dataCriacao, 650, template.getHeight() - 100);
        g2d.drawString(dataConclusao, 1250, template.getHeight() - 100);

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(template, "jpg", baos);
        return baos.toByteArray();

    }

    private void desenharCentralizado(Graphics2D g2d, String texto, int larguraTotal, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int larguraTexto = fm.stringWidth(texto);
        int x = (larguraTotal - larguraTexto) / 2;
        g2d.drawString(texto, x, y);
    }
}
