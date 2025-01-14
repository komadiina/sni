package org.unibl.etf.sni.controller.response;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashMap;
import java.util.List;

@JsonPOJOBuilder
public final class SimpleResponse {
    private String message;
    private HashMap<String, String> additional;

    public SimpleResponse() {
        message = "";
        additional = new HashMap<>();
    }

    public SimpleResponse(String message, HashMap<String, String> additional) {
        this.message = message;
        this.additional = additional;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, String> getAdditional() {
        return additional;
    }

    public void setAdditional(HashMap<String, String> additional) {
        this.additional = additional;
    }

    public void addAditional(String key, String value) {
        this.additional.put(key, value);
    }
}
