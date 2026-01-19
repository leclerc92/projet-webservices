const { ApolloServer, gql } = require('apollo-server');
const mongoose = require('mongoose');
const Reclamation = require('./models/Reclamation');

// Connexion à MongoDB
mongoose.connect('mongodb://mongo:27017/claims');


// Schéma GraphQL
const typeDefs = gql`
    type Reclamation {
        id: ID!
        clientId: String
        typeSinistre: String
        montant: Float
        statut: String
        raison: String
        dateCreation: String
        commentaire: String
    }

    input ReclamationInput {
        id: ID!
        clientId: String!
        typeSinistre: String!
        montant: Float!
        statut: String!
        raison: String!
        dateCreation: String!
        commentaire: String
    }

    type Query {
        reclamations: [Reclamation]
        reclamationBeforeDate(dateCreation: String!): [Reclamation]
        reclamationOnDate(dateCreation: String!): Reclamation
        reclamationAfterDate(dateCreation: String!): [Reclamation]
        reclamationByClientId(clientId: String!): [Reclamation]
    }

    type Mutation {
        addReclamation(input: ReclamationInput!): Reclamation
    }
`;

// Résolveur
const resolvers = {
    Query: {
        reclamations: async () => await Reclamation.find(),
        reclamationBeforeDate: async (_, { dateCreation }) => await Reclamation.find({ dateCreation: { $lt: dateCreation } }),
        reclamationAfterDate: async (_, { dateCreation }) => await Reclamation.find({ dateCreation: { $gt: dateCreation } }),
        reclamationOnDate: async (_, { dateCreation }) => await Reclamation.findOne({ dateCreation }),
        reclamationByClientId: async (_, { clientId }) => await Reclamation.find({ clientId })
    },
    Mutation: {
        addReclamation: async (_, { input }) => {
            const reclamation = new Reclamation(input);
            await reclamation.save();
            return reclamation;
        }
    }
};

// Démarrage
const server = new ApolloServer({ typeDefs, resolvers });

server.listen(4000).then(({ url }) => {
    console.log(`🚀 Serveur GraphQL démarré sur ${url}`);
});
