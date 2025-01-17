package org.unibl.etf.sni.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductListParams;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.stripe.*;
import org.unibl.etf.sni.service.StripeService;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    @Autowired
    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<?> createCardToken(@RequestBody StripePaymentIntentDto model) {
        SimpleResponse response = new SimpleResponse();

        try {
            // ne moze da mi serijalizuje PaymentIntent ??? ALOOO JACKSON
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(model);

            return new ResponseEntity<>(paymentIntent.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(model.toString());
            System.err.println(e.getMessage());

            e.printStackTrace();
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // ovo sve ispod sam mogao malo organizovati u StripeService metode ALIIIII MALO VREMENA PUNO POSLAAAAA ALOOOOO
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestBody StripeProductDto dto) {
        SimpleResponse response = new SimpleResponse();

        // ??? kreira proizvod i vrati id sve super ali ga ne mogu get-ovati??
        // Invalid null ID found for url path formatting. This can be because your string ID argument to the API method is null, or the ID field in your stripe object instance is null.
        try {
            ProductCreateParams params = ProductCreateParams.builder()
                    .setName(dto.getName())
                    .setDescription(dto.getDescription())
                    .setActive(true)
                    .build();

            Product product = Product.create(params);
            Long priceValue = ((Double)(dto.getPrice() * 100.0)).longValue();

            // update price
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setCurrency("usd")
                    .setProduct(product.getId())
                    .build();

            Price price = Price.create(priceParams);

            dto.setProductId(product.getId());
            response.setMessage("Product creation successful.");
            response.addAditional("product", dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (StripeException e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product")
    public ResponseEntity<?> getProducts(@RequestParam(name = "limit", required = false) Long limit) {
        try {
            Stripe.apiKey = stripeService.getSecretKey();

            var builder = ProductListParams.builder()
                    .setActive(true);

            if (limit != null)
                builder.setLimit(limit);

            ProductListParams params = builder.build();
            ProductCollection collection = Product.list(params);

            List<StripeProductDto> productDtos = new ArrayList<>();
            collection.getData().forEach((product) -> {
                Double productPrice = 0.0;

                try {
                    productPrice = Price.retrieve(product.getDefaultPrice()).getUnitAmount() / 100.0;
                } catch (StripeException e) {
                    e.printStackTrace();
                    return;
                }

                if (limit != null && productDtos.size() >= limit)
                    return;

                productDtos.add(new StripeProductDto(
                   product.getId(),
                   product.getName(),
                   product.getDescription(),
                   productPrice
                ));
            });

            SimpleResponse response = new SimpleResponse();
            response.setMessage(String.format("Fetched %d products.", productDtos.size()));
            response.addAditional("products", productDtos);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (StripeException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable String productId) {
        try {
            Stripe.apiKey = stripeService.getSecretKey();

            Product product = Product.retrieve(productId);
            Price price = Price.retrieve(product.getDefaultPrice());
            Double priceValue = price.getUnitAmount() / 100.0;

            StripeProductDto productDto = new StripeProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                priceValue
            );

            SimpleResponse response = new SimpleResponse();
            response.setMessage("Product retrieval successful.");
            response.addAditional("product", productDto);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (StripeException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/config")
    public ResponseEntity<?> config() {
        SimpleResponse response = new SimpleResponse();
        try {
            response.setMessage("Config retrieval successful.");
            response.addAditional("publishableKey", stripeService.getPublishableKey());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
