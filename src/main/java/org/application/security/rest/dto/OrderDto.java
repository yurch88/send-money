package org.application.security.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OrderDto {
    @NotNull
    @Size(min = 11, max = 11)
    private String phoneNumber;
    @NotNull
    private Float amount;

    @NotNull
    private String status;

    public OrderDto(String phoneNumber, Float amount, String status) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
                "\"phoneNumber\":" + "\"" + phoneNumber + "\"" +
                ", \"amount\":" + amount +
                ", \"status\":" + "\"" + status + "\"" +
                "}";
    }
}

