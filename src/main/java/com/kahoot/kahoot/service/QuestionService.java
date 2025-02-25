package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.Answer;
import com.kahoot.kahoot.model.Question;
import com.kahoot.kahoot.model.Quiz;
import com.kahoot.kahoot.model.dto.QuestionRequestDTO;
import com.kahoot.kahoot.repository.QuestionRepository;
import com.kahoot.kahoot.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    public Question addQuestion(QuestionRequestDTO questionRequestDTO) {
        logger.info("Starting to add a new question...");

        if (questionRequestDTO.getQuizId() == null) {
            logger.error("Quiz ID is missing in the request.");
            throw new IllegalArgumentException("Quiz ID must be provided.");
        }

        Quiz quiz = quizRepository.findById(questionRequestDTO.getQuizId())
                .orElseThrow(() -> {
                    logger.error("Quiz with ID={} not found.", questionRequestDTO.getQuizId());
                    return new IllegalArgumentException("Quiz not found");
                });

        Question question = new Question();
        question.setQuestionText(questionRequestDTO.getQuestionText());
        question.setQuiz(quiz);

        List<Answer> answers = questionRequestDTO.getAnswers().stream()
                .map(answerDTO -> {
                    Answer answer = new Answer();
                    answer.setText(answerDTO.getText());
                    answer.setCorrect(answerDTO.isCorrect());
                    answer.setQuestion(question);
                    return answer;
                })
                .collect(Collectors.toList());

        question.setAnswers(answers);

        Question savedQuestion = questionRepository.save(question);
        logger.info("New question added: ID={}, QuizID={}, Text='{}', Answer count={}",
                savedQuestion.getId(), savedQuestion.getQuiz().getId(), savedQuestion.getQuestionText(), savedQuestion.getAnswers().size());

        return savedQuestion;
    }

    public List<Question> getRandomQuestions(Long quizId, int limit) {
        logger.info("Fetching {} random questions for quiz ID={}", limit, quizId);
        List<Question> questions = questionRepository.findRandomByQuizId(quizId, limit);

        if (questions.isEmpty()) {
            logger.warn("No questions found for quiz ID={}", quizId);
        } else {
            logger.info("Fetched {} random questions for quiz ID={}", questions.size(), quizId);
        }

        return questions;
    }
}
