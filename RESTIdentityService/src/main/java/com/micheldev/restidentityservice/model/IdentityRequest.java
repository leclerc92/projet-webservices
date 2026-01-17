package com.micheldev.restidentityservice.model;

import lombok.Data;

@Data
public class IdentityRequest {
    private String customerId;
    private String fullName;
}