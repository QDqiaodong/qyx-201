FROM ${DOCKER_REGISTRY}maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY .mvn/settings.xml /root/.m2/settings.xml

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM ${DOCKER_REGISTRY}openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/edu-kit-system-1.0.0.jar app.jar

RUN mkdir -p /app/data/changelogs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
