package blogpj.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodeAnalysisService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String modelName = "gemini-2.5-flash";

    public String analyzeCode(String userCode) {
        String apiUrl = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                modelName,
                apiKey
        );

        int maxAttempts = 3;
        long delayMillis = 2000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String prompt = String.format("""
                    다음 코드를 분석해서 오류가 있으면 수정하고,
                    전반적으로 더 나은 방식으로 개선해주세요.
                    개선된 코드를 전체 코드 형식으로 다시 작성해주세요.

                    코드:
                    %s
                    """, userCode);

                Map<String, Object> parts = Map.of("text", prompt);
                Map<String, Object> contents = Map.of("parts", List.of(parts));
                Map<String, Object> body = Map.of("contents", List.of(contents));

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                Map<String, Object> response = restTemplate.postForObject(apiUrl, requestEntity, Map.class);

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");

                return (String) responseParts.get(0).get("text");

            } catch (HttpServerErrorException e) {
                System.out.println("API 요청 실패 (시도 " + attempt + "/" + maxAttempts + "): " + e.getMessage());

                if (attempt == maxAttempts) {
                    return "Gemini API 서버가 현재 응답하지 않습니다. 나중에 다시 시도해주세요.";
                }

                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return "요청 처리 중 오류가 발생했습니다.";
                }
            }
        }

        return "Gemini API 요청에 최종적으로 실패했습니다.";
    }
}
