-- Création de la table des réclamations
CREATE TABLE reclamations (
    id VARCHAR(50) PRIMARY KEY,
    client_id VARCHAR(50),
    type_sinistre VARCHAR(100),
    montant DECIMAL(10, 2),
    statut VARCHAR(50),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Données de test
INSERT INTO reclamations (id, client_id, type_sinistre, montant, statut) VALUES
('SIN-2026-002', 'CLI-123', 'Habitation', 15000, 'APPROUVE'),
('SIN-2026-003', 'CLI-456', 'Santé', 5000, 'REJETE');
