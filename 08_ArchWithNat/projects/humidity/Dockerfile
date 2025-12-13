FROM gradle:jdk21 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew clean build -x test

# Using Eclipse Temurin for guaranteed resolution of the runtime JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

EXPOSE 8020

ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]