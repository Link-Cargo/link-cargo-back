FROM openjdk:17-jdk
COPY *.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","-Dspring.profiles.active=prod", "/app.jar"]