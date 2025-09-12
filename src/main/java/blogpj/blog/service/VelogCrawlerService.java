package blogpj.blog.service;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class VelogCrawlerService {

    @Getter
    public static class VelogPost {
        private final String title;
        private final String link;

        public VelogPost(String title, String link) {
            this.title = title;
            this.link = link;
        }
    }

    public List<VelogPost> getTopPosts() {

        /* WebDriverManager가 자동으로 크롬드라이버 설치/경로 설정
        WebDriverManager.chromedriver().setup();*/

        // Dockerfile에서 설치한 Chromedriver 경로 직접 지정
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");             // 헤드리스 모드
        options.addArguments("--no-sandbox");           // 샌드박스 비활성화
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용 문제 방지
        options.setBinary("/usr/bin/chromium");         // Chromium 경로 지정

        WebDriver driver = new ChromeDriver(options);
        List<VelogPost> posts = new ArrayList<>();

        try {
            driver.get("https://velog.io/trending");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[class*='PostCard_styleLink']")));

            int count = Math.min(5, driver.findElements(By.cssSelector("li.PostCard_block__FTMsy")).size());

            for (int i = 0; i < count; i++) {
                WebElement item = driver.findElements(By.cssSelector("li.PostCard_block__FTMsy")).get(i);

                WebElement linkEl = item.findElement(By.cssSelector("a[class*='PostCard_styleLink']"));
                String link = linkEl.getAttribute("href");

                WebElement imgEl = linkEl.findElement(By.tagName("img"));
                String title = imgEl.getAttribute("alt").trim().replace(" post", "");

                posts.add(new VelogPost(title, link));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return posts;
    }
}