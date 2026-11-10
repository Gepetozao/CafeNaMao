package br.com.cafenamao.persistencia;

import br.com.cafenamao.dominio.Bebida;
import br.com.cafenamao.dominio.ItemPedido;
import br.com.cafenamao.dominio.Pedido;

import java.util.Comparator;
import java.util.List;

/**
 * Converte entre o domínio de pedido e suas entidades de banco.
 */
final class PedidoMapper {

    private PedidoMapper() {
    }

    static Pedido paraDominio(PedidoEntity entity) {
        List<ItemPedido> itens = entity.getItens().stream()
                .sorted(Comparator.comparingInt(ItemPedidoEntity::getOrdem))
                .map(PedidoMapper::itemParaDominio)
                .toList();

        return new Pedido(
                entity.getNumero(),
                entity.getCriadoEm(),
                itens,
                entity.getFormaPagamento(),
                entity.getStatus());
    }

    static PedidoEntity paraNovaEntidade(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setNumero(pedido.getNumero());
        entity.setCriadoEm(pedido.getCriadoEm());
        entity.setFormaPagamento(pedido.getFormaPagamento());
        entity.setStatus(pedido.getStatus());

        int ordem = 0;
        for (ItemPedido item : pedido.getItens()) {
            ItemPedidoEntity itemEntity = new ItemPedidoEntity();
            itemEntity.setPedido(entity);
            itemEntity.setOrdem(ordem++);
            itemEntity.setBebidaId(item.bebida().id());
            itemEntity.setBebidaNome(item.bebida().nome());
            itemEntity.setBebidaDescricao(item.bebida().descricao());
            itemEntity.setBebidaCategoria(item.bebida().categoria());
            itemEntity.setBebidaPreco(item.bebida().preco());
            itemEntity.setQuantidade(item.quantidade());
            entity.getItens().add(itemEntity);
        }

        return entity;
    }

    private static ItemPedido itemParaDominio(ItemPedidoEntity entity) {
        Bebida bebida = new Bebida(
                entity.getBebidaId(),
                entity.getBebidaNome(),
                entity.getBebidaDescricao(),
                entity.getBebidaCategoria(),
                entity.getBebidaPreco());

        return new ItemPedido(bebida, entity.getQuantidade());
    }
}
