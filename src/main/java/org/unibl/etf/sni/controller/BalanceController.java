package org.unibl.etf.sni.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.Balance;
import org.unibl.etf.sni.model.User;
import org.unibl.etf.sni.security.AccessController;
import org.unibl.etf.sni.security.ParsableJwt;
import org.unibl.etf.sni.service.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessController accessController;

    @GetMapping("/{username}")
    public ResponseEntity<?> getBalance(@PathVariable(name = "username") String username, HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();

        if (!accessController.isAdministrator(AccessController.extractToken(request.getHeader("Authorization")))) {
            return new ResponseEntity<>(new SimpleResponse("Insufficient permissions.", new HashMap<>()), HttpStatus.UNAUTHORIZED);
        }

        ResponseEntity<?> responseEntity = accessController.validateRequest(request);
        if (responseEntity != null) {
            return responseEntity;
        }

        Balance balance = balanceService.getBalance(username);
        response.setMessage("Request successful.");
        response.addAditional("balance", balance.getAmount().toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyBalance(HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        ResponseEntity<?> responseEntity = accessController.validateRequest(request);
        if (responseEntity != null) {
            return responseEntity;
        }
        ParsableJwt jwt = JwtStore.getInstance().getToken(AccessController.extractToken(request.getHeader("Authorization")));

        response.setMessage("Valid token.");
        response.addAditional("balance", balanceService.getBalance(jwt.getPayload().getSub()).getAmount().toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/me")
    public ResponseEntity<?> addMyBalance(@RequestBody Map<String, String> body, HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        Double amount;
        try {
            amount = Double.parseDouble(body.get("amount"));
        } catch (Exception e) {
            response.setMessage("Request failed.");
            response.addAditional("reason", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String token = AccessController.extractToken(request.getHeader("Authorization"));
        ParsableJwt jwt = JwtStore.getInstance().getToken(token);

        String username = jwt.getPayload().getSub();
        User user = userService.findByUsername(username);
        System.out.println("1");
        if (user == null) {
            accessController.invalidateJwt(username);
            response.setMessage("Malicious request detected - token invalidated..");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (!accessController.validBalanceAmount(user, amount)) {
            accessController.invalidateJwt(username);
            response.setMessage("Malicious request detected - token invalidated..");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        balanceService.increase(username, amount);
        response.setMessage("Successfully increased balance.");
        response.addAditional("balance", balanceService.getBalance(username).getAmount().toString());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<?> addBalance(@RequestParam(name = "amount") Double amount, HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        if (!accessController.isAdministrator(AccessController.extractToken(request.getHeader("Authorization")))) {
            return new ResponseEntity<>(new SimpleResponse("Insufficient permissions.", new HashMap<>()), HttpStatus.UNAUTHORIZED);
        }

        ResponseEntity<?> responseEntity = accessController.validateRequest(request);
        if (responseEntity != null) {
            return responseEntity;
        }
        ParsableJwt jwt = JwtStore.getInstance().getToken(AccessController.extractToken(request.getHeader("Authorization")));

        response.setMessage("Valid token.");
        response.addAditional("balance", balanceService.increase(jwt.getPayload().getSub(), amount).getAmount().toString());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping
    public ResponseEntity<?> updateBalance(@RequestParam(name = "amount") Double amount, HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        if (!accessController.isAdministrator(AccessController.extractToken(request.getHeader("Authorization")))) {
            return new ResponseEntity<>(new SimpleResponse("Insufficient permissions.", new HashMap<>()), HttpStatus.UNAUTHORIZED);
        }

        ResponseEntity<?> responseEntity = accessController.validateRequest(request);
        if (responseEntity != null) {
            return responseEntity;
        }
        ParsableJwt jwt = JwtStore.getInstance().getToken(AccessController.extractToken(request.getHeader("Authorization")));

        response.setMessage("Valid token.");
        Balance balance = balanceService.getBalance(jwt.getPayload().getSub());
        balance.setAmount(amount);
        balanceService.updateBalance(balance.getUsername(), balance);
        response.setMessage("Successfully updated balance.");
        response.addAditional("balance", balance.getAmount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
