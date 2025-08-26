FROM eclipse-temurin:21-jre
WORKDIR /app

COPY target/meta-financeira-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
