# --- Étape 1 : Build ---
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copier le pom.xml en premier pour tirer parti du cache des dépendances Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier les sources et compiler
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Étape 2 : Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
