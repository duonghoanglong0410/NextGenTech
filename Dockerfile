# Sử dụng base image cho Java 17
FROM eclipse-temurin:17-jdk-alpine

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file JAR vào container
COPY target/NextGenTech-0.0.1-SNAPSHOT.jar app.jar

# Expose cổng ứng dụng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "app.jar"]
