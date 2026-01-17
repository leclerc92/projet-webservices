package com.micheldev.workflowservice.service;

import com.micheldev.workflowservice.client.GraphQLClient;
import com.micheldev.workflowservice.dto.*;

import ch.qos.logback.core.net.server.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkflowOrchestratorService {

    private final RestClient restClient;
    private final GraphQLClient graphQLClient;

    @Value("${rest.identity.service.url}")
    private String identityServiceUrl;

    @Value("${soap.policy.service.url}")
    private String policyServiceUrl;

    public WorkflowOrchestratorService(@Value("${graphql.claims.service.url}") String claimsServiceUrl) {
        this.restClient = RestClient.create();
        this.graphQLClient = new GraphQLClient(claimsServiceUrl);
    }

    /**
     * Orchestrates calls to various services to retrieve client information.
     * 1) REST - Verify identity
     * 2) SOAP - Verify policy
     * 3) GraphQL - Get claims
     */
    public ClientInfoResponse getClientInfo(ClientInfoRequest request) {
        log.info("Fetching client info for clientId: {}, name: {}, policyNumber: {}", request.getClientId(), request.getName(), request.getPolicyNumber());

        // 1) Appel REST - Vérification identité
        boolean identityValid = verifyIdentity(request.getClientId(), request.getName());
        if (!identityValid) {
            log.warn("Identity verification failed for clientId: {}", request.getClientId());
            return null;
        }
        log.info("Identity verified successfully");
        if (!identityValid) {
           
        }
        // 2) Appel SOAP - Vérification policy
        boolean policyValid = verifyPolicy(request.getPolicyNumber());
        if (!policyValid) {
            log.warn("Policy verification failed for policyNumber: {}", request.getPolicyNumber());
            return null;
        }
        log.info("Policy verified successfully");

        // 3) Appel GraphQL - Récupération des claims
        List<ClaimResponse> claims = getClaims(request.getClientId());
        log.info("Retrieved {} claims for client", claims.size());

        return ClientInfoResponse.builder()
                .clientId(request.getClientId())
                .name(request.getName())
                .policyNumber(request.getPolicyNumber())
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

            log.info("SOAP Response: {}", response);

            // Parse simple - cherche "true" dans le champ valid (avec ou sans namespace)
            return response != null && response.contains(">true</");
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
            // Requête GraphQL avec variable typée
            String query = """
                query($clientId: String!) {
                    reclamationByClientId(clientId: $clientId) {
                        id
                        clientId
                        typeSinistre
                        montant
                        statut
                        dateCreation
                        commentaire
                    }
                }
                """;

            // Exécuter la requête via le client GraphQL
            List<Map<String, Object>> data = graphQLClient.executeAndExtract(query, Map.of("clientId", clientId), "reclamationByClientId");

            if (data == null) {
                return new ArrayList<>();
            }


            ObjectMapper objectMapper = new ObjectMapper();            
            List<ClaimResponse> claims = data.stream()
                    .map(reclamation -> objectMapper.convertValue(reclamation, ClaimResponse.class))
                    .collect(Collectors.toList());
            // Mapper les données vers ClaimResponse
            return claims;

        } catch (Exception e) {
            log.error("Error calling Claims Service: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les claims d'un client via GraphQL (méthode publique pour test)
     */
    public List<ClaimResponse> getClaimsByClientId(String clientId) {
        log.info("Fetching claims for clientId: {}", clientId);
        return getClaims(clientId);
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