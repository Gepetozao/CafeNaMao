package br.com.cafenamao.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante que o preflight do navegador é aceito quando a página é aberta por
 * {@code file://}, cujo cabeçalho {@code Origin} vale {@code null}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void aceitaPreflightDeLeituraDoCatalogo() throws Exception {
        mockMvc.perform(options("/api/bebidas")
                        .header(HttpHeaders.ORIGIN, "null")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }

    @Test
    void aceitaPreflightDeCriacaoDePedido() throws Exception {
        mockMvc.perform(options("/api/pedidos")
                        .header(HttpHeaders.ORIGIN, "null")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("POST")))
                .andExpect(header().string(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("content-type")));
    }
}
