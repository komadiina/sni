package org.unibl.etf.sni.controller.response;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashMap;

@JsonPOJOBuilder
public final class SimpleResponse {
    private String message;
    private HashMap<String, Object> additional;

    public SimpleResponse() {
        message = "";
        additional = new HashMap<>();
    }

    public SimpleResponse(String message, HashMap<String, Object> additional) {
        this.message = message;
        this.additional = additional;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, ?> getAdditional() {
        return additional;
    }

    public void setAdditional(HashMap<String, Object> additional) {
        this.additional = additional;
    }

    public void addAditional(String key, Object value) {
        this.additional.put(key, value);
    }

    public Object getAditional(String key) {
        return this.additional.get(key);
    }
}
