package br.com.cafenamao.dominio;

/**
 * Validações simples compartilhadas pelo domínio.
 */
final class Validacao {

    private Validacao() {
    }

    static String textoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        return valor.trim();
    }

    static <T> T objetoObrigatorio(T valor, String mensagem) {
        if (valor == null) {
            throw new IllegalArgumentException(mensagem);
        }

        return valor;
    }
}
