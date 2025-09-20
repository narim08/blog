package blogpj.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 윈도우 파일 경로에 맞게 매핑
        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations("file:///C:/profile-images/");
    }
}
