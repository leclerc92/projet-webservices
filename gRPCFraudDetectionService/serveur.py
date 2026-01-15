import grpc
from concurrent import futures
import fraud_detection_pb2 as pb2
import fraud_detection_pb2_grpc as pb2_grpc


class ServiceDetectionFraude(pb2_grpc.ServiceDetectionFraudeServicer):
    """Implémentation du service de détection de fraude"""

    def VerifierFraude(self, request, context):
        """Analyse le sinistre et retourne le niveau de risque"""
        
        print(f"Analyse du sinistre: {request.id_sinistre}")
        print(f"  Client: {request.id_client}")
        print(f"  Type: {request.type_sinistre}")
        print(f"  Montant: {request.montant}€")
        print(f"  Description: {request.description}")
        
        # Logique de détection de fraude
        niveau, raison = self._analyser_risque(request)
        
        # Construire la réponse
        reponse = pb2.ReponseFraude(
            id_sinistre=request.id_sinistre,
            niveau=niveau,
            raison=raison
        )
        
        print(f"  Résultat: {pb2.NiveauRisque.Name(niveau)} - {raison}")
        return reponse

    def _analyser_risque(self, request):
        """Analyse le risque de fraude basé sur plusieurs critères"""
        
        montant = request.montant
        description = request.description.lower()
        
        # Mots-clés suspects
        mots_suspects = ["vol", "incendie", "cambriolage", "disparu"]
        contient_mot_suspect = any(mot in description for mot in mots_suspects)
        
        # Règles de détection
        if montant > 50000:
            return pb2.ELEVE, "Montant très élevé (> 50 000€)"
        
        if montant > 20000 and contient_mot_suspect:
            return pb2.ELEVE, "Montant élevé avec mots-clés suspects"
        
        if montant > 10000:
            return pb2.MOYEN, "Montant significatif, vérification recommandée"
        
        if contient_mot_suspect:
            return pb2.MOYEN, "Mots-clés suspects détectés"
        
        return pb2.FAIBLE, "Aucun indicateur de fraude détecté"


def demarrer_serveur():
    """Démarre le serveur gRPC"""
    
    serveur = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    pb2_grpc.add_ServiceDetectionFraudeServicer_to_server(
        ServiceDetectionFraude(), serveur
    )
    
    port = "50051"
    serveur.add_insecure_port(f"[::]:{port}")
    serveur.start()
    
    print(f"Serveur de détection de fraude démarré sur le port {port}")
    print("En attente de requêtes...")
    
    serveur.wait_for_termination()


if __name__ == "__main__":
    demarrer_serveur()
