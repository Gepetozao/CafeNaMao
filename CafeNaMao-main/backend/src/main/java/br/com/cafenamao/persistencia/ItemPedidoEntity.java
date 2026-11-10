package br.com.cafenamao.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Item persistido de um pedido.
 *
 * <p>Os dados da bebida são copiados no momento do pedido. Assim, alterar ou
 * remover uma bebida do catálogo não muda o histórico nem os totais de pedidos
 * já confirmados.</p>
 */
@Entity
@Table(name = "pedido_item")
public class ItemPedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @Column(name = "bebida_id", length = 120, nullable = false)
    private String bebidaId;

    @Column(name = "bebida_nome", length = 120, nullable = false)
    private String bebidaNome;

    @Column(name = "bebida_descricao", length = 500, nullable = false)
    private String bebidaDescricao;

    @Column(name = "bebida_categoria", length = 80, nullable = false)
    private String bebidaCategoria;

    @Column(name = "bebida_preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal bebidaPreco;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    protected ItemPedidoEntity() {
        // Exigido pelo JPA.
    }

    public Long getId() {
        return id;
    }

    public PedidoEntity getPedido() {
        return pedido;
    }

    public void setPedido(PedidoEntity pedido) {
        this.pedido = pedido;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getBebidaId() {
        return bebidaId;
    }

    public void setBebidaId(String bebidaId) {
        this.bebidaId = bebidaId;
    }

    public String getBebidaNome() {
        return bebidaNome;
    }

    public void setBebidaNome(String bebidaNome) {
        this.bebidaNome = bebidaNome;
    }

    public String getBebidaDescricao() {
        return bebidaDescricao;
    }

    public void setBebidaDescricao(String bebidaDescricao) {
        this.bebidaDescricao = bebidaDescricao;
    }

    public String getBebidaCategoria() {
        return bebidaCategoria;
    }

    public void setBebidaCategoria(String bebidaCategoria) {
        this.bebidaCategoria = bebidaCategoria;
    }

    public BigDecimal getBebidaPreco() {
        return bebidaPreco;
    }

    public void setBebidaPreco(BigDecimal bebidaPreco) {
        this.bebidaPreco = bebidaPreco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
