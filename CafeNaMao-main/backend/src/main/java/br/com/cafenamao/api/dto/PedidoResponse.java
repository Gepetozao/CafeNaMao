package br.com.cafenamao.api.dto;

import br.com.cafenamao.dominio.Pedido;
import br.com.cafenamao.dominio.Preco;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representação pública de um pedido.
 */
public record PedidoResponse(
        String numero,
        LocalDateTime criadoEm,
        List<ItemPedidoResponse> itens,
        String formaPagamento,
        String formaPagamentoRotulo,
        String status,
        String statusRotulo,
        long totalCentavos) {

    public static PedidoResponse de(Pedido pedido) {
        return new PedidoResponse(
                pedido.getNumero(),
                pedido.getCriadoEm(),
                pedido.getItens().stream().map(ItemPedidoResponse::de).toList(),
                pedido.getFormaPagamento().getCodigo(),
                pedido.getFormaPagamento().getRotulo(),
                pedido.getStatus().getCodigo(),
                pedido.getStatus().getRotulo(),
                Preco.paraCentavos(pedido.total()));
    }
}
