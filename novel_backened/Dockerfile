FROM maven:3.8.8-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests package

FROM openjdk:8
WORKDIR /app
COPY --from=build /app/target/novel_backened-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
