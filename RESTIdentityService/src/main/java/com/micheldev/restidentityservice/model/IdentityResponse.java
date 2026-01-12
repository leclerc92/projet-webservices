package com.micheldev.restidentityservice.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class IdentityResponse {
    private boolean valid;
    private String message;
}