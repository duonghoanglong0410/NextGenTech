# Stage 1: Build
FROM maven:3-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY ../../../IT_HCMUNRE/SEO_Java%20Web/DoAn/v2/TheGioiCongNghe/TheGioiCongNghe/pom.xml .
COPY ../../../IT_HCMUNRE/SEO_Java%20Web/DoAn/v2/TheGioiCongNghe/TheGioiCongNghe/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "demo.jar"]
