package blogpj.blog.controller;

import blogpj.blog.service.VelogCrawlerService;
import blogpj.blog.service.VelogCrawlerService.VelogPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final VelogCrawlerService velogCrawlerService;

    @GetMapping("/")
    public String home(Model model) {
        List<VelogPost> posts = velogCrawlerService.getTopPosts();
        model.addAttribute("velogPosts", posts);
        return "index";  // src/main/resources/templates/index.html
    }
}
