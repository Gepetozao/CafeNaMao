package br.com.cafenamao.persistencia;

import br.com.cafenamao.api.BebidaRepositorio;
import br.com.cafenamao.api.BebidaService;
import br.com.cafenamao.api.PedidoRepositorio;
import br.com.cafenamao.api.PedidoService;
import br.com.cafenamao.api.dto.BebidaRequest;
import br.com.cafenamao.api.dto.ItemPedidoRequest;
import br.com.cafenamao.api.dto.PedidoRequest;
import br.com.cafenamao.api.dto.StatusRequest;
import br.com.cafenamao.dominio.Bebida;
import br.com.cafenamao.dominio.Pedido;
import br.com.cafenamao.dominio.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que o adaptador JPA preserva o comportamento observável dos
 * repositórios: catálogo inicial, criação, atualização e histórico de pedidos.
 */
@SpringBootTest
@ActiveProfiles("test")
class PersistenciaTest {

    @Autowired
    private BebidaRepositorio bebidaRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private BebidaService bebidaService;

    @Autowired
    private PedidoService pedidoService;

    @BeforeEach
    void limparDados() {
        bebidaRepositorio.reiniciar();
        pedidoRepositorio.limpar();
    }

    @Test
    void catalogoInicialEhPersistidoNaOrdemDoFrontEnd() {
        List<Bebida> bebidas = bebidaRepositorio.listar();

        assertEquals(6, bebidas.size());
        assertEquals("espresso-curto", bebidas.get(0).id());
        assertEquals("cha-de-canela", bebidas.get(5).id());
    }

    @Test
    void bebidaCriadaSobreviveANovaLeitura() {
        Bebida criada = bebidaService.criar(new BebidaRequest(
                null, "Café Gelado", "Bebida gelada com café.", "Café", 950L));

        Bebida lida = bebidaRepositorio.buscarPorId("cafe-gelado").orElseThrow();

        assertEquals(criada.id(), lida.id());
        assertEquals("Café Gelado", lida.nome());
        assertEquals(new BigDecimal("9.50"), lida.preco());
    }

    @Test
    void atualizacaoDeBebidaEhPersistida() {
        bebidaService.atualizar("cappuccino", new BebidaRequest(
                null, "Cappuccino Especial", "Nova descrição.", "Café", 800L));

        Bebida lida = bebidaRepositorio.buscarPorId("cappuccino").orElseThrow();

        assertEquals("Cappuccino Especial", lida.nome());
        assertEquals(new BigDecimal("8.00"), lida.preco());
    }

    @Test
    void pedidoMantemItensETotalAposNovaLeitura() {
        Pedido criado = pedidoService.criar(new PedidoRequest(
                List.of(
                        new ItemPedidoRequest("cappuccino", 2),
                        new ItemPedidoRequest("espresso-curto", 1)),
                "dinheiro"));

        Pedido lido = pedidoRepositorio.buscarPorNumero(criado.getNumero()).orElseThrow();

        assertEquals(2, lido.getItens().size());
        assertEquals("cappuccino", lido.getItens().get(0).bebida().id());
        assertEquals(2, lido.getItens().get(0).quantidade());
        assertEquals(new BigDecimal("19.00"), lido.total());
        assertEquals(StatusPedido.RECEBIDO, lido.getStatus());
    }

    @Test
    void alteracaoDeStatusEhPersistida() {
        Pedido pedido = criarPedidoEmDinheiro();

        pedidoService.alterarStatus(pedido.getNumero(), new StatusRequest("em-preparo"));

        assertEquals(StatusPedido.EM_PREPARO,
                pedidoRepositorio.buscarPorNumero(pedido.getNumero()).orElseThrow().getStatus());
    }

    @Test
    void pedidosSaoListadosDoMaisRecenteParaOMaisAntigo() {
        Pedido primeiro = criarPedidoEmDinheiro();
        Pedido segundo = criarPedidoEmDinheiro();

        List<Pedido> pedidos = pedidoRepositorio.listar();

        assertEquals(2, pedidos.size());
        assertEquals(segundo.getNumero(), pedidos.get(0).getNumero());
        assertEquals(primeiro.getNumero(), pedidos.get(1).getNumero());
    }

    @Test
    void remocaoDeBebidaEhPersistida() {
        assertTrue(bebidaRepositorio.remover("cha-de-canela"));
        assertTrue(bebidaRepositorio.buscarPorId("cha-de-canela").isEmpty());
        assertEquals(5, bebidaRepositorio.listar().size());
    }

    private Pedido criarPedidoEmDinheiro() {
        return pedidoService.criar(new PedidoRequest(
                List.of(new ItemPedidoRequest("cappuccino", 1)),
                "dinheiro"));
    }
}
