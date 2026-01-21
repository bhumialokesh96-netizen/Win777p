package com.win777.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SmsVerifyRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Mobile number is required")
    private String mobile;
    
    @NotBlank(message = "Message content is required")
    private String messageContent;
    
    private String verificationCode;
}
