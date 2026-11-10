package br.com.cafenamao.dominio;

import java.math.BigDecimal;

/**
 * Bebida do catálogo. O registro é imutável e valida os dados na criação.
 */
public record Bebida(
        String id,
        String nome,
        String descricao,
        String categoria,
        BigDecimal preco) {

    public Bebida {
        id = Validacao.textoObrigatorio(id, "O identificador da bebida é obrigatório.");
        nome = Validacao.textoObrigatorio(nome, "O nome da bebida é obrigatório.");
        descricao = Validacao.textoObrigatorio(descricao, "A descrição da bebida é obrigatória.");
        categoria = Validacao.textoObrigatorio(categoria, "A categoria da bebida é obrigatória.");
        preco = Preco.validar(preco);
    }
}
