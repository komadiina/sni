package org.unibl.etf.sni.controller.request;

public class TokenInfo {
    private String token;

    public TokenInfo() {}

    public TokenInfo(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
