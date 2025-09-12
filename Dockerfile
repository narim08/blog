# 1. Java 기반 이미지 사용
FROM eclipse-temurin:17-jdk-alpine

# 2. 필수 패키지 설치
RUN apk add --no-cache \
    bash \
    chromium \
    chromium-chromedriver \
    nss \
    freetype \
    harfbuzz \
    ttf-freefont \
    fontconfig \
    wget \
    unzip \
    curl \
    git

# 3. 환경 변수 설정 (Chromium 위치)
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_DRIVER=/usr/bin/chromedriver

# 4. 작업 디렉토리 설정
WORKDIR /app

# 5. 빌드된 jar 파일 복사
COPY build/libs/*.jar app.jar

# 6. 컨테이너 실행 시 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
