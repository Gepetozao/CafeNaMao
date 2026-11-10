package br.com.cafenamao.api;

import br.com.cafenamao.api.dto.BebidaRequest;
import br.com.cafenamao.api.erro.RequisicaoInvalidaException;
import br.com.cafenamao.catalogo.CatalogoBebidas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BebidaServiceTest {

    private final BebidaService service =
            new BebidaService(new BebidaRepositorioMemoria(new CatalogoBebidas()));

    @Test
    void rejeitaPrecoNulo() {
        // Arrange
        BebidaRequest request = new BebidaRequest(
                null, "Bebida", "Descrição", "Café", null);

        // Act
        RequisicaoInvalidaException erro = assertThrows(
                RequisicaoInvalidaException.class,
                () -> service.criar(request));

        // Assert
        assertEquals("O preço em centavos é obrigatório.", erro.getMessage());
    }
}
