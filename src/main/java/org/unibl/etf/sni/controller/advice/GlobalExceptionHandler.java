package org.unibl.etf.sni.controller.advice;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.security.AccessController;

@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<SimpleResponse> handleExpiredJwtException(ExpiredJwtException ex) {
//        SimpleResponse response = new SimpleResponse();
///*
//        // offer to extend the token
//        String authHeader = ex.getRequest().getHeader("Authorization");
//        String token = AccessController.extractToken(authHeader);
//        String newToken = JwtStore.getInstance().extendToken(token);
//
//        if (newToken != null) {
//            response.setMessage("Successfully extended token.");
//            response.addAditional("token", newToken);
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        }
// */
//        response.setMessage("JWT token has expired. Please log in again.");
//        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//    }
}
