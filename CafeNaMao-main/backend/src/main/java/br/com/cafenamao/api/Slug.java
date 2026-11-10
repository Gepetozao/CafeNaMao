package br.com.cafenamao.api;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Gera identificadores amigáveis (slugs) a partir de nomes.
 *
 * <p>Exemplo: {@code "Café Gelado"} vira {@code "cafe-gelado"}.</p>
 */
public final class Slug {

    private Slug() {
    }

    public static String de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("O texto para gerar o identificador é obrigatório.");
        }

        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = semAcento.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if (slug.isBlank()) {
            throw new IllegalArgumentException(
                    "Não foi possível gerar um identificador a partir do nome informado.");
        }

        return slug;
    }
}
