package br.com.cafenamao.api;

import br.com.cafenamao.dominio.Bebida;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de armazenamento de bebidas.
 *
 * <p>A API depende desta interface; o adaptador JPA fica em
 * {@code br.com.cafenamao.persistencia} e os testes unitários podem usar uma
 * implementação em memória.</p>
 */
public interface BebidaRepositorio {

    List<Bebida> listar();

    Optional<Bebida> buscarPorId(String id);

    boolean existePorId(String id);

    Bebida salvar(Bebida bebida);

    boolean remover(String id);

    /**
     * Restaura o catálogo inicial. Usado para isolar testes.
     */
    void reiniciar();
}
