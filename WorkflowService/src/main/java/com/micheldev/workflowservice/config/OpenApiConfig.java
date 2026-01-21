package com.micheldev.workflowservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Insurance Workflow API")
                .version("1.0.0")
                .description("""
                    API d'orchestration pour le système de gestion des réclamations d'assurance.

                    Cette API coordonne plusieurs microservices :
                    - **REST** (Port 8080) : Vérification d'identité
                    - **SOAP** (Port 8081) : Vérification de police d'assurance
                    - **GraphQL** (Port 4000) : Gestion des réclamations (stockage MongoDB)
                    - **gRPC** (Port 50051) : Détection de fraude
                    """)
                .contact(new Contact()
                    .name("Projet Web Services")
                    .email("contact@example.com")))
            .servers(List.of(
                new Server().url("http://localhost:8082").description("Serveur local")
            ));
    }
}
