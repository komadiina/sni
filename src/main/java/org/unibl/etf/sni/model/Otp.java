package org.unibl.etf.sni.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "varchar(48)")
    private String username;

    @Column(name = "code", nullable = false, columnDefinition = "varchar(6)")
    private String otpValue;

    @Column(name = "valid_until", nullable = false, columnDefinition = "datetime")
    private LocalDateTime expiryTime;

    @Column(name = "used", nullable = false, columnDefinition = "boolean")
    private boolean used;

    @Column(name = "password", nullable = false, columnDefinition = "text")
    private String password;

    public Otp() {}

    public Otp(String username, String password, String otp, LocalDateTime expiryTime) {
        this.username = username;
        used = false;
        this.otpValue = otp;
        this.expiryTime = expiryTime;
        this.password = password;
    }

    public Otp(String otpValue, String password, LocalDateTime expiryTime, boolean used, String username) {
        this.otpValue = otpValue;
        this.password = password;
        this.expiryTime = expiryTime;
        this.used = used;
        this.username = username;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getOtpValue() {
        return otpValue;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Otp otp = (Otp) o;
        return Objects.equals(otpValue, otp.otpValue) && Objects.equals(expiryTime, otp.expiryTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(otpValue, expiryTime);
    }
}
