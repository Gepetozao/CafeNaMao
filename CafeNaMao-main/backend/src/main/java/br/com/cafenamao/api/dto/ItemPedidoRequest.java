package br.com.cafenamao.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Item enviado na criação de um pedido.
 */
public record ItemPedidoRequest(

        @NotBlank(message = "O identificador da bebida é obrigatório.")
        String bebidaId,

        @Min(value = 1, message = "A quantidade mínima é 1.")
        @Max(value = 10, message = "A quantidade máxima é 10.")
        int quantidade) {
}
