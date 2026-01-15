package com.micheldev.flowableworkflowservice.controllers;

import org.flowable.engine.RuntimeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WorkflowController {

    // C'est le service principal de Flowable pour gérer les processus
    private final RuntimeService runtimeService;

    public WorkflowController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/start-claim")
    public String startProcess(@RequestBody Map<String, String> data) {
        
        // On prépare les variables initiales
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerId", data.get("customerId"));
        variables.put("fullName", data.get("fullName"));

        // On lance l'instance du processus par son ID (défini dans le XML)
        runtimeService.startProcessInstanceByKey("insuranceClaimProcess", variables);

        return "Processus démarré avec succès !";
    }
}