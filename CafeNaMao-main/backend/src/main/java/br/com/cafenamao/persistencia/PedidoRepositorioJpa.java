package br.com.cafenamao.persistencia;

import br.com.cafenamao.api.PedidoRepositorio;
import br.com.cafenamao.dominio.Pedido;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA do repositório de pedidos.
 */
@Repository
@Transactional(readOnly = true)
public class PedidoRepositorioJpa implements PedidoRepositorio {

    private final PedidoJpaRepositorio jpa;

    public PedidoRepositorioJpa(PedidoJpaRepositorio jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public Pedido salvar(Pedido pedido) {
        Optional<PedidoEntity> existente = jpa.findByNumero(pedido.getNumero());

        if (existente.isPresent()) {
            // Hoje o único dado mutável do pedido é o status.
            existente.get().setStatus(pedido.getStatus());
        } else {
            jpa.save(PedidoMapper.paraNovaEntidade(pedido));
        }

        return pedido;
    }

    @Override
    public Optional<Pedido> buscarPorNumero(String numero) {
        return jpa.findByNumero(numero).map(PedidoMapper::paraDominio);
    }

    @Override
    public List<Pedido> listar() {
        return jpa.findAllByOrderByIdDesc().stream()
                .map(PedidoMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void limpar() {
        jpa.deleteAllInBatch();
    }
}
