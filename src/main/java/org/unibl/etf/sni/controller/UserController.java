package org.unibl.etf.sni.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.User;
import org.unibl.etf.sni.security.AccessController;
import org.unibl.etf.sni.security.ParsableJwt;
import org.unibl.etf.sni.service.UserService;

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
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping
    public ResponseEntity<?> getUserByUsername(@RequestParam(name = "username", required = false) String username, HttpServletRequest request) {
        if (!accessController.isAuthenticated(request.getHeader("Authorization").split(" ")[1])) {
            return new ResponseEntity<>(new SimpleResponse("Invalid or expired token.", null), HttpStatus.UNAUTHORIZED);
        }

        if (username == null) {
            List<User> result = userService.getAllUsers();
            result.forEach(user -> user.setPassword("<obscured>"));
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        User result = userService.findByUsername(username);
        if (result != null) {
            result.setPassword("<obscured>");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        String token = AccessController.extractToken(request.getHeader("Authorization"));

        if (token == null) {
            response.setMessage("Insufficient permissions.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (!accessController.authenticateRequest(request, response, token)) {
            response.setMessage("Invalid or expired token.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ParsableJwt jwt = JwtStore.getInstance().getToken(token);
        User user = userService.findByUsername(jwt.getPayload().getSub());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
