package com.micheldev.workflowservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfoResponse {
    private String clientId;
    private String name;
    private String policyNumber;
    private List<ClaimResponse> claims;
    private String errorMessage;
}