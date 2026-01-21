const mongoose = require('mongoose');

const reclamationSchema = new mongoose.Schema({
    clientId: String,
    typeSinistre: String,
    montant: Number,
    statut: String,
    raison: String,
    dateCreation: String,
    commentaire: String
});

module.exports = mongoose.model('Reclamation', reclamationSchema);