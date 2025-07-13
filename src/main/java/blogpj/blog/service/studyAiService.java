package blogpj.blog.service;

import blogpj.blog.dto.QuestionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class studyAiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public QuestionDto generateQuestionFromGemini() {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = """
            소프트웨어 개발 관련 퀴즈 문제를 하나 출제해줘. 다음 조건을 반드시 따라줘:
        
            1. 문제는 객관식 4지선다형 형식으로 구성해줘.
            2. 주제는 개발 이론(예: 객체지향, 소프트웨어 공학, 디자인 패턴 등)과 프로그래밍 문법(Java, Python, C 등)을 번갈아가며 섞어줘.
            3. 특정 주제만 반복하지 말고 매번 무작위로 다른 주제를 선택해줘.
            4. 오답은 쉼표를 포함하지 않는 독립적인 문장으로 정확히 3개만 작성해줘.
            5. 출력 형식은 아래와 똑같이 맞춰줘. (변형 금지):
        
            문제: [문제 내용]
            정답: [정답 보기]
            오답: [오답 보기1] / [오답 보기2] / [오답 보기3]
            설명: [정답에 대한 간단한 설명]
        
            프로그래밍 문법 예시: 반복문, 조건문, 자료형, 클래스/상속, 인터페이스, 예외 처리, 컬렉션 사용 등
            개발 이론 예시: 애자일, 폭포수, SOLID, 캡슐화, 정렬 알고리즘 등
            
            *꼭 위 형식을 지키고, 오답은 반드시 슬래시(/)로 구분해줘.
            *오답 개수는 정확히 3개여야 하며, 쉼표나 중복이 있으면 안 돼.
        """;


        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        Map<String, Object> response = restTemplate.postForObject(
                GEMINI_URL + apiKey,
                body,
                Map.class
        );

        // 응답 파싱
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini 응답에 candidates가 없습니다.");
        }

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String text = (String) parts.get(0).get("text");

        return parseResponse(text);
    }

    private QuestionDto parseResponse(String text) {
        QuestionDto dto = new QuestionDto();

        String[] lines = text.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("문제:")) {
                dto.setQuestion(line.substring("문제:".length()).trim());

            } else if (line.startsWith("정답:")) {
                dto.setCorrectAnswer(line.substring("정답:".length()).trim());

            } else if (line.startsWith("오답:")) {
                String raw = line.substring("오답:".length()).trim();

                // 슬래시(/) 기준으로 분리
                String[] split = raw.split("/");
                List<String> wrongs = Arrays.stream(split)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .limit(3)
                        .toList();

                dto.setWrongAnswers(wrongs);

            } else if (line.startsWith("설명:")) {
                dto.setExplanation(line.substring("설명:".length()).trim());
            }
        }

        return dto;
    }


    /*private QuestionDto parseResponse(String text) {
        QuestionDto dto = new QuestionDto();

        for (String line : text.split("\n")) {
            line = line.trim();

            if (line.startsWith("문제:")) {
                dto.setQuestion(line.replace("문제:", "").trim());

            } else if (line.startsWith("정답:")) {
                dto.setCorrectAnswer(line.replace("정답:", "").trim());

            } else if (line.startsWith("오답:")) {
                // 오답: ['내용1', '내용2', '내용3']  형식 처리
                String raw = line.replace("오답:", "").trim();

                // 문자열 제거하고 쉼표 기준으로 자르기
                raw = raw.replaceAll("[\\[\\]'\"]", "");  // 괄호/따옴표 제거
                List<String> wrongs = Arrays.stream(raw.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
                dto.setWrongAnswers(wrongs);

            } else if (line.startsWith("설명:")) {
                dto.setExplanation(line.replace("설명:", "").trim());
            }
        }

        return dto;
    }*/


}
