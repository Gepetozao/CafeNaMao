package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormaPagamentoTest {

    @Test
    void converteCodigoValido() {
        assertEquals(Optional.of(FormaPagamento.DINHEIRO), FormaPagamento.deCodigo("dinheiro"));
        assertEquals(Optional.of(FormaPagamento.CARTAO_FICTICIO),
                FormaPagamento.deCodigo("cartao-ficticio"));
    }

    @Test
    void retornaVazioParaCodigoInvalido() {
        assertTrue(FormaPagamento.deCodigo("pix").isEmpty());
        assertTrue(FormaPagamento.deCodigo("DINHEIRO").isEmpty());
    }

    @Test
    void retornaVazioParaCodigoNulo() {
        assertTrue(FormaPagamento.deCodigo(null).isEmpty());
    }
}
