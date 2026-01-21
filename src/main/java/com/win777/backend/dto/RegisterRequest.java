package com.win777.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid mobile number format")
    private String mobile;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private String deviceFingerprint;
}
