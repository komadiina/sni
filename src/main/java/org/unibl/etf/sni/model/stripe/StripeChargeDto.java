package org.unibl.etf.sni.model.stripe;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class StripeChargeDto {
    private String stripeToken;
    private String username;
    private Double amount;
    private Boolean success;
    private String message;
    private String chargeId;
    private Map<String, Object> additionalInfo = new HashMap<>();

    public StripeChargeDto() {}

    public StripeChargeDto(String stripeToken, String username, Double amount, Boolean success, String message, String chargeId, Map<String, Object> additionalInfo) {
        this.stripeToken = stripeToken;
        this.username = username;
        this.amount = amount;
        this.success = success;
        this.message = message;
        this.chargeId = chargeId;
        this.additionalInfo = additionalInfo;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String toString() {
        return "StripeChargeDto{" +
                "stripeToken='" + stripeToken + '\'' +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", chargeId='" + chargeId + '\'' +
                ", additionalInfo=" + additionalInfo +
                '}';
    }
}
