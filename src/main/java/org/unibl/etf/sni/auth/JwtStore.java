package org.unibl.etf.sni.auth;

import org.unibl.etf.sni.security.ParsableJwt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class JwtStore {
    private static JwtStore instance = new JwtStore();
    private HashSet<ParsableJwt> tokens = new HashSet<>();
    private HashMap<String, ParsableJwt> assignedTokens = new HashMap<>();

    private JwtStore() {}

    public static JwtStore getInstance() {
        if (instance == null)
            instance = new JwtStore();

        return instance;
    }

    // to prevent malicious token usage, 'remember' who uses it
    public void assignToken(String username, ParsableJwt jwt) {
        assignedTokens.put(username, jwt);
        addToken(jwt);
    }

    public void unassignToken(String username) {
        ParsableJwt jwt = assignedTokens.get(username);
        assignedTokens.remove(username);
        removeToken(jwt);
        removeTokenByUsername(username);
    }

    // TODO: review if something breaks
    public boolean ownsToken(String username, ParsableJwt jwt) {
        return assignedTokens.containsKey(username) && assignedTokens.get(username).getToken().equals(jwt.getToken());
    }

    public void addToken(ParsableJwt token) {
        tokens.add(token);
    }

    public void removeTokenByUsername(String username) {
        tokens.removeIf(t -> t.getPayload().getSub().equals(username));
    }

    public void removeToken(ParsableJwt parsableJwt) {
        System.out.println("Removing: " + parsableJwt);
        tokens.remove(parsableJwt);
    }

    public List<ParsableJwt> getTokens() {
        return tokens.stream().toList();
    }

    public ParsableJwt getToken(String token) {
        return tokens.stream().filter(t -> t.getToken().equals(token)).findFirst().orElse(null);
    }

    public ParsableJwt hasToken(ParsableJwt jwt) {
        return tokens.stream().filter(t -> t.equals(jwt)).findFirst().orElse(null);
    }

    public ParsableJwt getTokenByUsername(String username) {
        return tokens.stream().filter(t -> t.getPayload().getSub().equals(username)).findFirst().orElse(null);
    }

    public ParsableJwt updateToken(ParsableJwt token) {
        removeToken(token);
        addToken(token);
        return token;
    }
}
