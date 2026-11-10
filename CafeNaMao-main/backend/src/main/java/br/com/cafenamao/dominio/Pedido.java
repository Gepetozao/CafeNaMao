package br.com.cafenamao.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Pedido confirmado. Guarda uma cópia imutável dos itens e calcula o total em
 * {@link BigDecimal}.
 */
public final class Pedido {

    private final String numero;
    private final LocalDateTime criadoEm;
    private final List<ItemPedido> itens;
    private final FormaPagamento formaPagamento;
    private StatusPedido status;

    public Pedido(
            String numero,
            LocalDateTime criadoEm,
            List<ItemPedido> itens,
            FormaPagamento formaPagamento,
            StatusPedido status) {

        this.numero = Validacao.textoObrigatorio(numero, "O número do pedido é obrigatório.");

        if (criadoEm == null) {
            throw new IllegalArgumentException("A data do pedido é obrigatória.");
        }

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa ter pelo menos um item.");
        }

        if (itens.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("O pedido não pode conter item nulo.");
        }

        this.criadoEm = criadoEm;
        this.itens = List.copyOf(itens);
        this.formaPagamento = Validacao.objetoObrigatorio(
                formaPagamento, "A forma de pagamento é obrigatória.");
        this.status = Validacao.objetoObrigatorio(status, "O status do pedido é obrigatório.");
    }

    public static Pedido novo(List<ItemPedido> itens, FormaPagamento formaPagamento) {
        return new Pedido(gerarNumero(), LocalDateTime.now(), itens, formaPagamento,
                StatusPedido.RECEBIDO);
    }

    public static Pedido novo(List<ItemPedido> itens, FormaPagamento formaPagamento,
            LocalDateTime criadoEm) {
        return new Pedido(gerarNumero(), criadoEm, itens, formaPagamento, StatusPedido.RECEBIDO);
    }

    public BigDecimal total() {
        return itens.stream()
                .map(ItemPedido::subtotal)
                .reduce(Preco.ZERO, BigDecimal::add);
    }

    public void alterarStatus(StatusPedido novoStatus) {
        this.status = Validacao.objetoObrigatorio(novoStatus, "O novo status é obrigatório.");
    }

    private static String gerarNumero() {
        return "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    public String getNumero() {
        return numero;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public StatusPedido getStatus() {
        return status;
    }
}
