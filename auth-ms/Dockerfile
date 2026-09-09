FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY auth-ms/pom.xml auth-ms/
COPY gateway/pom.xml gateway/
COPY note-ms/pom.xml note-ms/
COPY risk-ms/pom.xml risk-ms/
COPY patient-ms/pom.xml patient-ms/
RUN mvn -pl auth-ms -B dependency:go-offline
COPY auth-ms/src auth-ms/src
RUN mvn -pl auth-ms -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/auth-ms/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]