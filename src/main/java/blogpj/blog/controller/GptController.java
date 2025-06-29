package blogpj.blog.controller;

import blogpj.blog.dto.GptResponse;
import blogpj.blog.dto.ProjectRequest;
import blogpj.blog.service.GptService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class GptController {
    private final GptService gptService;

    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/generate-project")
    public Map<String, String> generate(@RequestBody ProjectRequest request) {
        String code = gptService.generateCode(request);
        return Map.of("code", code);
    }
}
