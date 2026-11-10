package br.com.cafenamao.api.dto;

import br.com.cafenamao.dominio.Bebida;
import br.com.cafenamao.dominio.Preco;

/**
 * Representação pública de uma bebida. O preço é exposto em centavos para
 * manter o mesmo contrato do front-end e evitar valores fracionários.
 */
public record BebidaResponse(
        String id,
        String nome,
        String descricao,
        String categoria,
        long precoCentavos) {

    public static BebidaResponse de(Bebida bebida) {
        return new BebidaResponse(
                bebida.id(),
                bebida.nome(),
                bebida.descricao(),
                bebida.categoria(),
                Preco.paraCentavos(bebida.preco()));
    }
}
