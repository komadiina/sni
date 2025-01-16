package org.unibl.etf.sni.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.unibl.etf.sni.auth.*;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.*;
import org.unibl.etf.sni.security.*;
import org.unibl.etf.sni.service.*;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AccessController accessController;

    @Autowired
    private JwtService jwtService;

    public AuthController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        SimpleResponse response = new SimpleResponse();

        if (accessController.userAlreadySignedIn(request.getUsername())) {
            String token = JwtStore.getInstance().getTokenByUsername(request.getUsername()).getToken();

            System.out.println("Already logged in: " + request.getUsername() +", " + token);
            response.setMessage("Already logged in.");

            response.addAditional("token", token);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            response.setMessage("Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (!new BCryptPasswordEncoder().matches(request.getPassword(), user.getPassword())) {
            response.setMessage("Invalid credentials.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // generate & save otp
        Otp otp = otpService.generateOtp(user.getUsername(), request.getPassword());
        otpService.storeOtp(otp);

        // instantiate deletion task
        OtpDbManagerThread thread = new OtpDbManagerThread(user.getUsername(), otpService);
        thread.setDaemon(false);
        thread.start();

        // send email
        try {
            otpService.sendOtp(user.getEmail(), otp.getOtpValue());
        } catch (Exception e) {
            response.setMessage("Failed to send email.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.setMessage(
                String.format("An authentication code has been mailed to you (ending with %s), valid for 5 minutes.",
                user.getEmail().substring(user.getEmail().lastIndexOf('@') + 1)));
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping("/otp")
    public ResponseEntity<?> otp(@RequestParam(name = "username") String username, @RequestParam(name = "otp") String otp,
                                 HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();


        if (username == null || otp == null) {
            response.setMessage("Missing required query parameters (username, otp).");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            if (!otpService.validateOtp(username, otp)) {
                response.setMessage("An invalid, expired or used authentication code (OTP) has been provided.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception exception) {
            response.setMessage(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // invalidate otp
        String password = otpService.deleteOtp(username);

        // login user
        JwtAuthResponse jwtAuthResponse = authService.login(
                new AuthRequest(username, password),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        // assign jwt
        JwtStore.getInstance().assignToken(username, new ParsableJwt(jwtAuthResponse.getToken()));

        response.setMessage("Successfully logged in.");
        response.addAditional("token", jwtAuthResponse.getToken());
        response.addAditional("role", String.valueOf(userService.findByUsername(username).getRole()));
        ResponseEntity responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        SimpleResponse response = new SimpleResponse();
        System.out.println(request.formUser());

        if (!accessController.validateRegistrationRequest(request).isValid()) {
            response.setMessage(accessController.validateRegistrationRequest(request).getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        accessController.registerUser(request.formUser());
        response.setMessage("Successfully registered.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // create redirect to login page
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("https://localhost:3000/login"));

        String token = AccessController.extractToken(request.getHeader("Authorization"));
        accessController.logout(token);

        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/role")
    public ResponseEntity<?> getRole(HttpServletRequest request) {
        System.out.println("GET /api/auth/role");
        System.out.println("Authorization: " + request.getHeader("Authorization"));

        SimpleResponse response = new SimpleResponse();
        String authHeader = request.getHeader("Authorization");
        String token = AccessController.extractToken(authHeader);

        if (!accessController.authenticateRequest(request, response, token)) {
            response.setMessage("Invalid or expired token.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        ParsableJwt jwt = JwtStore.getInstance().getToken(AccessController.extractToken(authHeader));
        if (jwt == null) {
            response.setMessage("Token not found in context.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.setMessage("Valid token.");
        response.addAditional("role", jwt.getPayload().getRole());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/extend-token")
    public ResponseEntity<?> extendToken(HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        String authHeader = request.getHeader("Authorization");
        String expiredToken = AccessController.extractToken(authHeader);

        if (expiredToken == null || expiredToken.isEmpty()) {
            return new ResponseEntity<>("Token is required", HttpStatus.BAD_REQUEST);
        }

        try {
            ParsableJwt jwt = JwtStore.getInstance().getToken(AccessController.extractToken(authHeader));
            if (jwt == null) {
                response.setMessage("Token not found in context.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            if (!jwt.isExpired()) {
                response.setMessage("Token is not expired.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // replace token in JwtStore
            String newToken = jwtService.extendToken(expiredToken);
            JwtStore.getInstance().unassignToken(jwt.getPayload().getSub());
            JwtStore.getInstance().assignToken(jwt.getPayload().getSub(), new ParsableJwt(newToken));

            response.setMessage("Successfully extended token.");
            response.addAditional("token", newToken);

            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExpiredJwtException exception) {
            exception.printStackTrace();
            response.setMessage("Token is expired. Generating a new token.");
            String newToken = jwtService.extendToken(expiredToken);
            response.addAditional("token", newToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception exception) {
            response.setMessage(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
