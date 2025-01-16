package org.unibl.etf.sni.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import com.stripe.model.Product;
import jakarta.ws.rs.GET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.paypal.response.ProductCatalog;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.*;

@RestController
@RequestMapping("/api/paypal")
public class PaypalController {

    private final ObjectMapper objectMapper;

    @Autowired
    private final PaypalServerSdkClient client;

    @Value("${paypal.client.id}")
    private String PAYPAL_CLIENT_ID;

    @Value("${paypal.client.secret}")
    private String PAYPAL_CLIENT_SECRET;

    public PaypalController(ObjectMapper objectMapper, @Qualifier("customPaypalClient") PaypalServerSdkClient client) {
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> request) {
        try {
            String cart = objectMapper.writeValueAsString(request.get("cart"));
            Order response = createOrder(cart);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/catalog/{productID}")
    public ResponseEntity<?> getProduct(@PathVariable String productID) {
        try {
            String url = "https://api-m.sandbox.paypal.com/v1/catalogs/products/" + productID;
            SimpleResponse response = getToken();
            String token = response.getAditional("access_token").toString();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> httpResponse = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 404) {
                response.setAdditional(new HashMap<>());
                response.setMessage("Product not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            JSONObject jsonObject = new JSONObject(httpResponse.body());
            Product product = objectMapper.readValue(jsonObject.toString(), Product.class);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();

            SimpleResponse response = new SimpleResponse();
            response.setMessage("Internal server error occurred, please try again later.");
            response.addAditional("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/catalog")
    public ResponseEntity<?> getCatalog() {
        SimpleResponse response = getToken();
        String token = response.getAditional("access_token").toString();
        String url = "https://api-m.sandbox.paypal.com/v1/catalogs/products";

        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            java.net.http.HttpResponse<String> httpResponse = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONObject(httpResponse.body()).getJSONArray("products");
            List<ProductCatalog> catalog = objectMapper.readValue(jsonArray.toString(), List.class);
            return new ResponseEntity<>(catalog, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/order/{orderID}/capture")
    public ResponseEntity<Order> captureOrder(@PathVariable String orderID) {
        try {
            Order response = captureOrders(orderID);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SimpleResponse getToken() {
        try {
            String base64 = PAYPAL_CLIENT_ID + ":" + PAYPAL_CLIENT_SECRET;
            String token = "Basic " + Base64.getEncoder().encodeToString(base64.getBytes());
            String url = "https://api-m.sandbox.paypal.com/v1/oauth2/token";

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", token)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                    .build();

            java.net.http.HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            // read "access_token" field from JSON-formatted response
            JSONObject jsonObject = new JSONObject(response.body());
            SimpleResponse tokenResponse = new SimpleResponse();
            tokenResponse.setMessage("Authenticated.");
            tokenResponse.addAditional("access_token", jsonObject.getString("access_token"));
            tokenResponse.addAditional("expires_in", jsonObject.getInt("expires_in"));
            tokenResponse.addAditional("scope", jsonObject.getString("scope"));
            tokenResponse.addAditional("nonce", jsonObject.getString("nonce"));

            return tokenResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Order createOrder(String cart) throws IOException, ApiException {
        OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
                null,
                new OrderRequest.Builder(
                        CheckoutPaymentIntent.fromString("CAPTURE"),
                        Arrays.asList(
                                new PurchaseUnitRequest.Builder(
                                        new AmountWithBreakdown.Builder(
                                                "USD",
                                                "100"
                                        ).build()
                                )

                                        .build()
                        )
                )

                        .build()
        ).build();
        OrdersController ordersController = client.getOrdersController();
        ApiResponse<Order> apiResponse = ordersController.ordersCreate(ordersCreateInput);
        return apiResponse.getResult();
    }

    private Order captureOrders(String orderID) throws IOException, ApiException {
        OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(
                orderID,
                null)
                .build();
        OrdersController ordersController = client.getOrdersController();
        ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
        return apiResponse.getResult();
    }
}
