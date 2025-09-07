package blogpj.blog.dto;

public class UserAnswerDto {
    private String selected;
    private String correctAnswer;
    private String explanation;

    // Getters & Setters
    public String getSelected() { return selected; }
    public void setSelected(String selected) { this.selected = selected; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
