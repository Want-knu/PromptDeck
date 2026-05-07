# ── Build stage ─────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# 의존성 레이어 캐시: Gradle wrapper + build 스크립트만 먼저 복사
COPY gradlew gradlew.bat ./
COPY gradle gradle/
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# 소스 복사 후 빌드 (-x test 로 테스트 제외)
COPY src src/
RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ────────────────────────────────────────────────
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
