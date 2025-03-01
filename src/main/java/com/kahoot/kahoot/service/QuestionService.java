package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.Answer;
import com.kahoot.kahoot.model.Question;
import com.kahoot.kahoot.model.Quiz;
import com.kahoot.kahoot.model.dto.AnswerDTO;
import com.kahoot.kahoot.model.dto.QuestionDTO;
import com.kahoot.kahoot.model.dto.QuestionRequestDTO;
import com.kahoot.kahoot.model.enums.QuestionType;
import com.kahoot.kahoot.repository.QuestionRepository;
import com.kahoot.kahoot.repository.QuizRepository;
import jakarta.transaction.Transactional;
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

    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public QuestionDTO addQuestion(QuestionRequestDTO questionRequestDTO) {
        logger.info("Starting to add a new question...");

        if (questionRequestDTO.getQuizId() == null) {
            throw new IllegalArgumentException("Quiz ID must be provided.");
        }

        Quiz quiz = quizRepository.findById(questionRequestDTO.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        Question question = new Question();
        question.setQuestionText(questionRequestDTO.getQuestionText());
        question.setQuiz(quiz);
        question.setQuestionType(questionRequestDTO.getQuestionType());
        question.setImageBase64(questionRequestDTO.getImageBase64());

        List<Answer> answers = questionRequestDTO.getAnswers().stream()
                .map(answerDTO -> {
                    logger.info("Mapping answer: text={}, correct={}", answerDTO.getText(), answerDTO.isCorrect());
                    Answer answer = new Answer();
                    answer.setText(answerDTO.getText());
                    answer.setCorrect(answerDTO.isCorrect());
                    answer.setQuestion(question);
                    return answer;
                })
                .collect(Collectors.toList());

        if (question.getQuestionType() == QuestionType.SINGLE_CHOICE) {
            long correctCount = answers.stream().filter(Answer::isCorrect).count();
            if (correctCount != 1) {
                throw new IllegalArgumentException("For SINGLE_CHOICE question, exactly one answer must be correct.");
            }
        }

        question.setAnswers(answers);
        Question savedQuestion = questionRepository.save(question);
        logger.info("New question added: ID={}, QuizID={}, Text='{}', Answer count={}",
                savedQuestion.getId(), savedQuestion.getQuiz().getId(), savedQuestion.getQuestionText(), savedQuestion.getAnswers().size());

        return convertToDTO(savedQuestion);
    }

    @Transactional
    public List<QuestionDTO> getRandomQuestions(Long quizId, int limit) {
        logger.info("Fetching {} random questions for quiz ID={}", limit, quizId);
        List<Question> questions = questionRepository.findRandomByQuizId(quizId, limit);

        if (questions.isEmpty()) {
            logger.warn("No questions found for quiz ID={}", quizId);
        } else {
            logger.info("Fetched {} random questions for quiz ID={}", questions.size(), quizId);
        }

        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private QuestionDTO convertToDTO(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                .map(answer -> new AnswerDTO(answer.getId(), answer.getText(), answer.isCorrect()))
                .collect(Collectors.toList());

        return new QuestionDTO(
                question.getId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getImageBase64(),
                answerDTOs
        );
    }
}
