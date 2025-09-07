package blogpj.blog.controller;

import blogpj.blog.dto.AnswerResultDto;
import blogpj.blog.dto.QuestionDto;
import blogpj.blog.dto.UserAnswerDto;
import blogpj.blog.service.studyAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class studyAiController {
    @Autowired
    private studyAiService aiService;

    @GetMapping("/question")
    public QuestionDto getQuestion() {
        return aiService.generateQuestionFromGemini();
    }

    @PostMapping("/answer")
    public AnswerResultDto checkAnswer(@RequestBody UserAnswerDto dto) {
        boolean isCorrect = dto.getSelected().equals(dto.getCorrectAnswer());
        return new AnswerResultDto(isCorrect, dto.getExplanation());
    }

}
