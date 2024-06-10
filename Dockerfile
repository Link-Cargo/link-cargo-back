FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/linkcargo-0.0.1-SNAPSHOT.jar app.jar
RUN chmod +x app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]