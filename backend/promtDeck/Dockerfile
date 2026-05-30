# ── Build stage ─────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Gradle wrapper + 빌드 스크립트 먼저 복사
COPY gradlew gradlew.bat ./
COPY gradle gradle/
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# 의존성만 먼저 다운로드 (소스가 바뀌어도 이 레이어는 캐시 재사용)
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드 (-x test 로 테스트 제외)
COPY src src/
RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ────────────────────────────────────────────────
# JRE 이미지가 없는 Java 버전도 있으므로 JDK 사용
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-XX:+UseSerialGC", "-jar", "app.jar"]
