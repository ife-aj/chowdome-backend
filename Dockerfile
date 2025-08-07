FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/chowdome-0.0.1-SNAPSHOT.jar /app/chowdome.jar
EXPOSE 8000
ENTRYPOINT [ "java","-jar", "/app/chowdome.jar" ]

