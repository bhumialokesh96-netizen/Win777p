package com.win777.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Mobile number is required")
    private String mobile;
    
    @NotBlank(message = "New password is required")
    private String newPassword;
}
