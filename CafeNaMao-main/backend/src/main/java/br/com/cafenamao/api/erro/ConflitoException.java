package br.com.cafenamao.api.erro;

/**
 * Lançada quando a operação conflita com um recurso já existente.
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
