package com.win777.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigRequest {
    
    private String configKey;
    private String configValue;
    private String configType;
    private String description;
}
