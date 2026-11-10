package br.com.cafenamao.dominio;

import java.util.Arrays;
import java.util.Optional;

public enum StatusPedido {

    RECEBIDO("recebido", "Recebido"),
    EM_PREPARO("em-preparo", "Em preparo"),
    FINALIZADO("finalizado", "Finalizado");

    private final String codigo;
    private final String rotulo;

    StatusPedido(String codigo, String rotulo) {
        this.codigo = codigo;
        this.rotulo = rotulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static Optional<StatusPedido> deCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }

        return Arrays.stream(values())
                .filter(status -> status.codigo.equals(codigo))
                .findFirst();
    }
}
