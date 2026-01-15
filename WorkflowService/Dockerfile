# Dockerfile pour WorkflowService
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copier les fichiers Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Donner les permissions d'exécution au wrapper Maven
RUN chmod +x ./mvnw

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline -B

# Copier le code source (inclut les fichiers .proto)
COPY src src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Image de production
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
