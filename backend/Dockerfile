FROM ubuntu:latest
LABEL authors="guscjf1211"

ENTRYPOINT ["top", "-b"]

# 1단계: 빌드 전용 환경 (JDK 21 환경에서 Gradle 빌드 수행)
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
# 도커 내에서 실행 권한 부여 후 빌드
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행 전용 환경 (최종 실행 파일만 추출)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# 1단계 빌드 결과물 중 jar 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]