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

# 기본 패키지 설치
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    curl \
    fonts-liberation \
    libnss3 \
    libxss1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libgbm-dev \
    && rm -rf /var/lib/apt/lists/*

# Chrome 114 버전 직접 설치 (Selenium 4.25.0과 호환되는 안정적인 버전)
RUN wget -q https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.90-1_amd64.deb \
    && dpkg -i google-chrome-stable_114.0.5735.90-1_amd64.deb || apt-get install -f -y \
    && rm google-chrome-stable_114.0.5735.90-1_amd64.deb

# ChromeDriver 114 버전 설치
RUN wget -q https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip -d /usr/local/bin/ \
    && rm chromedriver_linux64.zip \
    && chmod +x /usr/local/bin/chromedriver

# Gradle 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 환경 변수 설정
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROME_DRIVER=/usr/local/bin/chromedriver

# 컨테이너 실행
CMD ["java", "-jar", "build/libs/blog-0.0.1-SNAPSHOT.jar"]
