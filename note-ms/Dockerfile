FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY auth-ms/pom.xml auth-ms/
COPY gateway/pom.xml gateway/
COPY note-ms/pom.xml note-ms/
COPY risk-ms/pom.xml risk-ms/
COPY patient-ms/pom.xml patient-ms/
RUN mvn -pl note-ms -B dependency:go-offline
COPY note-ms/src note-ms/src
RUN mvn -pl note-ms -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/note-ms/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]