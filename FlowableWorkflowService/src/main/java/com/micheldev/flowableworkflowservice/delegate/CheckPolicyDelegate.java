package com.micheldev.flowableworkflowservice.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Component("checkPolicyDelegate")
public class CheckPolicyDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("--- [FLOWABLE] Début Vérification Police (SOAP) ---");

        // 1. Récupérer l'ID de police (depuis les variables ou en dur pour le test)
        String policyId = (String) execution.getVariable("policyId");
        if (policyId == null) policyId = "A12345"; // Valeur par défaut valide

        // 2. Construire l'enveloppe XML à la main (C'est ça le SOAP !)
        String soapRequest = 
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "                  xmlns:soap=\"http://www.example.com/soap-api\">" +
            "   <soapenv:Header/>" +
            "   <soapenv:Body>" +
            "      <soap:getPolicyRequest>" +
            "         <soap:policyNumber>" + policyId + "</soap:policyNumber>" +
            "         <soap:claimType>ACCIDENT</soap:claimType>" +
            "      </soap:getPolicyRequest>" +
            "   </soapenv:Body>" +
            "</soapenv:Envelope>";

        // 3. Envoyer la requête au service SOAP (Port 8081)
        String url = "http://localhost:8081/ws";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            String responseXml = restTemplate.postForObject(url, request, String.class);
            System.out.println("Réponse SOAP brute : " + responseXml);

            // 4. Analyser la réponse (Parsing simple)
            // On cherche la balise <ns2:valid>true</ns2:valid> ou similaire
            boolean isValid = responseXml.contains("true"); // Simplification pour le TP
            
            System.out.println("Police valide ? " + isValid);
            execution.setVariable("isPolicyValid", isValid);

        } catch (Exception e) {
            System.err.println("Erreur SOAP : " + e.getMessage());
            execution.setVariable("isPolicyValid", false);
        }
    }
}