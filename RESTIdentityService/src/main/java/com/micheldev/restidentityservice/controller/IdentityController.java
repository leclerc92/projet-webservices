package com.micheldev.restidentityservice.controller;

import com.micheldev.restidentityservice.model.IdentityRequest;
import com.micheldev.restidentityservice.model.IdentityResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity")
public class IdentityController {

    @PostMapping("/verify") 
    public IdentityResponse verifyIdentity(@RequestBody IdentityRequest request) {
        System.out.println("Demande reçue pour : " + request.getFullName());

        if ("INVALID".equals(request.getCustomerId())) {
            return new IdentityResponse(false, "Client inconnu ou blacklisté.");
        }

        if (request.getFullName() == null || request.getFullName().isEmpty()) {
            return new IdentityResponse(false, "Le nom est obligatoire.");
        }
        return new IdentityResponse(true, "Identité vérifiée avec succès.");
    }
}