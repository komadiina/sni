package org.unibl.etf.sni.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.unibl.etf.sni.model.Otp;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    @Query("select o from Otp o where o.username = ?1")
    Optional<Otp> findByUsername(String username);
}
