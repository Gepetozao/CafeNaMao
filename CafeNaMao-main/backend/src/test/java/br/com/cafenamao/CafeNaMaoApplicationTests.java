package br.com.cafenamao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CafeNaMaoApplicationTests {

    @Test
    void contextoCarrega() {
        // O contexto do Spring Boot deve iniciar sem erros de configuração.
    }
}
