package br.com.cafenamao.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regras de preço do domínio.
 *
 * <p>Os valores monetários usam {@link BigDecimal} com exatamente duas casas
 * decimais. Valores fracionários além do centavo são rejeitados para evitar
 * arredondamentos silenciosos.</p>
 */
public final class Preco {

    public static final int ESCALA = 2;
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(ESCALA, RoundingMode.UNNECESSARY);

    private Preco() {
    }

    public static BigDecimal validar(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O preço é obrigatório.");
        }

        if (valor.signum() < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }

        try {
            return valor.setScale(ESCALA, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException erro) {
            throw new IllegalArgumentException("O preço deve ter no máximo duas casas decimais.", erro);
        }
    }

    public static BigDecimal deCentavos(long centavos) {
        if (centavos < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }

        return BigDecimal.valueOf(centavos, ESCALA);
    }

    public static long paraCentavos(BigDecimal valor) {
        return validar(valor).movePointRight(ESCALA).longValueExact();
    }

    public static BigDecimal somar(BigDecimal valor, BigDecimal outroValor) {
        return validar(valor).add(validar(outroValor));
    }

    public static BigDecimal multiplicar(BigDecimal valor, int quantidade) {
        return validar(valor).multiply(BigDecimal.valueOf(quantidade));
    }
}
