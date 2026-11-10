package br.com.cafenamao.api.erro;

/**
 * Lançada quando um recurso solicitado não existe.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
