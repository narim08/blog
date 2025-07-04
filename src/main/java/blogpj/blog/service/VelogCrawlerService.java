package blogpj.blog.service;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

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
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");

        // ✅ headless 설정 추가
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        List<VelogPost> posts = new ArrayList<>();

        try {
            driver.get("https://velog.io/");
            Thread.sleep(3000); // ⏱ 로딩 대기 시간

            List<WebElement> elements = driver.findElements(By.cssSelector("a[href^='/@']"));

            for (int i = 0; i < Math.min(5, elements.size()); i++) {
                String title = elements.get(i).getText().trim();
                String link = elements.get(i).getAttribute("href");
                if (!title.isEmpty()) {
                    posts.add(new VelogPost(title, link));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // 창 닫기
        }

        return posts;
    }
}
