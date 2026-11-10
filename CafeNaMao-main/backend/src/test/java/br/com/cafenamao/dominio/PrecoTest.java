package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrecoTest {

    @Test
    void normalizaPrecoParaDuasCasas() {
        // Arrange
        BigDecimal valor = new BigDecimal("4");

        // Act
        BigDecimal preco = Preco.validar(valor);

        // Assert
        assertEquals(new BigDecimal("4.00"), preco);
        assertEquals(2, preco.scale());
    }

    @Test
    void rejeitaPrecoNulo() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> Preco.validar(null));

        assertEquals("O preço é obrigatório.", erro.getMessage());
    }

    @Test
    void rejeitaPrecoNegativo() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> Preco.validar(new BigDecimal("-0.01")));

        assertEquals("O preço não pode ser negativo.", erro.getMessage());
    }

    @Test
    void rejeitaPrecoComMaisDeDuasCasas() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> Preco.validar(new BigDecimal("4.001")));

        assertEquals("O preço deve ter no máximo duas casas decimais.", erro.getMessage());
    }

    @Test
    void somaValoresEmCentavos() {
        // Arrange
        BigDecimal primeiro = new BigDecimal("4.00");
        BigDecimal segundo = new BigDecimal("5.50");

        // Act
        BigDecimal total = Preco.somar(primeiro, segundo);

        // Assert
        assertEquals(new BigDecimal("9.50"), total);
    }

    @Test
    void multiplicaPrecoPelaQuantidade() {
        // Arrange
        BigDecimal preco = new BigDecimal("7.50");

        // Act
        BigDecimal total = Preco.multiplicar(preco, 3);

        // Assert
        assertEquals(new BigDecimal("22.50"), total);
    }

    @Test
    void converteCentavosParaPreco() {
        // Act
        BigDecimal preco = Preco.deCentavos(750);

        // Assert
        assertEquals(new BigDecimal("7.50"), preco);
        assertEquals(2, preco.scale());
    }

    @Test
    void convertePrecoParaCentavos() {
        // Act
        long centavos = Preco.paraCentavos(new BigDecimal("7.50"));

        // Assert
        assertEquals(750L, centavos);
    }

    @Test
    void rejeitaCentavosNegativos() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> Preco.deCentavos(-1));

        assertEquals("O preço não pode ser negativo.", erro.getMessage());
    }
}
