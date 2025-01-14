package org.unibl.etf.sni.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.sni.model.User;
import org.unibl.etf.sni.security.AccessController;
import org.unibl.etf.sni.service.UserService;

import java.net.Authenticator;
import java.util.List;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AccessController accessController;

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        User result = userService.addUser(user);

        if (result != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping
    public ResponseEntity<?> getUserByUsername(@RequestParam(name = "username", required = false) String username, HttpServletRequest request) {
        if (!accessController.isAuthenticated(request.getHeader("Authorization").split(" ")[1])) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (username == null) {
            List<User> result = userService.getAllUsers();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        User result = userService.findByUsername(username);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/test")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        String token = AccessController.extractToken(request.getHeader("Authorization"));
        if (token == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (!accessController.isAuthenticated(token)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(request.getHeader("Authorization"), HttpStatus.OK);
    }
}
