package br.com.cafenamao.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlugTest {

    @Test
    void geraSlugSemAcentos() {
        assertEquals("cafe-gelado", Slug.de("Café Gelado"));
        assertEquals("cha-de-canela", Slug.de("Chá de Canela"));
    }

    @Test
    void normalizaEspacosESimbolos() {
        assertEquals("espresso-curto", Slug.de("  Espresso   Curto  "));
        assertEquals("mocha-especial", Slug.de("Mocha & Especial!"));
    }

    @Test
    void rejeitaTextoNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> Slug.de(null));
        assertThrows(IllegalArgumentException.class, () -> Slug.de("   "));
    }

    @Test
    void rejeitaTextoSemLetrasOuNumeros() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> Slug.de("!!!"));

        assertEquals("Não foi possível gerar um identificador a partir do nome informado.",
                erro.getMessage());
    }
}
