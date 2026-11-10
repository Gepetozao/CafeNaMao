package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemPedidoTest {

    private static Bebida bebida(String id, String preco) {
        return new Bebida(id, "Bebida " + id, "Descrição", "Café", new BigDecimal(preco));
    }

    @Test
    void calculaSubtotalDoItem() {
        // Arrange
        Bebida cappuccino = bebida("cappuccino", "7.50");

        // Act
        ItemPedido item = new ItemPedido(cappuccino, 3);

        // Assert
        assertEquals(new BigDecimal("22.50"), item.subtotal());
    }

    @Test
    void rejeitaBebidaNula() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido(null, 1));

        assertEquals("A bebida do item é obrigatória.", erro.getMessage());
    }

    @Test
    void rejeitaQuantidadeForaDoLimite() {
        Bebida bebida = bebida("espresso-curto", "4.00");

        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(bebida, 0));
        assertThrows(IllegalArgumentException.class, () -> new ItemPedido(bebida, 11));
    }
}
