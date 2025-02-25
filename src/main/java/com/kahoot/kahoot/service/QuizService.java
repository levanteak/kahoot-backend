package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.Participant;
import com.kahoot.kahoot.model.Quiz;
import com.kahoot.kahoot.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    public Quiz createQuiz(Quiz quiz) {
        logger.info("Creating a new quiz with title: '{}'", quiz.getTitle());
        quiz.setCreatedAt(LocalDateTime.now()); // Set the current date and time
        Quiz savedQuiz = quizRepository.save(quiz); // Save the quiz to the database
        logger.info("Quiz '{}' created successfully with ID: {}", quiz.getTitle(), savedQuiz.getId());
        return savedQuiz;
    }

    public List<Participant> getResults(Long quizId) {
        logger.info("Fetching results for quizId: {}", quizId);
        List<Participant> participants = quizRepository.getResults(quizId);

        if (participants.isEmpty()) {
            logger.warn("No participants found for quizId: {}", quizId);
        } else {
            logger.info("Participants found for quizId {}: {}", quizId, participants.size());
        }

        return participants;
    }
    public List<Quiz> getAllQuizzes() {
        logger.info("Fetching all quizzes");
        return quizRepository.findAll();
    }

}
