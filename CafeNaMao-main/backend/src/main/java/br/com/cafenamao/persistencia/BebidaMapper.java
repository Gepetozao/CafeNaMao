package br.com.cafenamao.persistencia;

import br.com.cafenamao.dominio.Bebida;

/**
 * Converte entre o domínio de bebida e sua representação no banco.
 */
final class BebidaMapper {

    private BebidaMapper() {
    }

    static Bebida paraDominio(BebidaEntity entity) {
        return new Bebida(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getCategoria(),
                entity.getPreco());
    }

    static void copiarParaEntidade(Bebida bebida, BebidaEntity entity) {
        entity.setId(bebida.id());
        entity.setNome(bebida.nome());
        entity.setDescricao(bebida.descricao());
        entity.setCategoria(bebida.categoria());
        entity.setPreco(bebida.preco());
    }

    static BebidaEntity paraNovaEntidade(Bebida bebida, int ordem) {
        BebidaEntity entity = new BebidaEntity();
        copiarParaEntidade(bebida, entity);
        entity.setOrdem(ordem);
        return entity;
    }
}
