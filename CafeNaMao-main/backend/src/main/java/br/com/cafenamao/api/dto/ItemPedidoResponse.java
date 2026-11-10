package br.com.cafenamao.api.dto;

import br.com.cafenamao.dominio.ItemPedido;
import br.com.cafenamao.dominio.Preco;

/**
 * Item de pedido devolvido pela API, com valores em centavos.
 */
public record ItemPedidoResponse(
        String bebidaId,
        String nome,
        int quantidade,
        long precoCentavos,
        long subtotalCentavos) {

    public static ItemPedidoResponse de(ItemPedido item) {
        return new ItemPedidoResponse(
                item.bebida().id(),
                item.bebida().nome(),
                item.quantidade(),
                Preco.paraCentavos(item.bebida().preco()),
                Preco.paraCentavos(item.subtotal()));
    }
}
