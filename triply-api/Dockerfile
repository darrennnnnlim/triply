FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/triply-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]