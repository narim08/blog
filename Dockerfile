# Java 기반 이미지 사용
FROM openjdk:17-jdk-bullseye

WORKDIR /app

# 프로젝트 복사
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY gradle gradle
COPY src src

# Gradle 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 컨테이너 실행
CMD ["java", "-jar", "build/libs/blog-0.0.1-SNAPSHOT.jar"]
