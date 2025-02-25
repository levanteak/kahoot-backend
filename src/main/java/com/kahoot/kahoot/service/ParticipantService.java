package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.*;
import com.kahoot.kahoot.repository.AnswerRepository;
import com.kahoot.kahoot.repository.ParticipantRepository;
import com.kahoot.kahoot.repository.ResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Participant createParticipant(String name, Link link) {
        logger.info("Creating participant with name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            logger.error("Error: Participant name cannot be empty.");
            throw new IllegalArgumentException("Participant name cannot be empty");
        }

        Participant participant = new Participant();
        participant.setName(name);
        participant.setLink(link);
        participant.setScore(0);

        Participant savedParticipant = participantRepository.save(participant);
        logger.info("Participant created: ID={}, name={}, quizId={}",
                savedParticipant.getId(), savedParticipant.getName(), link.getQuiz().getId());

        return savedParticipant;
    }

    @Transactional
    public void submitAnswers(Long participantId, List<Response> responses) {
        Instant startTime = Instant.now();
        logger.info("Starting submitAnswers. participantId: {}, number of responses: {}", participantId, responses.size());

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> {
                    logger.error("Error: Participant with ID={} not found", participantId);
                    return new EntityNotFoundException("Participant not found for ID: " + participantId);
                });

        Set<Long> answeredQuestionIds = new HashSet<>();

        for (Response response : responses) {
            if (response.getAnswer() == null || response.getAnswer().getId() == null) {
                logger.error("Error: Answer is missing or has no ID: {}", response);
                throw new IllegalArgumentException("Answer is missing or has no ID.");
            }

            Answer answer = answerRepository.findById(response.getAnswer().getId())
                    .orElseThrow(() -> {
                        logger.error("Error: Answer with ID={} not found", response.getAnswer().getId());
                        return new EntityNotFoundException("Answer not found for ID: " + response.getAnswer().getId());
                    });

            Question question = answer.getQuestion();
            if (question == null) {
                logger.error("Error: Question for answer {} not found.", answer.getId());
                throw new IllegalStateException("Question for the answer is missing.");
            }

            response.setQuestion(question);
            response.setAnswer(answer);
            response.setParticipant(participant);

            logger.info("Processed response: participantId={}, questionId={}, answerId={}, correct={}",
                    participantId, question.getId(), answer.getId(), answer.isCorrect());
        }

        responseRepository.saveAll(responses);
        logger.info("Responses successfully saved. participantId={}", participantId);

        int newScore = calculateScore(responses);
        participant.setScore(participant.getScore() + newScore);
        participantRepository.save(participant);

        logger.info("Score updated: participantId={}, new score={}", participantId, participant.getScore());
        logger.info("submitAnswers completed in {} ms", Duration.between(startTime, Instant.now()).toMillis());
    }

    public int calculateScore(List<Response> responses) {
        int score = 0;
        for (Response response : responses) {
            if (response.getAnswer().isCorrect()) {
                score++;
            }
        }
        logger.info("Calculated score: {} based on {} responses", score, responses.size());
        return score;
    }
}
