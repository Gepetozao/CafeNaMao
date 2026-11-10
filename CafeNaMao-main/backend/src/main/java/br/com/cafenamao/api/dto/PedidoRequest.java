package br.com.cafenamao.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Dados recebidos para criar um pedido.
 */
public record PedidoRequest(

        @NotEmpty(message = "O pedido precisa ter pelo menos um item.")
        List<@Valid ItemPedidoRequest> itens,

        @NotBlank(message = "A forma de pagamento é obrigatória.")
        String formaPagamento) {
}
