package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegraQuantidadeTest {

    @Test
    void aceitaOsValoresLimite() {
        assertDoesNotThrow(() -> RegraQuantidade.validar(RegraQuantidade.MINIMA));
        assertDoesNotThrow(() -> RegraQuantidade.validar(RegraQuantidade.MAXIMA));
        assertTrue(RegraQuantidade.valida(1));
        assertTrue(RegraQuantidade.valida(10));
    }

    @Test
    void rejeitaQuantidadeAbaixoDoMinimo() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> RegraQuantidade.validar(0));

        assertEquals("A quantidade mínima é 1.", erro.getMessage());
        assertFalse(RegraQuantidade.valida(0));
        assertFalse(RegraQuantidade.valida(-1));
    }

    @Test
    void rejeitaQuantidadeAcimaDoMaximo() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> RegraQuantidade.validar(11));

        assertEquals("A quantidade máxima é 10.", erro.getMessage());
        assertFalse(RegraQuantidade.valida(11));
    }
}
