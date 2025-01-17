package org.unibl.etf.sni.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PriceRetrieveParams;
import com.stripe.param.ProductListParams;
import org.unibl.etf.sni.config.GmailConfig;
import org.unibl.etf.sni.mail.GmailHTMLSender;
import org.unibl.etf.sni.model.Transaction;
import org.unibl.etf.sni.model.stripe.*;
import com.stripe.Stripe;
import com.stripe.model.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.unibl.etf.sni.mail.GmailAPIClient.getCredentials;

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

    public Product getProductByID(String productID) {
        try {
            return Product.retrieve(productID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getProductPrice(String productID) {
        try {
            ProductListParams params = ProductListParams.builder()
                    .setActive(true)
                    .build();

            ProductCollection collection = Product.list(params);
            for (Product product : collection.getData()) {
                if (product.getId().equals(productID)) {
                    try {
                        Price price = Price.retrieve(product.getDefaultPrice());
                        return price.getUnitAmount() / 100.0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendMail(String recipient, Transaction transaction, Product productDetails) {
        String htmlBody = String.format(
            "<html>" +
                    "<body>" +
                        "<p>Thank you for purchasing from our service!</p>" +
                        "<p>Transaction ID: %s</p>" +
                        "<p>Product: %s</p>" +
                        "<p>Total: %s</p>" +
                        "<p>Time of transaction: %s</p>" +
                    "</body>" +
                "</html>",
            transaction.getId(),
            productDetails.getName(),
            transaction.getTotal(),
            transaction.getTimestamp()
        );

        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GmailConfig.JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(GmailConfig.APPLICATION_NAME)
                    .build();

            GmailHTMLSender.sendHtmlEmail(
                    service,
                    "me",
                    recipient,
                    "bigblue9992@gmail.com",
                    "Transaction information - " + productDetails.getName(),
                    htmlBody);
        } catch (Exception ex) {
            System.err.println("Unable to send email to " + recipient + ", reason: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
