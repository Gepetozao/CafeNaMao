package br.com.cafenamao.catalogo;

import br.com.cafenamao.dominio.Bebida;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogoBebidasTest {

    private final CatalogoBebidas catalogo = new CatalogoBebidas();

    @Test
    void listaSeisBebidas() {
        // Act
        List<Bebida> bebidas = catalogo.listar();

        // Assert
        assertEquals(6, bebidas.size());
        assertEquals("Espresso Curto", bebidas.get(0).nome());
        assertEquals(4, bebidas.stream().filter(bebida -> bebida.categoria().equals("Café")).count());
    }

    @Test
    void identificadoresSaoUnicos() {
        // Arrange
        List<Bebida> bebidas = catalogo.listar();

        // Act
        Set<String> ids = new HashSet<>();
        bebidas.forEach(bebida -> ids.add(bebida.id()));

        // Assert
        assertEquals(bebidas.size(), ids.size());
    }

    @Test
    void todosOsPrecosSaoPositivosComDuasCasas() {
        catalogo.listar().forEach(bebida -> {
            assertTrue(bebida.preco().signum() > 0);
            assertEquals(2, bebida.preco().scale());
        });
    }

    @Test
    void buscaBebidaPorIdentificador() {
        // Act
        Bebida encontrada = catalogo.buscarPorId("cappuccino").orElseThrow();

        // Assert
        assertEquals("Cappuccino", encontrada.nome());
        assertTrue(catalogo.buscarPorId("inexistente").isEmpty());
        assertTrue(catalogo.buscarPorId(null).isEmpty());
    }
}
