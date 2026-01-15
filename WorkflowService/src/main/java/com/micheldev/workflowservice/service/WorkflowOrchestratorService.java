package com.micheldev.workflowservice.service;

import com.micheldev.workflowservice.dto.ClaimRequest;
import com.micheldev.workflowservice.dto.ClaimResponse;
import com.micheldev.workflowservice.dto.ClientInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowOrchestratorService {



    /**
     * Orchestrates calls to various services to retrieve client information.
     */
    public ClientInfoResponse getClientInfo(String name, String clientId, String policyNumber) {
        log.info("Fetching client info for clientId: {}, name: {}, policyNumber: {}", clientId, name, policyNumber);

        return null;
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