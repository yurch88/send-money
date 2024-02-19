package org.application.security.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SendMoneyDto {

    @NotNull
    @Size(min = 11, max = 11)
    private String phoneNumber;

    @NotNull
    private Float amount;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
