package br.com.cafenamao.api;

import br.com.cafenamao.catalogo.CatalogoBebidas;
import br.com.cafenamao.dominio.Bebida;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação em memória de {@link BebidaRepositorio}.
 *
 * <p>Usada apenas em testes unitários que não sobem o contexto do Spring. A
 * aplicação real usa o adaptador JPA.</p>
 */
public class BebidaRepositorioMemoria implements BebidaRepositorio {

    private final CatalogoBebidas catalogo;
    private final Map<String, Bebida> bebidas = new LinkedHashMap<>();

    public BebidaRepositorioMemoria(CatalogoBebidas catalogo) {
        this.catalogo = catalogo;
        reiniciar();
    }

    @Override
    public synchronized List<Bebida> listar() {
        return List.copyOf(bebidas.values());
    }

    @Override
    public synchronized Optional<Bebida> buscarPorId(String id) {
        return Optional.ofNullable(bebidas.get(id));
    }

    @Override
    public synchronized boolean existePorId(String id) {
        return bebidas.containsKey(id);
    }

    @Override
    public synchronized Bebida salvar(Bebida bebida) {
        bebidas.put(bebida.id(), bebida);
        return bebida;
    }

    @Override
    public synchronized boolean remover(String id) {
        return bebidas.remove(id) != null;
    }

    @Override
    public synchronized void reiniciar() {
        bebidas.clear();
        catalogo.listar().forEach(bebida -> bebidas.put(bebida.id(), bebida));
    }
}
