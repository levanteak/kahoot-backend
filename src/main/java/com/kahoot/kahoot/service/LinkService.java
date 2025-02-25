package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.Link;
import com.kahoot.kahoot.model.Quiz;
import com.kahoot.kahoot.repository.LinkRepository;
import com.kahoot.kahoot.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LinkService {

    private static final Logger logger = LoggerFactory.getLogger(LinkService.class);

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private QuizRepository quizRepository;

    public Link createLink(Long quizId, int expirationMinutes) {
        logger.info("Creating link for quiz ID: {} with expiration time of {} minutes", quizId, expirationMinutes);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> {
                    logger.error("Quiz with ID {} not found", quizId);
                    return new IllegalArgumentException("Quiz not found");
                });

        Link link = new Link();
        link.setQuiz(quiz);
        link.setToken(UUID.randomUUID().toString());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        link.setIsUsed(false);

        link = linkRepository.save(link);
        logger.info("Link created successfully: Token = {}, Expires at = {}", link.getToken(), link.getExpiresAt());

        return link;
    }

    public Link getLinkByToken(String token) {
        logger.info("Fetching link with token: {}", token);

        Link link = linkRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Link with token {} not found", token);
                    return new IllegalArgumentException("Link not found");
                });

        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Attempt to access expired link: Token = {}, Expired at = {}", token, link.getExpiresAt());
            throw new IllegalArgumentException("This link has expired");
        }

        logger.info("Link is valid: Token = {}, Expires at = {}", token, link.getExpiresAt());
        return link;
    }
}
