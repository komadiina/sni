package org.unibl.etf.sni.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unibl.etf.sni.db.StripeProductRepository;

@Deprecated
@Service
public class StripeProductDbService {

    @Autowired
    private StripeProductRepository stripeProductRepository;

    public StripeProductDbService(StripeProductRepository stripeProductRepository) {
        this.stripeProductRepository = stripeProductRepository;
    }
}
