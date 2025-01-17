package org.unibl.etf.sni.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "balance")
@Data
public class StripeProduct {

    @Id
    @Column(name = "stripe_id", nullable = false, unique = true, columnDefinition = "varchar(96)")
    private String id;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(256)")
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(16,2)")
    private Double price;

    public StripeProduct() {}

    public StripeProduct(String productId, String name, String description, Double price) {
        this.id = productId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String productId) {
        this.id = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "StripeProduct{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
