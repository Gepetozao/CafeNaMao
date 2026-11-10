package br.com.cafenamao.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o consumo da API pelo front-end estático.
 *
 * <p>As páginas são abertas diretamente por {@code file://}, o que faz o
 * navegador enviar {@code Origin: null}. Como o pagamento é simulado e a API não
 * usa cookies nem credenciais, a liberação ampla é suficiente para este projeto
 * acadêmico.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
