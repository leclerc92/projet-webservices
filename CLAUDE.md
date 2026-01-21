# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Insurance claim processing system demonstrating web service interoperability. The **WorkflowService** orchestrates four specialized microservices using different protocols (REST, SOAP, GraphQL, gRPC) to process insurance claims and verify client information.

## Architecture

```
                    ┌─────────────────────┐
                    │   WorkflowService   │
                    │   (Orchestrator)    │
                    │   Port: 8082        │
                    └─────────┬───────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐   ┌─────────────────┐   ┌─────────────────┐
│ REST Identity │   │  SOAP Policy    │   │ GraphQL Claims  │
│ Port: 8080    │   │  Port: 8081     │   │ Port: 4000      │
│ Java/Spring   │   │  Java/Spring WS │   │ Node/Apollo     │
└───────────────┘   └─────────────────┘   └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │ gRPC Fraud Det. │
                    │ Port: 50051     │
                    │ Python          │
                    └─────────────────┘
```

## Service Details

| Service | Technology | Port | Purpose |
|---------|------------|------|---------|
| WorkflowService | Spring Boot 4, Java 17, gRPC client | 8082 | Main orchestrator - calls all other services |
| RESTIdentityService | Spring Boot 4, Java 17 | 8080 | Identity verification via REST |
| SOAPPolicySercice | Spring Boot 4, Spring WS, Java 17 | 8081 | Policy validation via SOAP |
| GraphQLClaimTrackingService | Node.js, Apollo Server | 4000 | Claim tracking via GraphQL |
| gRPCFraudDetectionService | Python 3.11, grpcio | 50051 | Fraud detection via gRPC |

## Build & Run Commands

### Run All Services (Docker)
```bash
docker compose up --build
```

### Individual Service Development

**Java Services (REST, SOAP, Workflow):**
```bash
cd <ServiceName>
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw clean package -DskipTests
```

**GraphQL Service (Node.js):**
```bash
cd GraphQLClaimTrackingService
npm install
npm start
```

**gRPC Service (Python):**
```bash
cd gRPCFraudDetectionService
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Regenerate proto files if fraud_detection.proto changes
python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. fraud_detection.proto

python serveur.py
```

## Key Workflow Endpoints

- `GET /api/workflow/client?name=X&clientId=Y&policyNumber=Z` - Get client info (calls REST → SOAP → GraphQL)
- `POST /api/workflow/claim` - Submit claim (calls REST → SOAP → gRPC for fraud detection)

## Proto File Locations

The gRPC proto definition exists in two places that must stay synchronized:
- `gRPCFraudDetectionService/fraud_detection.proto` (Python server)
- `WorkflowService/src/main/proto/fraud_detection.proto` (Java client, includes java_package options)

## Business Logic Notes

- **Identity**: Customer ID "INVALID" is rejected; empty names are rejected
- **Policy**: Policy numbers starting with "A" are valid; others are rejected
- **Fraud**: Claims >50k€ auto-flagged high risk; >20k€ with keywords (vol, incendie, cambriolage, disparu) flagged high risk
