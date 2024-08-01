FROM openjdk:17-jdk
RUN mkdir -p /app/logs
WORKDIR /app
COPY build/libs/linkcargo-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.yml /app/application.yml
RUN touch /app/logs/application.log && chmod 666 /app/logs/application.log
ENV LOGGING_FILE_NAME=/app/logs/application.log
RUN chmod +x app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=/app/application.yml"]
