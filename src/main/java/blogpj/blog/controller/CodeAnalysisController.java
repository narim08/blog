package blogpj.blog.controller;
import blogpj.blog.dto.CodeAnalysisRequest;
import blogpj.blog.dto.CodeAnalysisResponse;
import blogpj.blog.service.CodeAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;

    @PostMapping("/analyze-code")
    public ResponseEntity<CodeAnalysisResponse> analyzeCode(@RequestBody CodeAnalysisRequest request) {
        String userCode = request.getCode();
        String improvedCode = codeAnalysisService.analyzeCode(userCode);
        return ResponseEntity.ok(new CodeAnalysisResponse(improvedCode));
    }
}
