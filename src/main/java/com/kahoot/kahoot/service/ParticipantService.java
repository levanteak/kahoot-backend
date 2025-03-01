package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.*;
import com.kahoot.kahoot.repository.AnswerRepository;
import com.kahoot.kahoot.repository.ParticipantRepository;
import com.kahoot.kahoot.repository.QuestionRepository;
import com.kahoot.kahoot.repository.ResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

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
        logger.info("Starting submitAnswers. participantId: {}, number of responses: {}",
                participantId, responses != null ? responses.size() : 0);

        if (responses == null || responses.isEmpty()) {
            logger.warn("submitAnswers failed: responses list is empty. participantId={}", participantId);
            throw new IllegalArgumentException("Response list cannot be empty.");
        }

        // Загружаем участника
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> {
                    logger.error("Participant not found for ID: {}", participantId);
                    return new EntityNotFoundException("Participant not found for ID: " + participantId);
                });

        List<Response> validResponses = new ArrayList<>();

        for (Response response : responses) {
            if (response == null || response.getAnswers() == null || response.getAnswers().isEmpty()) {
                logger.warn("Invalid response detected. participantId={}, responseId={}",
                        participantId, response != null ? response.getId() : null);
                throw new IllegalArgumentException("Each response must contain at least one answer.");
            }

            // Получаем ID всех ответов из запроса
            List<Long> answerIds = response.getAnswers().stream().map(Answer::getId).toList();
            logger.info("Fetching answers for IDs: {}", answerIds);

            // Получаем ответы одним запросом
            Map<Long, Answer> answersMap = answerRepository.findAllById(answerIds)
                    .stream().collect(Collectors.toMap(Answer::getId, a -> a));

            List<Answer> answers = response.getAnswers().stream()
                    .map(answer -> {
                        Answer foundAnswer = answersMap.get(answer.getId());
                        if (foundAnswer == null) {
                            logger.error("Answer not found for ID: {}", answer.getId());
                            throw new EntityNotFoundException("Answer not found for ID: " + answer.getId());
                        }
                        return foundAnswer;
                    })
                    .collect(Collectors.toList());

            if (answers.isEmpty()) {
                logger.error("No valid answers found for response. participantId={}, responseId={}",
                        participantId, response.getId());
                throw new IllegalStateException("No valid answers found for response.");
            }

            // Проверяем, что все ответы принадлежат одному и тому же вопросу
            Question question = answers.get(0).getQuestion();
            if (answers.stream().anyMatch(a -> !a.getQuestion().equals(question))) {
                logger.error("All answers must belong to the same question. participantId={}, responseId={}",
                        participantId, response.getId());
                throw new IllegalStateException("All answers must belong to the same question.");
            }

            response.setQuestion(question);
            response.setAnswers(answers);
            response.setParticipant(participant);
            validResponses.add(response);

            logger.info("Processed response: participantId={}, questionId={}, answers={}",
                    participantId, question.getId(), answers.stream().map(Answer::getId).toList());
        }

        // Сохраняем ответы, если они есть
        if (!validResponses.isEmpty()) {
            responseRepository.saveAll(validResponses);
            logger.info("Responses successfully saved. participantId={}, totalResponses={}",
                    participantId, validResponses.size());
        } else {
            logger.warn("No valid responses to save. participantId={}", participantId);
        }

        // Обновляем баллы участника
        int newScore = calculateScore(validResponses);
        participant.setScore(participant.getScore() + newScore);

        Instant scoreUpdateStart = Instant.now();
        participantRepository.save(participant);
        logger.info("Score updated: participantId={}, new score={}, update time={} ms",
                participantId, participant.getScore(), Duration.between(scoreUpdateStart, Instant.now()).toMillis());

        logger.info("submitAnswers completed in {} ms", Duration.between(startTime, Instant.now()).toMillis());
    }




    private int calculateScore(List<Response> responses) {
        return (int) responses.stream()
                .filter(response -> response.getAnswers().stream().allMatch(Answer::isCorrect))
                .count() * 10;
    }


}
