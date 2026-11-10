package br.com.cafenamao.persistencia;

import br.com.cafenamao.dominio.FormaPagamento;
import br.com.cafenamao.dominio.StatusPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representação de {@link br.com.cafenamao.dominio.Pedido} no banco.
 *
 * <p>O identificador numérico interno dá a ordem real de inserção; o número do
 * pedido continua sendo a chave de negócio, com restrição de unicidade.</p>
 */
@Entity
@Table(name = "pedido")
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "numero", length = 20, nullable = false, unique = true)
    private String numero;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Convert(converter = FormaPagamentoConverter.class)
    @Column(name = "forma_pagamento", length = 30, nullable = false)
    private FormaPagamento formaPagamento;

    @Convert(converter = StatusPedidoConverter.class)
    @Column(name = "status", length = 30, nullable = false)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<ItemPedidoEntity> itens = new ArrayList<>();

    protected PedidoEntity() {
        // Exigido pelo JPA.
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public List<ItemPedidoEntity> getItens() {
        return itens;
    }
}
