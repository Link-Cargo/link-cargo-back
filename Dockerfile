FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/linkcargo-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.yml /app/application.yml
RUN chmod +x app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=/app/application.yml"]
