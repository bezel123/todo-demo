FROM maven:3.6-alpine AS build
WORKDIR /app/
COPY . /app
RUN mvn compile && mvn package

FROM openjdk:8-jre-alpine AS final
WORKDIR /app
COPY --from=build "/app/target/todo-demo-0.0.1-SNAPSHOT.jar" .
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/app/todo-demo-0.0.1-SNAPSHOT.jar"]




