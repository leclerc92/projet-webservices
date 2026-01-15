const { ApolloServer, gql } = require('apollo-server');
const { Pool } = require('pg');

// Connexion PostgreSQL
const pool = new Pool({
    host: process.env.DB_HOST || 'localhost',
    port: 5432,
    database: 'claims_db',
    user: 'claims_user',
    password: 'claims_password'
});

// Schéma GraphQL simplifié
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

// Résolveur unique
const resolvers = {
    Query: {
        reclamations: async () => {
            const result = await pool.query('SELECT * FROM reclamations ORDER BY date_creation DESC');
            return result.rows.map(row => ({
                id: row.id,
                clientId: row.client_id,
                typeSinistre: row.type_sinistre,
                montant: parseFloat(row.montant),
                statut: row.statut,
                dateCreation: row.date_creation
            }));
        }
    }
};

// Démarrage
const server = new ApolloServer({ typeDefs, resolvers });

server.listen(4000).then(({ url }) => {
    console.log(`🚀 Serveur GraphQL démarré sur ${url}`);
});
