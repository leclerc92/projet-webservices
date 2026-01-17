package com.micheldev.workflowservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    
    @JsonProperty("id")
    private String claimId;
    
    @JsonProperty("clientId")
    private String clientId;
    
    @JsonProperty("typeSinistre")
    private String claimType;
    
    @JsonProperty("montant")
    private BigDecimal amount;

    @JsonProperty("commentaire")
    private String comment;
    
    @JsonProperty("statut")
    private String status;
    
    @JsonProperty("dateCreation")
    private String createdAt;  // Changez en String pour correspondre au format GraphQL


}