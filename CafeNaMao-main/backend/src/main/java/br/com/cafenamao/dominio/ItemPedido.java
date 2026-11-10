package br.com.cafenamao.dominio;

import java.math.BigDecimal;

/**
 * Item de um pedido: uma bebida e a quantidade escolhida.
 */
public record ItemPedido(Bebida bebida, int quantidade) {

    public ItemPedido {
        if (bebida == null) {
            throw new IllegalArgumentException("A bebida do item é obrigatória.");
        }

        RegraQuantidade.validar(quantidade);
    }

    public BigDecimal subtotal() {
        return Preco.multiplicar(bebida.preco(), quantidade);
    }
}
