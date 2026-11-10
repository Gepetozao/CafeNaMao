package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusPedidoTest {

    @Test
    void converteCodigoValido() {
        assertEquals(Optional.of(StatusPedido.RECEBIDO), StatusPedido.deCodigo("recebido"));
        assertEquals(Optional.of(StatusPedido.EM_PREPARO), StatusPedido.deCodigo("em-preparo"));
        assertEquals(Optional.of(StatusPedido.FINALIZADO), StatusPedido.deCodigo("finalizado"));
    }

    @Test
    void retornaVazioParaCodigoInvalido() {
        assertTrue(StatusPedido.deCodigo("pronto").isEmpty());
        assertTrue(StatusPedido.deCodigo("RECEBIDO").isEmpty());
    }

    @Test
    void retornaVazioParaCodigoNulo() {
        assertTrue(StatusPedido.deCodigo(null).isEmpty());
    }
}
