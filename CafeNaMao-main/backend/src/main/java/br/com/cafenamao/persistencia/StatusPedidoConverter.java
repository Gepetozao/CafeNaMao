package br.com.cafenamao.persistencia;

import br.com.cafenamao.dominio.StatusPedido;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persiste o status pelo código usado na API ("recebido", "em-preparo",
 * "finalizado").
 */
@Converter
public class StatusPedidoConverter implements AttributeConverter<StatusPedido, String> {

    @Override
    public String convertToDatabaseColumn(StatusPedido status) {
        return status == null ? null : status.getCodigo();
    }

    @Override
    public StatusPedido convertToEntityAttribute(String codigo) {
        return StatusPedido.deCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Status desconhecido no banco: '" + codigo + "'."));
    }
}
