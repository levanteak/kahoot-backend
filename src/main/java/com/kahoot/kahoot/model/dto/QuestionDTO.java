package com.kahoot.kahoot.model.dto;

import com.kahoot.kahoot.model.Answer;
import com.kahoot.kahoot.model.enums.QuestionType;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private String imageBase64;
    private List<AnswerDTO> answers;

    public QuestionDTO(Long id, String questionText, QuestionType questionType, String imageBase64, List<AnswerDTO> answers) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
        this.imageBase64 = imageBase64;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
}
