package com.micheldev.workflowservice.controller;

import com.micheldev.workflowservice.dto.ClaimRequest;
import com.micheldev.workflowservice.dto.ClaimResponse;
import com.micheldev.workflowservice.dto.ClientInfoRequest;
import com.micheldev.workflowservice.dto.ClientInfoResponse;
import com.micheldev.workflowservice.service.WorkflowOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowOrchestratorService orchestratorService;

    /**
     * GET endpoint to retrieve client data by calling various external services.
     * Called by the frontend with client name, id, and policy number.
     */
    @GetMapping("/client")
    public ResponseEntity<ClientInfoResponse> getClientInfo(
            @RequestParam String name,
            @RequestParam String clientId,
            @RequestParam String policyNumber) {


        ClientInfoRequest clientInfoRequest = new ClientInfoRequest(name, clientId, policyNumber);
        ClientInfoResponse response = orchestratorService.getClientInfo(clientInfoRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * POST endpoint to submit a claim.
     * Registers information in the database via various external services.
     */
    @PostMapping("/claim")
    public ResponseEntity<ClaimResponse> submitClaim(@RequestBody ClaimRequest claimRequest) {
        ClaimResponse response = orchestratorService.submitClaim(claimRequest);
        return ResponseEntity.ok(response);
    }
}