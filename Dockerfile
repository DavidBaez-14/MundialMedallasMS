# Etapa 1: Build con Maven y Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos de Maven para aprovechar cache de dependencias
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias primero para acelerar builds posteriores
RUN mvn dependency:go-offline -B

# Copiar codigo fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime liviano
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar artefacto generado
COPY --from=build /app/target/*.jar app.jar

# Puerto por convención (DigitalOcean inyecta PORT)
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
