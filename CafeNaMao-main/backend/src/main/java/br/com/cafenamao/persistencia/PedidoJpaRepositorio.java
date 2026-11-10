package br.com.cafenamao.persistencia;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Consultas Spring Data para pedidos.
 */
public interface PedidoJpaRepositorio extends JpaRepository<PedidoEntity, Long> {

    @EntityGraph(attributePaths = "itens")
    Optional<PedidoEntity> findByNumero(String numero);

    @EntityGraph(attributePaths = "itens")
    List<PedidoEntity> findAllByOrderByIdDesc();
}
