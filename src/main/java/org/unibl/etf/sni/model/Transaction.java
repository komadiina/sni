package org.unibl.etf.sni.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "int")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, columnDefinition = "varchar(48)")
    private String username;

    @Column(name = "stripe_product_id", nullable = false, columnDefinition = "varchar(256)")
    private String stripeProductId;

    @Column(name = "total", nullable = false, columnDefinition = "decimal(16,2)")
    private Double total;

    @Column(name = "accepted", nullable = false, columnDefinition = "boolean")
    private boolean accepted;

    @Column(name = "reject_reason", nullable = true, columnDefinition = "text")
    private String rejectReason;

    @Column(name = "timestamp", nullable = false, columnDefinition = "datetime")
    private LocalDateTime timestamp;

    @Column(name = "currency", nullable = false, columnDefinition = "varchar(3)")
    private String currency = "usd";

    public Transaction() {}

    public Transaction(Long id, String username, String stripeProductId, Double total, boolean accepted, String rejectReason, LocalDateTime timestamp, String currency) {
        this.id = id;
        this.username = username;
        this.stripeProductId = stripeProductId;
        this.total = total;
        this.accepted = accepted;
        this.rejectReason = rejectReason;
        this.timestamp = timestamp;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStripeProductId() {
        return stripeProductId;
    }

    public void setStripeProductId(String stripeProductId) {
        this.stripeProductId = stripeProductId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", stripeProductId='" + stripeProductId + '\'' +
                ", total=" + total +
                ", accepted=" + accepted +
                ", rejectReason='" + rejectReason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
