package com.example.miniProjekt.model;

import jakarta.persistence.*;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

// SaleLine.java (surrogat PK + unikhed)
@Entity
@Table(
        name = "sale_line",
        uniqueConstraints = @UniqueConstraint(name = "uq_sale_product", columnNames = {"sale_id","product_id"})
)
public class SaleLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(optional = false) @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false) private int quantity;
    @Column(name="unit_price", nullable=false, precision=10, scale=2)
    private BigDecimal unitPrice;

    public SaleLine() {
    }

    public SaleLine(Long id, Sale sale, Product product, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.sale = sale;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
