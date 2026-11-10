package br.com.cafenamao.dominio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoTest {

    private static final LocalDateTime DATA_FIXA = LocalDateTime.of(2026, 11, 9, 10, 30);

    private static Bebida bebida(String id, String preco) {
        return new Bebida(id, "Bebida " + id, "Descrição", "Café", new BigDecimal(preco));
    }

    private static ItemPedido item(String id, String preco, int quantidade) {
        return new ItemPedido(bebida(id, preco), quantidade);
    }

    @Test
    void calculaTotalComItensDiferentes() {
        // Arrange
        List<ItemPedido> itens = List.of(
                item("espresso-curto", "4.00", 3),
                item("cafe-com-leite", "5.50", 2));

        // Act
        Pedido pedido = new Pedido("PED-0001", DATA_FIXA, itens, FormaPagamento.DINHEIRO,
                StatusPedido.RECEBIDO);

        // Assert
        assertEquals(new BigDecimal("23.00"), pedido.total());
    }

    @Test
    void iniciaComStatusRecebidoENumeroGerado() {
        // Arrange
        List<ItemPedido> itens = List.of(item("cappuccino", "7.50", 1));

        // Act
        Pedido pedido = Pedido.novo(itens, FormaPagamento.CARTAO_FICTICIO, DATA_FIXA);

        // Assert
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
        assertTrue(pedido.getNumero().startsWith("PED-"));
        assertEquals(DATA_FIXA, pedido.getCriadoEm());
    }

    @Test
    void rejeitaListaDeItensVaziaOuNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido("PED-0001", DATA_FIXA, List.of(), FormaPagamento.DINHEIRO,
                        StatusPedido.RECEBIDO));

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido("PED-0001", DATA_FIXA, null, FormaPagamento.DINHEIRO,
                        StatusPedido.RECEBIDO));
    }

    @Test
    void rejeitaItemNuloNaLista() {
        ItemPedido item = item("mocha", "8.50", 1);

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido("PED-0001", DATA_FIXA, Arrays.asList(item, null),
                        FormaPagamento.DINHEIRO, StatusPedido.RECEBIDO));
    }

    @Test
    void rejeitaDataNula() {
        List<ItemPedido> itens = List.of(item("mocha", "8.50", 1));

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido("PED-0001", null, itens, FormaPagamento.DINHEIRO,
                        StatusPedido.RECEBIDO));

        assertEquals("A data do pedido é obrigatória.", erro.getMessage());
    }

    @Test
    void rejeitaPagamentoNulo() {
        List<ItemPedido> itens = List.of(item("mocha", "8.50", 1));

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido("PED-0001", DATA_FIXA, itens, null, StatusPedido.RECEBIDO));
    }

    @Test
    void rejeitaStatusNulo() {
        List<ItemPedido> itens = List.of(item("mocha", "8.50", 1));

        assertThrows(IllegalArgumentException.class,
                () -> new Pedido("PED-0001", DATA_FIXA, itens, FormaPagamento.DINHEIRO, null));
    }

    @Test
    void alteraStatusDoPedido() {
        // Arrange
        Pedido pedido = new Pedido("PED-0001", DATA_FIXA,
                List.of(item("mocha", "8.50", 1)), FormaPagamento.DINHEIRO,
                StatusPedido.RECEBIDO);

        // Act
        pedido.alterarStatus(StatusPedido.EM_PREPARO);
        pedido.alterarStatus(StatusPedido.FINALIZADO);

        // Assert
        assertEquals(StatusPedido.FINALIZADO, pedido.getStatus());
    }

    @Test
    void rejeitaStatusNuloNaAlteracao() {
        Pedido pedido = new Pedido("PED-0001", DATA_FIXA,
                List.of(item("mocha", "8.50", 1)), FormaPagamento.DINHEIRO,
                StatusPedido.RECEBIDO);

        assertThrows(IllegalArgumentException.class, () -> pedido.alterarStatus(null));
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
    }

    @Test
    void itensDoPedidoSaoImutaveis() {
        ItemPedido item = item("mocha", "8.50", 1);
        Pedido pedido = new Pedido("PED-0001", DATA_FIXA, List.of(item),
                FormaPagamento.DINHEIRO, StatusPedido.RECEBIDO);

        assertThrows(UnsupportedOperationException.class,
                () -> pedido.getItens().add(item("cappuccino", "7.50", 1)));
    }
}
