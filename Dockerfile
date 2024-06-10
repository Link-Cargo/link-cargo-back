FROM openjdk:17-jdk
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod", "/app.jar"]