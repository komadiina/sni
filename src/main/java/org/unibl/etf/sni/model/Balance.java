package org.unibl.etf.sni.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.unibl.etf.sni.db.BalanceRepository;

@Entity
@Table(name = "balance")
@Data
public class Balance {
    @Id
    @Column(name = "username", nullable = false, unique = true, columnDefinition = "varchar(48)")
    private String username;

    @Column(name = "amount", nullable = false, columnDefinition = "decimal(16,2)")
    private Double amount;

    public Balance() {}

    public Balance(String username, Double amount) {
        this.username = username;
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
