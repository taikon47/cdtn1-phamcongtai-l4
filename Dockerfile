# =========================================================================
# DOCKERFILE CHO BACKEND SPRING BOOT (TRACK SE - LUỒNG L4)
# Multi-stage build tối ưu kích thước image và bảo mật (non-root)
# =========================================================================

# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# Copy Maven wrapper và pom.xml trước để tận dụng cache
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn ./.mvn
RUN chmod +x ./mvnw

# Tải dependencies trước
RUN ./mvnw dependency:go-offline -B || true

# Copy toàn bộ mã nguồn và build package
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cài đặt curl phục vụ healthcheck của Docker
RUN apk add --no-cache curl

# Tạo user không đặc quyền (non-root user) để tuân thủ OWASP
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy file jar từ builder stage
COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8081

HEALTHCHECK --interval=5s --timeout=3s --retries=10 --start-period=15s \
  CMD curl -f http://localhost:8081/health || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
