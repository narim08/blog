package blogpj.blog.controller;

import blogpj.blog.service.VelogCrawlerService;
import blogpj.blog.service.VelogCrawlerService.VelogPost;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VelogController {

    private final VelogCrawlerService velogCrawlerService;

    @GetMapping("/api/velog-top")
    public List<VelogPost> getVelogTopPosts() {
        return velogCrawlerService.getTopPosts();
    }
}
