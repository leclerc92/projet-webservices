// init-mongo.js
db = db.getSiblingDB('claims'); // Nom de la base défini dans server.js

db.reclamations.drop(); // Nettoyer avant d'insérer (optionnel)

db.reclamations.insertMany([
    {
        "clientId": "CLI-123",
        "typeSinistre": "Vol",
        "montant": 150000.0,
        "statut": "REJETE",
        "raison": "Vol de téléphone",
        "dateCreation": "2023-12-01",
        "commentaire": "Samsung S21"
    },
    {
        "clientId": "CLI-123",
        "typeSinistre": "Dégâts des eaux",
        "montant": 500.0,
        "statut": "APPROUVE",
        "raison": "Fuite machine à laver",
        "dateCreation": "2024-01-15",
        "commentaire": "Cuisine inondée"
    },
    {
        "clientId": "CLI-456",
        "typeSinistre": "Accident",
        "montant": 25000.0,
        "statut": "REJETE",
        "raison": "Non couvert",
        "dateCreation": "2024-02-20",
        "commentaire": "Accident responsable"
    }
]);