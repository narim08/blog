package blogpj.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoadingController {
    @GetMapping("/")
    public String redirectToLoading() {
        return "redirect:/loading.html";
    }
}
