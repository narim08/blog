package blogpj.blog.dto;

public class CodeAnalysisResponse {
    private String correctedCode;

    public CodeAnalysisResponse(String correctedCode) {
        this.correctedCode = correctedCode;
    }

    public String getCorrectedCode() {
        return correctedCode;
    }

    public void setCorrectedCode(String correctedCode) {
        this.correctedCode = correctedCode;
    }
}
