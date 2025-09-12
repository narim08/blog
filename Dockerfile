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

# 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    findutils curl bash \
    chromium \
    chromium-driver \
    fonts-liberation \
    libnss3 \
    libxss1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libgbm-dev \
    && rm -rf /var/lib/apt/lists/*

# Gradle 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 환경 변수 설정 (Chromium 위치)
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_DRIVER=/usr/bin/chromedriver

# 5. 빌드된 jar 파일 복사
COPY build/libs/*.jar app.jar

# 컨테이너 실행
CMD ["java", "-jar", "build/libs/blog-0.0.1-SNAPSHOT.jar"]
