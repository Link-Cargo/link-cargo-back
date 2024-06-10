FROM openjdk:17-jdk
WORKDIR /usr/src/app
COPY *.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]