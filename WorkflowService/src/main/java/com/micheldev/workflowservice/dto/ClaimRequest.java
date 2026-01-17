package com.micheldev.workflowservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {
    private String nom;
    private String policyNumber;
    private String claimType;
    private BigDecimal claimedAmount;
    private String commentaire;
}