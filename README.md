# projet-webservices


## Installation et Démarrage

1. **Cloner le dépôt :**
```bash
git clone https://github.com/leclerc92/projet-webservices.git

```

2. **Accéder au répertoire du projet :**
```bash
cd projet-webservices

```

3. **Construire et lancer les services :**

```bash
docker compose up -d --build

```

## Accès aux Services

Une fois les conteneurs lancés, voici les points d'entrée pour accéder à l'interface utilisateur et aux différentes API depuis votre navigateur ou un client API (comme Postman ou Insomnia).

| Service | Type | Port Local | URL d'accès / Endpoint | Description                                                                                                       |
| --- | --- | --- | --- |-------------------------------------------------------------------------------------------------------------------|
| **Frontend** | Web UI | `3000` | [http://localhost:3000](http://localhost:3000) | Interface utilisateur principale pour tester l'application.                                                       |
| **Workflow Service** | REST (Orchestrator) | `8082` | [http://localhost:8082](http://localhost:8082) | Point d'entrée principal de l'API qui coordonne les autres services.                                              |
| **Identity Service** | REST | `8080` | [http://localhost:8080/api/identity](http://localhost:8080/api/identity) | Gestion et vérification de l'identité des utilisateurs.                                                           |
| **Policy Service** | SOAP | `8081` | [http://localhost:8081/ws](http://localhost:8081/ws) | Vérification de la police d'assurance.                                                                            |
| **Claim Service** | GraphQL | `4000` | [http://localhost:4000](http://localhost:4000) | Suivi des réclamations (stockage dans MongoDB).                                                                   |
| **Fraud Service** | gRPC | `50051` | `localhost:50051` | Détection de fraude.                                                                                              |
| **MongoDB** | Base de données | `27017` | `mongodb://localhost:27017` | Base de données NoSQL pour le stockage des réclamations (utilisée par le service GraphQL).                        |

## Arrêter l'application

Pour arrêter et supprimer les conteneurs :

```bash
docker compose down

```

---
