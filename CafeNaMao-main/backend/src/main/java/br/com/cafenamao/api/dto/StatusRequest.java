package br.com.cafenamao.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados recebidos para alterar o status de um pedido.
 */
public record StatusRequest(

        @NotBlank(message = "O status é obrigatório.")
        String status) {
}
