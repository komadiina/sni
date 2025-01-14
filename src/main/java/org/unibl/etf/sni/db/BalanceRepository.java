package org.unibl.etf.sni.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.unibl.etf.sni.model.Balance;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    @Query("select b from Balance b where b.username = ?1")
    Optional<Balance> findByUsername(String username);
}
