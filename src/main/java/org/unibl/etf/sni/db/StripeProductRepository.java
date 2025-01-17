package org.unibl.etf.sni.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.unibl.etf.sni.model.StripeProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface StripeProductRepository extends JpaRepository<StripeProduct, Long> {
    @Query("select p from StripeProduct p where p.id = ?1")
    Optional<StripeProduct> findById(int id);

    @Query("select p from StripeProduct p where p.name = ?1")
    List<StripeProduct> findAllByName(String name);
}
