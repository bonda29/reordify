# build stage: Maven + JDK 21
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

# copy & build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# runtime stage: JRE 21
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# grab the jar
COPY --from=builder /app/target/reordify-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
