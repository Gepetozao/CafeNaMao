package br.com.cafenamao.persistencia;

import br.com.cafenamao.dominio.FormaPagamento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persiste a forma de pagamento pelo código usado na API ("dinheiro",
 * "cartao-ficticio"), mantendo o banco legível.
 */
@Converter
public class FormaPagamentoConverter implements AttributeConverter<FormaPagamento, String> {

    @Override
    public String convertToDatabaseColumn(FormaPagamento formaPagamento) {
        return formaPagamento == null ? null : formaPagamento.getCodigo();
    }

    @Override
    public FormaPagamento convertToEntityAttribute(String codigo) {
        return FormaPagamento.deCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Forma de pagamento desconhecida no banco: '" + codigo + "'."));
    }
}
