package br.com.cafenamao.dominio;

/**
 * Limites de quantidade por bebida, iguais aos usados no front-end.
 */
public final class RegraQuantidade {

    public static final int MINIMA = 1;
    public static final int MAXIMA = 10;

    private RegraQuantidade() {
    }

    public static void validar(int quantidade) {
        if (quantidade < MINIMA) {
            throw new IllegalArgumentException("A quantidade mínima é " + MINIMA + ".");
        }

        if (quantidade > MAXIMA) {
            throw new IllegalArgumentException("A quantidade máxima é " + MAXIMA + ".");
        }
    }

    public static boolean valida(int quantidade) {
        return quantidade >= MINIMA && quantidade <= MAXIMA;
    }
}
