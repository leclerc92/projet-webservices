# Dockerfile pour gRPCFraudDetectionService
FROM python:3.11-slim

WORKDIR /app

# Installer les dépendances système
RUN apt-get update && apt-get install -y --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

# Copier les fichiers de dépendances
COPY requirements.txt .

# Installer les dépendances Python
RUN pip install --no-cache-dir -r requirements.txt

# Copier uniquement le fichier .proto et le serveur
COPY fraud_detection.proto .
COPY serveur.py .

# Régénérer les fichiers pb2 avec la version de protobuf installée
RUN python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. fraud_detection.proto

EXPOSE 50051

CMD ["python", "serveur.py"]
