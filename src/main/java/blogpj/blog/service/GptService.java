package blogpj.blog.service;

import blogpj.blog.dto.ProjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException; // HttpServerErrorException 임포트
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GptService {

    private final RestTemplate restTemplate;


    @Value("${gemini.api.key}")
    private String apiKey;

    private final String modelName = "gemini-2.5-flash";

    public GptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // --- 이 메서드를 아래 코드로 전체 교체 ---
    public String generateCode(ProjectRequest req) {
        String apiUrl = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                modelName,
                apiKey
        );

        // 1. 재시도 횟수와 대기 시간 설정
        int maxAttempts = 3; // 최대 3번 시도
        long delayMillis = 2000; // 2초 대기

        // 2. 재시도를 위한 for 반복문 시작
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                // --- 기존 API 호출 코드는 그대로 사용 ---
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String prompt = buildPrompt(req);

                Map<String, Object> parts = Map.of("text", prompt);
                Map<String, Object> contents = Map.of("parts", List.of(parts));
                Map<String, Object> body = Map.of("contents", List.of(contents));

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                Map<String, Object> response = restTemplate.postForObject(
                        apiUrl,
                        requestEntity,
                        Map.class
                );
                // --- API 호출 성공 시, 결과 파싱 후 즉시 반환 ---
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");
                System.out.println((String) responseParts.get(0).get("text"));
                return (String) responseParts.get(0).get("text");

            } catch (HttpServerErrorException e) {
                // 3. 503 에러가 발생하면 여기로 들어옴
                System.out.println("API 요청 실패 (시도 " + attempt + "/" + maxAttempts + "): " + e.getMessage());

                // 마지막 시도였다면, 더 이상 재시도하지 않고 사용자에게 에러 메시지 반환
                if (attempt == maxAttempts) {
                    return "Gemini API 서버가 현재 응답하지 않습니다. 나중에 다시 시도해주세요.";
                }

                // 마지막 시도가 아니라면, 잠시 대기 후 다음 시도 진행
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return "요청 처리 중 오류가 발생했습니다.";
                }
            }
        }
        // for문이 모두 실패한 경우 (이론상 도달하기 어려움)
        return "Gemini API 요청에 최종적으로 실패했습니다.";
    }


    private String buildPrompt(ProjectRequest r) {
        // 이 메서드는 수정할 필요 없습니다.
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