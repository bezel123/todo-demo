# base image
FROM maven:3.6-alpine AS build
WORKDIR /app/
# copy src
COPY . .
# download dependencies
RUN mvn dependency:go-offline -B
# build
RUN mvn compile && mvn package -Dmaven.test.skip=true

# final image
FROM openjdk:8-jre-alpine AS final
WORKDIR /app
# copy jar
COPY --from=build "/app/target/todo-demo-0.0.1-SNAPSHOT.jar" .
EXPOSE 8080
# run jar
ENTRYPOINT [ "java", "-jar", "/app/todo-demo-0.0.1-SNAPSHOT.jar"]




