# Sử dụng OpenJDK 17 làm base image
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR từ dự án vào container
COPY target/NextGenTech-0.0.1-SNAPSHOT.jar app.jar

# Sao chép toàn bộ thư mục classes (chứa resources và compiled classes) nếu cần
COPY target/classes /app/classes

# Sao chép chứng chỉ nếu dùng SSL
COPY src/main/resources/ca.pem /app/ca.pem

# Sao chép templates và static nếu không được nhúng vào JAR
COPY src/main/resources/templates /app/templates
COPY src/main/resources/static /app/static

# Thiết lập biến môi trường (giúp xác định classpath và resources)
ENV CLASSPATH=/app/classes:/app/templates:/app/static

# Mở cổng 8080 (dùng bởi Spring Boot)
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-Dspring.config.location=classpath:/application.properties", "-jar", "app.jar"]
