package com.kahoot.kahoot.model.dto;

import com.kahoot.kahoot.model.enums.QuestionType;
import java.util.List;

public class QuestionRequestDTO {
    private String questionText;
    private Long quizId;
    private List<AnswerRequestDTO> answers;
    private String imageBase64;
    private QuestionType questionType;

    // Геттеры и сеттеры
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<AnswerRequestDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerRequestDTO> answers) {
        this.answers = answers;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
}
