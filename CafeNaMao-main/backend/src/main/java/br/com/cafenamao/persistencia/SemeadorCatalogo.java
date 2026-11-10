package br.com.cafenamao.persistencia;

import br.com.cafenamao.api.BebidaRepositorio;
import br.com.cafenamao.catalogo.CatalogoBebidas;
import br.com.cafenamao.dominio.Bebida;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Popula o catálogo na primeira inicialização do banco.
 *
 * <p>A carga só acontece quando a tabela está vazia, preservando bebidas
 * cadastradas, alteradas ou removidas durante o uso.</p>
 */
@Component
public class SemeadorCatalogo implements ApplicationRunner {

    private final BebidaRepositorio repositorio;
    private final CatalogoBebidas catalogo;

    public SemeadorCatalogo(BebidaRepositorio repositorio, CatalogoBebidas catalogo) {
        this.repositorio = repositorio;
        this.catalogo = catalogo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repositorio.listar().isEmpty()) {
            return;
        }

        for (Bebida bebida : catalogo.listar()) {
            repositorio.salvar(bebida);
        }
    }
}
