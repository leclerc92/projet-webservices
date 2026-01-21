package com.micheldev.workflowservice.controller;

import com.micheldev.workflowservice.dto.ClaimRequest;
import com.micheldev.workflowservice.dto.ClaimResponse;
import com.micheldev.workflowservice.dto.ClientInfoRequest;
import com.micheldev.workflowservice.dto.ClientInfoResponse;
import com.micheldev.workflowservice.service.WorkflowOrchestratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Workflow", description = "API d'orchestration des services d'assurance")
public class WorkflowController {

    private final WorkflowOrchestratorService orchestratorService;

    @Operation(
        summary = "Récupérer les informations client",
        description = "Orchestre les appels vers REST (identité), SOAP (police) et GraphQL (historique réclamations)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informations client récupérées avec succès",
            content = @Content(schema = @Schema(implementation = ClientInfoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Identité ou police invalide", content = @Content)
    })
    @GetMapping("/client")
    public ResponseEntity<ClientInfoResponse> getClientInfo(
            @Parameter(description = "Nom du client", example = "John") @RequestParam String name,
            @Parameter(description = "Identifiant client", example = "CLI-123") @RequestParam String clientId,
            @Parameter(description = "Numéro de police (doit commencer par 'A')", example = "A12345") @RequestParam String policyNumber) {

        ClientInfoRequest clientInfoRequest = new ClientInfoRequest(name, clientId, policyNumber);
        ClientInfoResponse response = orchestratorService.getClientInfo(clientInfoRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Soumettre une réclamation",
        description = "Orchestre les appels vers REST (identité), SOAP (police), gRPC (détection fraude) et GraphQL (stockage MongoDB)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Réclamation créée avec succès",
            content = @Content(schema = @Schema(implementation = ClaimResponse.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping("/claim")
    public ResponseEntity<ClaimResponse> submitClaim(@RequestBody ClaimRequest claimRequest) {
        ClaimResponse response = orchestratorService.submitClaim(claimRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}