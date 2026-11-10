package br.com.cafenamao.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Representação de {@link br.com.cafenamao.dominio.Bebida} no banco.
 *
 * <p>A coluna {@code ordem} preserva a ordem de exibição do catálogo, algo que o
 * domínio não precisa carregar.</p>
 */
@Entity
@Table(name = "bebida")
public class BebidaEntity {

    @Id
    @Column(name = "id", length = 120, nullable = false)
    private String id;

    @Column(name = "nome", length = 120, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 500, nullable = false)
    private String descricao;

    @Column(name = "categoria", length = 80, nullable = false)
    private String categoria;

    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    protected BebidaEntity() {
        // Exigido pelo JPA.
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }
}
