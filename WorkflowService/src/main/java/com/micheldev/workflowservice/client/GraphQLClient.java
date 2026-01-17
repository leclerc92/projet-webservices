package com.micheldev.workflowservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Client GraphQL dédié pour effectuer des requêtes GraphQL.
 * Utilise le protocole GraphQL standard avec query et variables.
 */
@Slf4j
public class GraphQLClient {

    private final String endpoint;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GraphQLClient(String endpoint) {
        this.endpoint = endpoint;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Exécute une requête GraphQL avec des variables.
     *
     * @param query     La requête GraphQL (ex: "query($id: String!) { user(id: $id) { name } }")
     * @param variables Les variables à passer à la requête
     * @return Les données de la réponse (contenu de "data")
     */
    public Map<String, Object> execute(String query, Map<String, Object> variables) {
        try {
            // Construire le body GraphQL standard
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            if (variables != null && !variables.isEmpty()) {
                requestBody.put("variables", variables);
            }

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.debug("GraphQL Request: {}", jsonBody);

            // Créer la requête HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Exécuter la requête
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.debug("GraphQL Response: {}", response.body());

            // Parser la réponse
            Map<String, Object> responseMap = objectMapper.readValue(
                    response.body(),
                    new TypeReference<Map<String, Object>>() {}
            );

            log.debug("Parsed GraphQL Response Map: {}", responseMap);
            // Vérifier les erreurs GraphQL
            if (responseMap.containsKey("errors")) {
                log.error("GraphQL Errors: {}", responseMap.get("errors"));
            }

            // Retourner les données
            if (responseMap.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                return data;
            }

            return new HashMap<>();

        } catch (Exception e) {
            log.error("GraphQL request failed: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    /**
     * Exécute une requête GraphQL sans variables.
     *
     * @param query La requête GraphQL
     * @return Les données de la réponse
     */
    public Map<String, Object> execute(String query) {
        return execute(query, null);
    }

    /**
     * Exécute une requête et extrait directement un champ spécifique.
     *
     * @param query     La requête GraphQL
     * @param variables Les variables
     * @param fieldName Le nom du champ à extraire de "data"
     * @return La valeur du champ demandé
     */
    @SuppressWarnings("unchecked")
    public <T> T executeAndExtract(String query, Map<String, Object> variables, String fieldName) {
        Map<String, Object> data = execute(query, variables);
        return (T) data.get(fieldName);
    }
}
