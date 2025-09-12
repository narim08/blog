package blogpj.blog.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
        WebDriver driver = null;
        List<VelogPost> posts = new ArrayList<>();

        try {
            driver = createWebDriver();

            log.info("Velog 크롤링 시작");
            driver.get("https://velog.io/trending");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[class*='PostCard_styleLink']")));

            List<WebElement> postElements = driver.findElements(By.cssSelector("li.PostCard_block__FTMsy"));
            int count = Math.min(5, postElements.size());

            log.info("발견된 포스트 수: {}, 크롤링할 포스트 수: {}", postElements.size(), count);

            for (int i = 0; i < count; i++) {
                try {
                    WebElement item = postElements.get(i);
                    WebElement linkEl = item.findElement(By.cssSelector("a[class*='PostCard_styleLink']"));
                    String link = linkEl.getAttribute("href");

                    WebElement imgEl = linkEl.findElement(By.tagName("img"));
                    String title = imgEl.getAttribute("alt").trim().replace(" post", "");

                    posts.add(new VelogPost(title, link));
                    log.info("크롤링 완료 {}/{}: {}", i + 1, count, title);

                } catch (Exception e) {
                    log.warn("포스트 {} 크롤링 중 오류: {}", i, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Velog 크롤링 중 오류 발생", e);
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                    log.info("WebDriver 종료 완료");
                } catch (Exception e) {
                    log.warn("WebDriver 종료 중 오류", e);
                }
            }
        }

        return posts;
    }

    private WebDriver createWebDriver() {
        // Docker 환경에서 Chrome/ChromeDriver 경로 설정
        String chromeBin = System.getenv("CHROME_BIN");
        String chromeDriver = System.getenv("CHROME_DRIVER");

        if (chromeDriver != null) {
            System.setProperty("webdriver.chrome.driver", chromeDriver);
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        }

        ChromeOptions options = new ChromeOptions();

        // Docker 환경에서 필수 옵션들
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");

        // 이미지와 JavaScript는 활성화 (Velog는 React 기반이므로 필요)
        // options.addArguments("--disable-images"); // 제거
        // options.addArguments("--disable-javascript"); // 제거

        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        // Chrome 바이너리 경로 설정
        if (chromeBin != null) {
            options.setBinary(chromeBin);
        } else {
            options.setBinary("/usr/bin/google-chrome");
        }

        // CDP 관련 로깅 비활성화
        options.addArguments("--disable-logging");
        options.addArguments("--log-level=3");
        options.addArguments("--silent");

        // 성능 최적화
        options.addArguments("--memory-pressure-off");
        options.addArguments("--max_old_space_size=4096");

        log.info("ChromeOptions 설정 완료");
        log.info("Chrome Binary: {}", chromeBin != null ? chromeBin : "/usr/bin/google-chrome");
        log.info("ChromeDriver: {}", chromeDriver != null ? chromeDriver : "/usr/local/bin/chromedriver");

        return new ChromeDriver(options);
    }
}