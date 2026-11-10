package br.com.cafenamao.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Dados recebidos para criar ou atualizar uma bebida.
 *
 * <p>O identificador é opcional na criação: quando não informado, ele é gerado
 * a partir do nome. Na atualização, o identificador da URL é o que vale.</p>
 */
public record BebidaRequest(

        @Pattern(
                regexp = "^$|^[a-z0-9]+(-[a-z0-9]+)*$",
                message = "O identificador deve conter apenas letras minúsculas, números e hífens.")
        @Size(max = 80, message = "O identificador deve ter no máximo 80 caracteres.")
        String id,

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
        String nome,

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
        String descricao,

        @NotBlank(message = "A categoria é obrigatória.")
        @Size(max = 40, message = "A categoria deve ter no máximo 40 caracteres.")
        String categoria,

        @NotNull(message = "O preço em centavos é obrigatório.")
        @Positive(message = "O preço em centavos deve ser maior que zero.")
        Long precoCentavos) {
}
