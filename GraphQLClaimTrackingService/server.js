const { ApolloServer, gql } = require('apollo-server');

// Données en dur
const reclamations = [
    { id: 'SIN-2026-001', clientId: 'CLI-123', typeSinistre: 'Auto', montant: 25000, statut: 'EN_COURS', dateCreation: '2026-01-10' },
    { id: 'SIN-2026-002', clientId: 'CLI-123', typeSinistre: 'Habitation', montant: 15000, statut: 'APPROUVE', dateCreation: '2026-01-08' },
    { id: 'SIN-2026-003', clientId: 'CLI-456', typeSinistre: 'Santé', montant: 5000, statut: 'REJETE', dateCreation: '2026-01-05' }
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
    }

    type Query {
        reclamations: [Reclamation]
    }
`;

// Résolveur
const resolvers = {
    Query: {
        reclamations: () => reclamations
    }
};

// Démarrage
const server = new ApolloServer({ typeDefs, resolvers });

server.listen(4000).then(({ url }) => {
    console.log(`🚀 Serveur GraphQL démarré sur ${url}`);
});
