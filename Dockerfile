FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package

CMD ["java", "-jar", "target/NextGenTech-0.0.1-SNAPSHOT.jar"]
