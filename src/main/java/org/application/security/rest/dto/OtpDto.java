package org.application.security.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OtpDto {
    @NotNull
    @Size(min = 4, max = 4)
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
