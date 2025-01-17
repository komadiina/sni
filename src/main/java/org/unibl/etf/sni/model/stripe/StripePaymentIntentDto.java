package org.unibl.etf.sni.model.stripe;

public class StripePaymentIntentDto {
    private Long amount;
    private String currency;
    private String paymentMethod;


    public StripePaymentIntentDto() {}

    public StripePaymentIntentDto(Long amount, String currency, String paymentMethod) {
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "StripePaymentIntentDto{" +
                "amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
