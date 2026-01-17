const { ApolloServer, gql } = require('apollo-server');

// Données en dur
const reclamations = [
    { id: 'SIN-2026-001', clientId: 'CLI-123', typeSinistre: 'Auto', montant: 25000, statut: 'REJETE', dateCreation: '2026-01-10', commentaire: 'Collision avec un autre véhicule' },
    { id: 'SIN-2026-002', clientId: 'CLI-123', typeSinistre: 'Habitation', montant: 15000, statut: 'REJETE', dateCreation: '2026-01-08', commentaire: 'Dégâts des eaux suite à fuite' },
    { id: 'SIN-2026-003', clientId: 'CLI-456', typeSinistre: 'Santé', montant: 5000, statut: 'REJETE', dateCreation: '2026-01-05', commentaire: 'Acte non couvert par le contrat' },
    { id: 'SIN-2026-004', clientId: 'CLI-789', typeSinistre: 'Auto', montant: 45000, statut: 'APPROUVE', dateCreation: '2026-01-12', commentaire: 'Accident avec responsabilité partagée' },
    { id: 'SIN-2026-005', clientId: 'CLI-456', typeSinistre: 'Vie', montant: 100000, statut: 'APPROUVE', dateCreation: '2026-01-03', commentaire: 'Décès accidentel - indemnisation complète' },
    { id: 'SIN-2026-006', clientId: 'CLI-123', typeSinistre: 'Auto', montant: 8500, statut: 'REJETE', dateCreation: '2026-01-15', commentaire: 'Franchise non atteinte' },
    { id: 'SIN-2026-007', clientId: 'CLI-321', typeSinistre: 'Habitation', montant: 32000, statut: 'APPROUVE', dateCreation: '2026-01-14', commentaire: 'Incendie - expertise en cours' },
    { id: 'SIN-2026-008', clientId: 'CLI-789', typeSinistre: 'Santé', montant: 2500, statut: 'APPROUVE', dateCreation: '2026-01-11', commentaire: 'Frais hospitaliers remboursés' },
    { id: 'SIN-2026-009', clientId: 'CLI-321', typeSinistre: 'Voyage', montant: 3200, statut: 'APPROUVE', dateCreation: '2026-01-09', commentaire: 'Annulation de voyage pour raison médicale' },
    { id: 'SIN-2026-010', clientId: 'CLI-456', typeSinistre: 'Auto', montant: 18000, statut: 'REJETE', dateCreation: '2026-01-07', commentaire: 'Conduite en état d\'ivresse' }
];


// Schéma GraphQL
const typeDefs = gql`
    type Reclamation {
        id: ID!
        clientId: String
        typeSinistre: String
        montant: Float
        statut: String
        dateCreation: String
        commentaire: String
    }

    type Query {
        reclamations: [Reclamation]
        reclamationBeforeDate(dateCreation: String!): [Reclamation]
        reclamationOnDate(dateCreation: String!): Reclamation
        reclamationAfterDate(dateCreation: String!): [Reclamation]
        reclamationByClientId(clientId: String!): [Reclamation]

    }
`;

// Résolveur
const resolvers = {
    Query: {
        reclamations: () => reclamations,
        reclamationBeforeDate: (_, { dateCreation }) => reclamations.filter(r => r.dateCreation < dateCreation),
        reclamationAfterDate: (_, { dateCreation }) => reclamations.filter(r => r.dateCreation > dateCreation),
        reclamationOnDate: (_, { dateCreation }) => reclamations.find(r => r.dateCreation === dateCreation),
        reclamationByClientId: (_, { clientId }) => reclamations.filter(r => r.clientId === clientId)
    }
};

// Démarrage
const server = new ApolloServer({ typeDefs, resolvers });

server.listen(4000).then(({ url }) => {
    console.log(`🚀 Serveur GraphQL démarré sur ${url}`);
});
