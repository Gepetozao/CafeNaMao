package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BebidaTest {

    @Test
    void criaBebidaValida() {
        // Arrange
        String id = "cappuccino";
        String nome = "Cappuccino";

        // Act
        Bebida bebida = new Bebida(id, nome,
                "Café, leite vaporizado e espuma.", "Café", new BigDecimal("7.50"));

        // Assert
        assertEquals(id, bebida.id());
        assertEquals(nome, bebida.nome());
        assertEquals(new BigDecimal("7.50"), bebida.preco());
    }

    @Test
    void rejeitaIdentificadorVazio() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Bebida("  ", "Cappuccino", "Descrição", "Café", new BigDecimal("7.50")));

        assertEquals("O identificador da bebida é obrigatório.", erro.getMessage());
    }

    @Test
    void rejeitaNomeVazio() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Bebida("cappuccino", "", "Descrição", "Café", new BigDecimal("7.50")));

        assertEquals("O nome da bebida é obrigatório.", erro.getMessage());
    }

    @Test
    void rejeitaDescricaoVazia() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Bebida("cappuccino", "Cappuccino", null, "Café", new BigDecimal("7.50")));

        assertEquals("A descrição da bebida é obrigatória.", erro.getMessage());
    }

    @Test
    void rejeitaCategoriaVazia() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Bebida("cappuccino", "Cappuccino", "Descrição", " ", new BigDecimal("7.50")));

        assertEquals("A categoria da bebida é obrigatória.", erro.getMessage());
    }

    @Test
    void rejeitaPrecoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Bebida("cappuccino", "Cappuccino", "Descrição", "Café", null));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Bebida("cappuccino", "Cappuccino", "Descrição", "Café",
                        new BigDecimal("-1.00")));
    }
}
