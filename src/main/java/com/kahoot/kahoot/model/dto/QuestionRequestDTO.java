package com.kahoot.kahoot.model.dto;

import java.util.List;

public class QuestionRequestDTO {
    private String questionText;
    private Long quizId;
    private List<AnswerRequestDTO> answers;

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
}
