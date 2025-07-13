package blogpj.blog.dto;

public class AnswerResultDto {
    private boolean correct;
    private String explanation;

    public AnswerResultDto(boolean correct, String explanation) {
        this.correct = correct;
        this.explanation = explanation;
    }

    public boolean isCorrect() { return correct; }
    public String getExplanation() { return explanation; }
}
