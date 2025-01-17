package org.unibl.etf.sni.service;

import com.stripe.param.PaymentIntentCreateParams;
import org.unibl.etf.sni.model.stripe.*;
import com.stripe.Stripe;
import com.stripe.model.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StripeService {
    @Value("${stripe.secret}")
    private String stripeApiKey;

    @Value("${stripe.publishable}")
    private String stripePublishableApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String getSecretKey() {
        return stripeApiKey;
    }

    public String getPublishableKey() {
        return stripePublishableApiKey;
    }

    public PaymentIntent createPaymentIntent(StripePaymentIntentDto dto) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(dto.getAmount())
                    .setCurrency("eur")
                    .setPaymentMethod(dto.getPaymentMethod())
                    .addPaymentMethodType("card")
                    .build();

            return PaymentIntent.create(params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
