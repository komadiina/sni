package org.unibl.etf.sni;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Key;

@SpringBootApplication(scanBasePackages = {"org.unibl.etf.sni", "org.unibl.etf.sni.controller", "org.unibl.etf.sni.service"})
@EnableJpaRepositories(basePackages = "org.unibl.etf.sni.db")
public class SniApplication {
    public static void main(String[] args) {
        SpringApplication.run(SniApplication.class, args);
    }
}
