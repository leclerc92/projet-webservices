package com.micheldev.flowableworkflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component("verifyIdentityDelegate")
public class VerifyIdentityDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("--- [FLOWABLE] Début Vérification Identité ---");

        // 1. Récupérer les données entrées au lancement du processus
        String customerId = (String) execution.getVariable("customerId");
        String fullName = (String) execution.getVariable("fullName");

        // Valeurs par défaut si null
        if (customerId == null) customerId = "12345";
        if (fullName == null) fullName = "Test User";

        // 2. Préparer l'appel au Service REST (qui tourne sur le port 8080)
        String url = "http://localhost:8080/api/identity/verify";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("customerId", customerId);
        requestBody.put("fullName", fullName);
        requestBody.put("birthDate", "1990-01-01");

        RestTemplate restTemplate = new RestTemplate();

        try {
            // 3. Appel réel
            System.out.println("Appel du service REST Identity...");
            Map response = restTemplate.postForObject(url, requestBody, Map.class);

            boolean isValid = (Boolean) response.get("valid");
            System.out.println("Réponse reçue : " + isValid);

            // 4. Sauvegarder le résultat dans le processus Flowable
            // Cette variable "isIdentityValid" servira pour la Gateway (le losange)
            execution.setVariable("isIdentityValid", isValid);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel REST : " + e.getMessage());
            execution.setVariable("isIdentityValid", false);
        }
    }
}