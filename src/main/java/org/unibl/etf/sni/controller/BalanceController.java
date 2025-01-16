package org.unibl.etf.sni.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.Balance;
import org.unibl.etf.sni.security.AccessController;
import org.unibl.etf.sni.security.ParsableJwt;
import org.unibl.etf.sni.service.*;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessController accessController;

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

    @PostMapping
    public ResponseEntity<?> addBalance(@RequestParam(name = "amount") Double amount, HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
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
