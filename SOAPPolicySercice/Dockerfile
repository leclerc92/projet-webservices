# Dockerfile pour SOAPPolicySercice
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copier les fichiers Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Donner les permissions d'exécution au wrapper Maven
RUN chmod +x ./mvnw

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Image de production
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Installer curl pour le healthcheck
RUN apk add --no-cache curl

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
