package com.micheldev.workflowservice.service;

import com.micheldev.workflowservice.client.GraphQLClient;
import com.micheldev.workflowservice.dto.*;

import com.micheldev.workflowservice.grpc.ServiceDetectionFraudeGrpc; //
import com.micheldev.workflowservice.grpc.RequeteFraude;
import com.micheldev.workflowservice.grpc.ReponseFraude;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class WorkflowOrchestratorService {

    private final RestClient restClient;
    private final GraphQLClient graphQLClient;
    private final ServiceDetectionFraudeGrpc.ServiceDetectionFraudeBlockingStub fraudStub;
    private final ManagedChannel channel;

    @Value("${rest.identity.service.url}")
    private String identityServiceUrl;

    @Value("${soap.policy.service.url}")
    private String policyServiceUrl;

    public WorkflowOrchestratorService(
        @Value("${graphql.claims.service.url}") String claimsServiceUrl,
        @Value("${grpc.fraud.service.host}") String grpcHost,
        @Value("${grpc.fraud.service.port}") int grpcPort) {
        this.restClient = RestClient.create();
        this.graphQLClient = new GraphQLClient(claimsServiceUrl);
        this.channel = ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
        this.fraudStub = ServiceDetectionFraudeGrpc.newBlockingStub(channel);
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
        log.info("Submitting claim for policy: {}, type: {}, amount: {}",request.getPolicyNumber(), request.getClaimType(), request.getClaimedAmount());
        
        // Verifier l'identié du client
        boolean identityValid = verifyIdentity(request.getClientId(), request.getNom());
        if (!identityValid) {
            log.warn("Identity verification failed for clientId: {}", request.getNom());
            return ClaimResponse.builder()
                .claimId("CLAIM-" + System.currentTimeMillis())
                .clientId(request.getClientId())
                .policyNumber(request.getPolicyNumber())
                .claimType(request.getClaimType())
                .amount(request.getClaimedAmount())
                .comment(request.getCommentaire())
                .status("REJETE")
                .reason("IDENTITE NON VERIFIEE")
                .createdAt(java.time.LocalDateTime.now().toString())
                .build();
        }
        log.info("Identity verified successfully");

        // 1. Vérifier la police via SOAP
        boolean policyValid = verifyPolicy(request.getPolicyNumber());
        if (!policyValid) {
            log.warn("Policy verification failed for policyNumber: {}", request.getPolicyNumber());
            return ClaimResponse.builder()
                .claimId("CLAIM-" + System.currentTimeMillis())
                .clientId(request.getClientId())
                .policyNumber(request.getPolicyNumber())
                .claimType(request.getClaimType())
                .amount(request.getClaimedAmount())
                .comment(request.getCommentaire())
                .status("REJETE")
                .reason("N° POLICE D'ASSURANCE NON VERIFIEE")
                .createdAt(java.time.LocalDateTime.now().toString())
                .build();
        }
        log.info("Policy verified successfully");

        // 2. Vérifier la fraude via gRPC
        RequeteFraude grpcRequest = RequeteFraude.newBuilder()
                .setIdSinistre("GEN-ID-" + System.currentTimeMillis())
                .setIdClient(request.getClientId()) 
                .setTypeSinistre(request.getClaimType())
                .setMontant(request.getClaimedAmount().doubleValue())
                .setDescription(request.getCommentaire())
                .build();
        ReponseFraude response = fraudStub.verifierFraude(grpcRequest);

        System.out.println("Niveau de risque : " + response.getNiveau());
        System.out.println("Raison : " + response.getRaison());

        if (response.getNiveauValue() >= 3) { // ELEVE
             // Logique de rejet
            log.warn("Fraud detected for claim. Niveau: {}, Raison: {}", response.getNiveau(), response.getRaison());
            return ClaimResponse.builder()
                .claimId("CLAIM-" + System.currentTimeMillis())
                .clientId(request.getClientId())
                .policyNumber(request.getPolicyNumber())
                .claimType(request.getClaimType())
                .amount(request.getClaimedAmount())
                .comment(request.getCommentaire())
                .status("REJETE")
                .reason("SUSPICION DE FRAUDE: " + response.getRaison())
                .createdAt(java.time.LocalDateTime.now().toString())
                .build();
        }
        
        return ClaimResponse.builder()
                .claimId("CLAIM-" + System.currentTimeMillis())
                .clientId(request.getClientId())
                .policyNumber(request.getPolicyNumber())
                .claimType(request.getClaimType())
                .amount(request.getClaimedAmount())
                .comment(request.getCommentaire())
                .status("APPROUVE")
                .createdAt(java.time.LocalDateTime.now().toString())
                .build();
    }
}