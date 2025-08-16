# ---- Stage 1: Build the application ----
FROM maven:3.9.9-eclipse-temurin-21 AS build

ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

WORKDIR /app

# We will template settings.xml and copy it in
COPY ./settings-docker.xml /root/.m2/settings.xml

# Replace placeholders with ARG values (not printed in logs)
RUN sed -i "s|GITHUB_ACTOR|${GITHUB_ACTOR}|g" /root/.m2/settings.xml && \
    sed -i "s|GITHUB_TOKEN|${GITHUB_TOKEN}|g" /root/.m2/settings.xml

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
