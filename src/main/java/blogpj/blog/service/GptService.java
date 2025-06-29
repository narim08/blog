package blogpj.blog.service;

import blogpj.blog.dto.ProjectRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class GptService {
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    public GptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateCode(ProjectRequest req) {
        String prompt = buildPrompt(req);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 유능한 프로그래머입니다."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                requestEntity,
                Map.class
        );

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }

    private String buildPrompt(ProjectRequest r) {
        return String.format("""
                다음 조건에 맞는 프로그램 코드를 작성해주세요:

                1. 언어: %s
                2. 알고리즘: %s
                3. 주제: %s
                4. 입력 형식: %s
                5. 입력 예시 및 조건: %s
                6. 출력 형식: %s
                7. 출력 예시: %s
                8. 잘못된 입력 처리 방식: %s
                9. 주의사항: %s
                10. 코드 스타일: %s
                """,
                r.language,
                String.join(", ", r.algorithms),
                r.topic,
                String.join(", ", r.input_format),
                r.input_example,
                String.join(", ", r.output_format),
                r.output_example,
                r.error_handling,
                r.warnings,
                String.join(", ", r.style)
        );
    }
}
