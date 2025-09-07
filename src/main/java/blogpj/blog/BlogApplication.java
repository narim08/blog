package blogpj.blog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {

	public static void main(String[] args) {
		//Dotenv dotenv = Dotenv.load();
		// .env 파일의 변수를 시스템 속성으로 설정
		//System.setProperty("GEMINI_API_KEY", dotenv.get("GEMINI_API_KEY"));
		SpringApplication.run(BlogApplication.class, args);
	}

}
