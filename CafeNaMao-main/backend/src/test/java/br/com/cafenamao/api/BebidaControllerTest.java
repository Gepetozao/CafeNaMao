package br.com.cafenamao.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BebidaControllerTest {

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
    void listaBebidasComSucesso() throws Exception {
        mvc.perform(get("/api/bebidas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].id").value("espresso-curto"))
                .andExpect(jsonPath("$[0].nome").value("Espresso Curto"))
                .andExpect(jsonPath("$[0].precoCentavos").value(400));
    }

    @Test
    void buscaBebidaPorIdentificador() throws Exception {
        mvc.perform(get("/api/bebidas/cappuccino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cappuccino"))
                .andExpect(jsonPath("$.nome").value("Cappuccino"))
                .andExpect(jsonPath("$.categoria").value("Café"))
                .andExpect(jsonPath("$.precoCentavos").value(750));
    }

    @Test
    void buscaBebidaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/bebidas/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail")
                        .value("Bebida não encontrada para o identificador 'inexistente'."));
    }

    @Test
    void criaBebidaComIdentificadorGerado() throws Exception {
        mvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Café Gelado",
                                  "descricao": "Bebida gelada com café.",
                                  "categoria": "Café",
                                  "precoCentavos": 950
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/bebidas/cafe-gelado")))
                .andExpect(jsonPath("$.id").value("cafe-gelado"))
                .andExpect(jsonPath("$.nome").value("Café Gelado"))
                .andExpect(jsonPath("$.precoCentavos").value(950));

        mvc.perform(get("/api/bebidas/cafe-gelado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Café Gelado"));
    }

    @Test
    void criaBebidaComIdentificadorInformado() throws Exception {
        mvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "cafe-gelado",
                                  "nome": "Café Gelado",
                                  "descricao": "Bebida gelada com café.",
                                  "categoria": "Café",
                                  "precoCentavos": 950
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/bebidas/cafe-gelado")))
                .andExpect(jsonPath("$.id").value("cafe-gelado"));
    }

    @Test
    void criaBebidaComIdentificadorDuplicadoRetorna409() throws Exception {
        mvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "cappuccino",
                                  "nome": "Outro Cappuccino",
                                  "descricao": "Já existe.",
                                  "categoria": "Café",
                                  "precoCentavos": 900
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito"))
                .andExpect(jsonPath("$.detail")
                        .value("Já existe uma bebida com o identificador 'cappuccino'."));
    }

    @Test
    void validaCamposObrigatoriosDaBebida() throws Exception {
        mvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "descricao": "",
                                  "categoria": "",
                                  "precoCentavos": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição inválida"))
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.descricao").exists())
                .andExpect(jsonPath("$.erros.categoria").exists())
                .andExpect(jsonPath("$.erros.precoCentavos").exists());
    }

    @Test
    void validaFormatoDoIdentificador() throws Exception {
        mvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "Café Gelado",
                                  "nome": "Café Gelado",
                                  "descricao": "Bebida gelada.",
                                  "categoria": "Café",
                                  "precoCentavos": 950
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.id").exists());
    }

    @Test
    void rejeitaNomeQueNaoGeraIdentificador() throws Exception {
        mvc.perform(post("/api/bebidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "!!!",
                                  "descricao": "Nome sem letras.",
                                  "categoria": "Café",
                                  "precoCentavos": 100
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Não foi possível gerar um identificador a partir do nome informado."));
    }

    @Test
    void atualizaBebidaExistente() throws Exception {
        mvc.perform(put("/api/bebidas/cappuccino")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Cappuccino Especial",
                                  "descricao": "Nova descrição do cappuccino.",
                                  "categoria": "Café",
                                  "precoCentavos": 800
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cappuccino"))
                .andExpect(jsonPath("$.nome").value("Cappuccino Especial"))
                .andExpect(jsonPath("$.precoCentavos").value(800));

        mvc.perform(get("/api/bebidas/cappuccino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cappuccino Especial"))
                .andExpect(jsonPath("$.precoCentavos").value(800));
    }

    @Test
    void atualizaBebidaComIdentificadorDiferenteRetorna400() throws Exception {
        mvc.perform(put("/api/bebidas/cappuccino")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "mocha",
                                  "nome": "Cappuccino",
                                  "descricao": "Descrição.",
                                  "categoria": "Café",
                                  "precoCentavos": 750
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "O identificador do corpo não pode ser diferente do identificador da URL."));
    }

    @Test
    void atualizaBebidaInexistenteRetorna404() throws Exception {
        mvc.perform(put("/api/bebidas/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Bebida",
                                  "descricao": "Descrição.",
                                  "categoria": "Café",
                                  "precoCentavos": 100
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Bebida não encontrada para o identificador 'inexistente'."));
    }

    @Test
    void removeBebidaExistente() throws Exception {
        mvc.perform(delete("/api/bebidas/cha-de-canela"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/bebidas/cha-de-canela"))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/bebidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void removeBebidaInexistenteRetorna404() throws Exception {
        mvc.perform(delete("/api/bebidas/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Bebida não encontrada para o identificador 'inexistente'."));
    }
}
