package org.unibl.etf.sni.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.ProductListParams;
import com.stripe.param.ProductUpdateParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.Transaction;
import org.unibl.etf.sni.model.stripe.*;
import org.unibl.etf.sni.security.AccessController;
import org.unibl.etf.sni.security.ParsableJwt;
import org.unibl.etf.sni.service.BalanceService;
import org.unibl.etf.sni.service.StripeService;
import org.unibl.etf.sni.service.TransactionService;
import org.unibl.etf.sni.service.UserService;

import java.net.PasswordAuthentication;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {
    @Autowired
    private final BalanceService balanceService;

    @Autowired
    private final TransactionService transactionService;

    @Autowired
    private final StripeService stripeService;

    @Autowired
    private final AccessController accessController;
    @Autowired
    private UserService userService;

    public StripeController(BalanceService balanceService, TransactionService transactionService, StripeService stripeService, AccessController accessController) {
        this.balanceService = balanceService;
        this.transactionService = transactionService;
        this.stripeService = stripeService;
        this.accessController = accessController;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<?> createCardToken(@RequestBody StripePaymentIntentDto model, HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();

        try {
            // fetch username from header
            ParsableJwt jwt = JwtStore.getInstance().getToken(AccessController.extractToken(request.getHeader("Authorization")));
            String username = jwt.getPayload().getSub();

            // check if user has enough balance
            Double productPrice = 0.0;

            if (model.getProductID() != null) {
                productPrice = stripeService.getProductPrice(model.getProductID());
            } else {
                response.setMessage("Product ID cannot be null.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Transaction transaction = new Transaction();

            // hotfix, jer moram konvertovati izmedju double i long
            model.setAmount(model.getAmount() * 100);

            if (!accessController.userHasEnoughBalance(username, productPrice)) {
                transaction.setAccepted(false);
                transaction.setUsername(username);
                transaction.setTimestamp(LocalDateTime.now());
                transaction.setStripeProductId(model.getProductID());
                transaction.setTotal(productPrice);
                transaction.setCurrency(model.getCurrency());
                transaction.setRejectReason("INSUFFICIENT_BALANCE");
                transactionService.addTransaction(transaction);

                response.setMessage("Not enough balance");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                transaction.setAccepted(true);
                transaction.setUsername(username);
                transaction.setTimestamp(LocalDateTime.now());
                transaction.setStripeProductId(model.getProductID());
                transaction.setTotal(productPrice);
                transaction.setCurrency(model.getCurrency());
                transaction = transactionService.addTransaction(transaction);

                balanceService.decreaseBalance(username, productPrice);
            }

            PaymentIntent paymentIntent = stripeService.createPaymentIntent(model);
            String email = userService.findByUsername(username).getEmail();
            stripeService.sendMail(email, transaction, Product.retrieve(model.getProductID()));
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

        try {
            Long priceValue = ((Double)(dto.getPrice() * 100.0)).longValue();
            Stripe.apiKey = stripeService.getSecretKey();


            ProductCreateParams productCreateParams = ProductCreateParams.builder()
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setActive(true)
                .build();

            Product product = Product.create(productCreateParams);
            dto.setProductId(product.getId());

            PriceCreateParams params = PriceCreateParams.builder()
                .setCurrency("usd")
                .setProduct(product.getId())
                .setUnitAmount(priceValue)
                .build();

            Price price = Price.create(params);

            // assign price and update the product
            ProductUpdateParams updateParams = ProductUpdateParams.builder()
                    .setDefaultPrice(price.getId())
                    .build();

            product.update(updateParams);
            dto.setProductId(price.getProduct());

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
                    System.out.println(product.getId() + ":" + product.getDefaultPrice());
                    productPrice = Price.retrieve(product.getDefaultPrice())
                            .getUnitAmount() / 100.0;
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
