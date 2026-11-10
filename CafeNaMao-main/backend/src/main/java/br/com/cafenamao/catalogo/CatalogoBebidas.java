package br.com.cafenamao.catalogo;

import br.com.cafenamao.dominio.Bebida;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Catálogo em memória com as mesmas seis bebidas exibidas no front-end.
 *
 * <p>A persistência em banco de dados será tratada em uma etapa futura.</p>
 */
@Component
public class CatalogoBebidas {

    private final List<Bebida> bebidas;

    public CatalogoBebidas() {
        this.bebidas = List.of(
                new Bebida("espresso-curto", "Espresso Curto",
                        "Café intenso e encorpado, preparado em dose curta.",
                        "Café", new BigDecimal("4.00")),
                new Bebida("cafe-com-leite", "Café com Leite",
                        "Espresso suave com leite vaporizado e cremoso.",
                        "Café", new BigDecimal("5.50")),
                new Bebida("cappuccino", "Cappuccino",
                        "Café, leite vaporizado e uma camada generosa de espuma.",
                        "Café", new BigDecimal("7.50")),
                new Bebida("mocha", "Mocha",
                        "Espresso com chocolate, leite e finalização cremosa.",
                        "Café", new BigDecimal("8.50")),
                new Bebida("chocolate-quente", "Chocolate Quente",
                        "Chocolate dissolvido lentamente em leite quente.",
                        "Chocolate", new BigDecimal("7.00")),
                new Bebida("cha-de-canela", "Chá de Canela",
                        "Infusão aromática com canela e um toque de mel.",
                        "Chá", new BigDecimal("5.00")));
    }

    public List<Bebida> listar() {
        return bebidas;
    }

    public Optional<Bebida> buscarPorId(String id) {
        if (id == null) {
            return Optional.empty();
        }

        return bebidas.stream()
                .filter(bebida -> bebida.id().equals(id))
                .findFirst();
    }
}
