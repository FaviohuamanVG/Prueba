package com.techtrend.catalog.model;

import java.math.BigDecimal;

/**
 * Entidad Product que representa un producto en el catálogo
 */
public class Product {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    public Product() {}

    public Product(String id, String name, BigDecimal price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean hasStock(Integer requestedQuantity) {
        return this.quantity != null && requestedQuantity != null && this.quantity >= requestedQuantity;
    }

    public boolean isAvailable() {
        return this.quantity != null && this.quantity > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id.equals(product.id) && 
               name.equals(product.name) && 
               price.equals(product.price) && 
               quantity.equals(product.quantity);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, price, quantity);
    }
}