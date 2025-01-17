package org.unibl.etf.sni.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.auth.RegisterRequest;
import org.unibl.etf.sni.auth.RegistrationRequestResponse;
import org.unibl.etf.sni.controller.response.SimpleResponse;
import org.unibl.etf.sni.model.User;
import org.unibl.etf.sni.model.Balance;
import org.unibl.etf.sni.service.BalanceService;
import org.unibl.etf.sni.service.UserService;

@Service
public class AccessController {
    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    public RegistrationRequestResponse validateRegistrationRequest(RegisterRequest req) {
        // assure no fields are null
        if (!assureNonNullity(req))
            return new RegistrationRequestResponse(false, "All fields are required.");

        // username/email taken
        if (userService.findByUsername(req.getUsername()) != null
                || userService.findByEmail(req.getEmail()) != null)
            return new RegistrationRequestResponse(false, "Username or email already taken.");

        // invalid password
        if (req.getPassword().length() < 8 || !req.getPassword().matches("[a-zA-Z0-9!@#$%^&*()_+=<>{}]+"))
            return new RegistrationRequestResponse(false,
                    "Password must be at least 8 characters long, contain at least one letter, one number and one special character.");

        if (!req.getPasswordConfirmation().equalsIgnoreCase(req.getPassword()))
            return new RegistrationRequestResponse(false, "Passwords do not match.");

        return new RegistrationRequestResponse(true, "");
    }

    public boolean isAdministrator(String token) {
        if (token == null) return false;

        ParsableJwt jwt = new ParsableJwt(token);
        if (JwtStore.getInstance().getToken(token) == null) return false;
        return jwt.getPayload().getRole().equalsIgnoreCase("0");
    }

    public void registerUser(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.addUser(user);
        balanceService.addBalance(new Balance(user.getUsername(), 0.0));
    }

    public boolean validBalanceAmount(User user, double requiredAmount) {
        return requiredAmount <= 999.99;
//        return balanceService.getBalance(user.getUsername()).getAmount() >= requiredAmount;
    }

    public boolean userHasEnoughBalance(String username, Double required) {
        return balanceService.getBalance(username).getAmount() >= required;
    }

    private boolean assureNonNullity(RegisterRequest req) {
        if (notEmpty(req.getUsername()) && notEmpty(req.getPassword()) && notEmpty(req.getPasswordConfirmation())
                && notEmpty(req.getEmail()) && notEmpty(req.getFirstName()) && notEmpty(req.getLastName())
                && notEmpty(req.getContactPhone()) && notEmpty(req.getBillingAddress()))
            return true;
        else return false;
    }

    private boolean notEmpty(String str) {
        return str != null && !str.isEmpty() && !str.isBlank();
    }

    public Boolean isRequestAcceptable(HttpServletRequest request) {
        // cant be empty lol
        if (request == null) return false;

        // user must use Bearer authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return false;

        // token value field cannot be empty
        String token = AccessController.extractToken(authHeader);
        if (token == null) return false;

        // token must exist in current context
        ParsableJwt jwt = new ParsableJwt(token);
        if (JwtStore.getInstance().getToken(token) == null) return false;

        // cant be expired
        if (jwt.isExpired() || jwt.getPayload().getIat() > System.currentTimeMillis() / 1000) {
            return false;
        }

        // user must own the token
        if (!JwtStore.getInstance().ownsToken(jwt.getPayload().getSub(), jwt)) {
            return false;
        }

        // anything else?? idk
        return true;
    }

    public ParsableJwt invalidateJwt(@NotNull String username) {
        ParsableJwt jwt = JwtStore.getInstance().getTokenByUsername(username);
        jwt.invalidate();
        JwtStore.getInstance().updateToken(jwt);
        return jwt;
    }

    public ParsableJwt extendJwt(@NotNull String username) {
        ParsableJwt jwt = JwtStore.getInstance().getTokenByUsername(username);
        jwt.extend();
        JwtStore.getInstance().updateToken(jwt);
        return jwt;
    }

    public boolean isTokenExpired(@NotNull String username) {
        ParsableJwt jwt = JwtStore.getInstance().getTokenByUsername(username);
        return jwt.isExpired();
    }

    public boolean hasToken(@NotNull String username) {
        ParsableJwt jwt = JwtStore.getInstance().getTokenByUsername(username);
        return jwt != null;
    }

    public boolean isAuthenticated(@NotNull String bearerToken) {
        if (bearerToken == null)
            return false;

        ParsableJwt jwt = new ParsableJwt(bearerToken);
        if (JwtStore.getInstance().getToken(jwt.getToken()) == null) return false;

        // verify that token is not expired
        if (jwt.isExpired() || jwt.getPayload().getIat() > System.currentTimeMillis() / 1000) {
            return false;
        } else return jwt.getHeader().getAlg().equalsIgnoreCase("HS256");
    }

    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    }

    public boolean userAlreadySignedIn(String username) {
        return JwtStore.getInstance().getTokenByUsername(username) != null && !JwtStore.getInstance().getTokenByUsername(username).isExpired();
    }

    public void logout(String token) {
        ParsableJwt jwt = JwtStore.getInstance().getToken(token);
        JwtStore.getInstance().unassignToken(jwt.getPayload().getSub());
        JwtStore.getInstance().removeToken(jwt);
    }

    public boolean notSpoofed(String tokenValue, String userAgent, String remoteAddr) {
        try {
            ParsableJwt jwt = new ParsableJwt(tokenValue);
            ParsableJwt found = JwtStore.getInstance().getToken(jwt.getToken());

            if (userAgent != null && !userAgent.equals(found.getPayload().getUserAgent())) return false;
            else if (remoteAddr != null && !remoteAddr.equals(found.getPayload().getIp())) return false;

            return JwtStore.getInstance().hasToken(jwt) != null && jwt.equals(found);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public boolean authenticateRequest(HttpServletRequest request, SimpleResponse response, String token) {
        if (!isAuthenticated(token)) {
            response.addAditional("code", "401");
            response.setMessage("Unauthorized");
            return false;
        }

        if (!notSpoofed(token, request.getHeader("User-Agent"), request.getRemoteAddr())) {
            response.addAditional("code", "401");
            response.setMessage("Malicious token request detected.");
            logout(token);
            return false;
        }

        return true;
    }

    public ResponseEntity<?> validateRequest(HttpServletRequest request) {
        SimpleResponse response = new SimpleResponse();
        if (!isRequestAcceptable(request)) {
            response.setMessage("Invalid or expired token.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        return null;
    }
}
