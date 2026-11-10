package br.com.cafenamao.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerTest {

    private static final String PEDIDO_DINHEIRO = """
            {
              "formaPagamento": "dinheiro",
              "itens": [
                { "bebidaId": "cappuccino", "quantidade": 3 },
                { "bebidaId": "espresso-curto", "quantidade": 2 }
              ]
            }
            """;

    private static final String PEDIDO_CARTAO = """
            {
              "formaPagamento": "cartao-ficticio",
              "itens": [
                { "bebidaId": "mocha", "quantidade": 1 }
              ]
            }
            """;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BebidaRepositorio bebidaRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @BeforeEach
    void reiniciarDados() {
        bebidaRepositorio.reiniciar();
        pedidoRepositorio.limpar();
    }

    @Test
    void criaPedidoComSucesso() throws Exception {
        MvcResult resultado = mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_DINHEIRO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(startsWith("PED-")))
                .andExpect(jsonPath("$.status").value("recebido"))
                .andExpect(jsonPath("$.statusRotulo").value("Recebido"))
                .andExpect(jsonPath("$.formaPagamento").value("dinheiro"))
                .andExpect(jsonPath("$.formaPagamentoRotulo").value("Dinheiro"))
                .andExpect(jsonPath("$.itens.length()").value(2))
                .andExpect(jsonPath("$.itens[0].bebidaId").value("cappuccino"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(3))
                .andExpect(jsonPath("$.itens[0].precoCentavos").value(750))
                .andExpect(jsonPath("$.itens[0].subtotalCentavos").value(2250))
                .andExpect(jsonPath("$.itens[1].subtotalCentavos").value(800))
                .andExpect(jsonPath("$.totalCentavos").value(3050))
                .andReturn();

        String numero = JsonPath.read(resultado.getResponse().getContentAsString(), "$.numero");
        assertTrue(resultado.getResponse().getHeader("Location")
                .endsWith("/api/pedidos/" + numero));
    }

    @Test
    void criaPedidoComCartaoFicticio() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_CARTAO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formaPagamento").value("cartao-ficticio"))
                .andExpect(jsonPath("$.formaPagamentoRotulo").value("Cartão fictício"))
                .andExpect(jsonPath("$.totalCentavos").value(850));
    }

    @Test
    void criaPedidoComBebidaInexistenteRetorna404() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "formaPagamento": "dinheiro",
                                  "itens": [
                                    { "bebidaId": "bebida-inexistente", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(
                        "Bebida não encontrada para o identificador 'bebida-inexistente'."));
    }

    @Test
    void criaPedidoComQuantidadeAbaixoDoMinimoRetorna400() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "formaPagamento": "dinheiro",
                                  "itens": [
                                    { "bebidaId": "cappuccino", "quantidade": 0 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }

    @Test
    void criaPedidoComQuantidadeAcimaDoMaximoRetorna400() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "formaPagamento": "dinheiro",
                                  "itens": [
                                    { "bebidaId": "cappuccino", "quantidade": 11 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros").exists());
    }

    @Test
    void criaPedidoSemItensRetorna400() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "formaPagamento": "dinheiro", "itens": [] }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.itens").exists());
    }

    @Test
    void criaPedidoComFormaPagamentoInvalidaRetorna400() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "formaPagamento": "pix",
                                  "itens": [
                                    { "bebidaId": "cappuccino", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Forma de pagamento inválida: 'pix'. Use 'dinheiro' ou 'cartao-ficticio'."));
    }

    @Test
    void criaPedidoSemFormaPagamentoRetorna400() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "itens": [
                                    { "bebidaId": "cappuccino", "quantidade": 1 }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.formaPagamento").exists());
    }

    @Test
    void rejeitaCorpoMalformado() throws Exception {
        mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("O corpo da requisição é inválido ou está ausente."));
    }

    @Test
    void listaPedidosDoMaisRecenteParaOMaisAntigo() throws Exception {
        String primeiro = criarPedido(PEDIDO_DINHEIRO);
        String segundo = criarPedido(PEDIDO_CARTAO);

        mvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numero").value(segundo))
                .andExpect(jsonPath("$[1].numero").value(primeiro));
    }

    @Test
    void buscaPedidoPorNumero() throws Exception {
        String numero = criarPedido(PEDIDO_DINHEIRO);

        mvc.perform(get("/api/pedidos/{numero}", numero))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(numero))
                .andExpect(jsonPath("$.totalCentavos").value(3050));
    }

    @Test
    void buscaPedidoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/pedidos/PED-INEXISTENTE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Pedido não encontrado para o número 'PED-INEXISTENTE'."));
    }

    @Test
    void alteraStatusDoPedido() throws Exception {
        String numero = criarPedido(PEDIDO_DINHEIRO);

        mvc.perform(patch("/api/pedidos/{numero}/status", numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "em-preparo" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("em-preparo"))
                .andExpect(jsonPath("$.statusRotulo").value("Em preparo"));

        mvc.perform(patch("/api/pedidos/{numero}/status", numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "finalizado" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("finalizado"))
                .andExpect(jsonPath("$.statusRotulo").value("Finalizado"));

        mvc.perform(get("/api/pedidos/{numero}", numero))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("finalizado"));
    }

    @Test
    void alteraStatusComCodigoInvalidoRetorna400() throws Exception {
        String numero = criarPedido(PEDIDO_DINHEIRO);

        mvc.perform(patch("/api/pedidos/{numero}/status", numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "pronto" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Status inválido: 'pronto'. Use 'recebido', 'em-preparo' ou 'finalizado'."));
    }

    @Test
    void alteraStatusSemStatusRetorna400() throws Exception {
        String numero = criarPedido(PEDIDO_DINHEIRO);

        mvc.perform(patch("/api/pedidos/{numero}/status", numero)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.status").exists());
    }

    @Test
    void alteraStatusDePedidoInexistenteRetorna404() throws Exception {
        mvc.perform(patch("/api/pedidos/PED-INEXISTENTE/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "status": "finalizado" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Pedido não encontrado para o número 'PED-INEXISTENTE'."));
    }

    private String criarPedido(String corpo) throws Exception {
        MvcResult resultado = mvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn();

        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.numero");
    }
}
