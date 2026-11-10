package br.com.cafenamao.api;

import br.com.cafenamao.dominio.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de armazenamento de pedidos.
 */
public interface PedidoRepositorio {

    Pedido salvar(Pedido pedido);

    Optional<Pedido> buscarPorNumero(String numero);

    /**
     * Lista os pedidos do mais recente para o mais antigo, como no histórico do
     * front-end.
     */
    List<Pedido> listar();

    void limpar();
}
