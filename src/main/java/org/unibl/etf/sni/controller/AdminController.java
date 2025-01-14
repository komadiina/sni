package org.unibl.etf.sni.controller;

import jakarta.ws.rs.core.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
