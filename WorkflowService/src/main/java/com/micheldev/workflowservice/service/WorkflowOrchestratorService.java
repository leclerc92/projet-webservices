package com.micheldev.workflowservice.service;

import com.micheldev.workflowservice.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkflowOrchestratorService {

    private final RestClient restClient;

    @Value("${services.identity.url}")
    private String identityServiceUrl;

    @Value("${services.policy.url}")
    private String policyServiceUrl;

    @Value("${services.claims.url}")
    private String claimsServiceUrl;

    public WorkflowOrchestratorService() {
        this.restClient = RestClient.create();
    }

    /**
     * Orchestrates calls to various services to retrieve client information.
     * 1) REST - Verify identity
     * 2) SOAP - Verify policy
     * 3) GraphQL - Get claims
     */
    public ClientInfoResponse getClientInfo(String name, String clientId, String policyNumber) {
        log.info("Fetching client info for clientId: {}, name: {}, policyNumber: {}", clientId, name, policyNumber);

        // 1) Appel REST - Vérification identité
        boolean identityValid = verifyIdentity(clientId, name);
        if (!identityValid) {
            log.warn("Identity verification failed for clientId: {}", clientId);
            return null;
        }
        log.info("Identity verified successfully");

        // 2) Appel SOAP - Vérification policy
        boolean policyValid = verifyPolicy(policyNumber);
        if (!policyValid) {
            log.warn("Policy verification failed for policyNumber: {}", policyNumber);
            return null;
        }
        log.info("Policy verified successfully");

        // 3) Appel GraphQL - Récupération des claims
        List<ClaimResponse> claims = getClaims(clientId);
        log.info("Retrieved {} claims for client", claims.size());

        return ClientInfoResponse.builder()
                .clientId(clientId)
                .name(name)
                .policyNumber(policyNumber)
                .claims(claims)
                .build();
    }

    /**
     * Appel REST vers IdentityService
     */
    private boolean verifyIdentity(String clientId, String name) {
        try {
            IdentityRequest request = IdentityRequest.builder()
                    .customerId(clientId)
                    .fullName(name)
                    .build();

            IdentityResponse response = restClient.post()
                    .uri(identityServiceUrl + "/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(IdentityResponse.class);

            return response != null && response.isValid();
        } catch (Exception e) {
            log.error("Error calling Identity Service: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Appel SOAP vers PolicyService
     */
    private boolean verifyPolicy(String policyNumber) {
        try {
            String soapRequest = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:soap="http://www.example.com/soap-api">
                    <soapenv:Header/>
                    <soapenv:Body>
                        <soap:getPolicyRequest>
                            <soap:policyNumber>%s</soap:policyNumber>
                            <soap:claimType>DEFAULT</soap:claimType>
                        </soap:getPolicyRequest>
                    </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(policyNumber);

            String response = restClient.post()
                    .uri(policyServiceUrl)
                    .contentType(MediaType.TEXT_XML)
                    .body(soapRequest)
                    .retrieve()
                    .body(String.class);

            // Parse simple de la réponse SOAP pour extraire <valid>
            return response != null && response.contains("<valid>true</valid>");
        } catch (Exception e) {
            log.error("Error calling Policy Service: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Appel GraphQL vers ClaimTrackingService
     */
    @SuppressWarnings("unchecked")
    private List<ClaimResponse> getClaims(String clientId) {
        try {
            String graphqlQuery = """
                {
                    "query": "{ reclamations { id clientId typeSinistre montant statut dateCreation } }"
                }
                """;

            Map<String, Object> response = restClient.post()
                    .uri(claimsServiceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(graphqlQuery)
                    .retrieve()
                    .body(Map.class);

            List<ClaimResponse> claims = new ArrayList<>();
            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                List<Map<String, Object>> reclamations = (List<Map<String, Object>>) data.get("reclamations");

                if (reclamations != null) {
                    for (Map<String, Object> rec : reclamations) {
                        // Filtre par clientId
                        if (clientId.equals(rec.get("clientId"))) {
                            claims.add(ClaimResponse.builder()
                                    .claimId((String) rec.get("id"))
                                    .status((String) rec.get("statut"))
                                    .message((String) rec.get("typeSinistre"))
                                    .claimedAmount(rec.get("montant") != null
                                            ? new BigDecimal(rec.get("montant").toString())
                                            : null)
                                    .createdAt(LocalDateTime.now())
                                    .build());
                        }
                    }
                }
            }
            return claims;
        } catch (Exception e) {
            log.error("Error calling Claims Service: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Orchestrates calls to various services to submit a claim.
     */
    public ClaimResponse submitClaim(ClaimRequest request) {
        log.info("Submitting claim for policy: {}, type: {}, amount: {}",
                request.getPolicyNumber(), request.getClaimType(), request.getClaimedAmount());

        return null;
    }
}