package br.com.cafenamao.api.erro;

/**
 * Lançada quando os dados recebidos são sintaticamente válidos, mas não podem
 * ser aceitos pela regra de negócio.
 */
public class RequisicaoInvalidaException extends RuntimeException {

    public RequisicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
