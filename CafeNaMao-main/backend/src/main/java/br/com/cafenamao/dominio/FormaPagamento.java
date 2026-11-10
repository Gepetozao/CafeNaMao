package br.com.cafenamao.dominio;

import java.util.Arrays;
import java.util.Optional;

public enum FormaPagamento {

    DINHEIRO("dinheiro", "Dinheiro"),
    CARTAO_FICTICIO("cartao-ficticio", "Cartão fictício");

    private final String codigo;
    private final String rotulo;

    FormaPagamento(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static Optional<FormaPagamento> deCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(pagamento -> pagamento.codigo.equals(codigo))
                .findFirst();
    }
}
