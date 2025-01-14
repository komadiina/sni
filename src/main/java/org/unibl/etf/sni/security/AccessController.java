package org.unibl.etf.sni.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.unibl.etf.sni.auth.JwtStore;
import org.unibl.etf.sni.auth.RegisterRequest;
import org.unibl.etf.sni.auth.RegistrationRequestResponse;
import org.unibl.etf.sni.model.ParsableJwt;
import org.unibl.etf.sni.model.User;
import org.unibl.etf.sni.service.BalanceService;
import org.unibl.etf.sni.service.UserService;

import java.lang.reflect.Field;
import java.util.Base64;

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

    public void registerUser(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.addUser(user);
    }

    public boolean validBalanceAmount(User user, double requiredAmount) {
        return balanceService.getBalance(user.getUsername()).getAmount() >= requiredAmount;
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
        ParsableJwt jwt = new ParsableJwt(bearerToken);

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

    public void logout(String token) {
        ParsableJwt jwt = JwtStore.getInstance().getToken(token);

        jwt.invalidate();
        JwtStore.getInstance().updateToken(jwt);

        JwtStore.getInstance().unassignToken(jwt.getPayload().getSub());
        JwtStore.getInstance().removeToken(jwt);
    }
}
