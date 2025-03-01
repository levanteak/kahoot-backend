package com.kahoot.kahoot.controller;

import com.kahoot.kahoot.model.*;
import com.kahoot.kahoot.model.dto.*;
import com.kahoot.kahoot.service.LinkService;
import com.kahoot.kahoot.service.ParticipantService;
import com.kahoot.kahoot.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz Controller", description = "API for managing quizzes")
//@CrossOrigin("https://kahoot-frontend-phi.vercel.app")
@CrossOrigin("http://localhost:3000")

public class QuizController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @GetMapping("/{linkId}")
    public TestSession getQuiz(@PathVariable String linkId) {
        logger.info("Received request to get quiz for linkId: {}", linkId);

        // Получаем ссылку по токену
        Link link = linkService.getLinkByToken(linkId);
        logger.info("Found link with token: {}, associated with quiz: {}", link.getToken(), link.getQuiz().getTitle());

        // Получаем случайные вопросы и преобразуем их в DTO
        List<QuestionDTO> questionDTOs = questionService.getRandomQuestions(link.getQuiz().getId(), link.getQuiz().getNumQuestions())
                .stream()
                .map(q -> new QuestionDTO(
                        q.getId(),
                        q.getQuestionText(),
                        q.getQuestionType(),
                        q.getImageBase64(),
                        q.getAnswers().stream()
                                .map(a -> new AnswerDTO(a.getId(), a.getText(), a.isCorrect()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        // Возвращаем сессию теста
        return new TestSession(link, questionDTOs);
    }





    @PostMapping(value = "/{linkId}/submit", consumes = "application/json")
    public SubmissionResult submitAnswers(@PathVariable String linkId, @RequestBody Submission submission) {
        logger.info("Received submission for linkId: {}", linkId);
        logger.info("Participant name: {}", submission.getParticipantName());
        logger.info("Number of responses received: {}", submission.getResponses() != null ? submission.getResponses().size() : 0);

        // Get the link by token
        Link link = linkService.getLinkByToken(linkId);
        logger.info("Found link with token: {} associated with quiz: {}", link.getToken(), link.getQuiz().getTitle());

        // Create a new participant
        Participant participant = participantService.createParticipant(submission.getParticipantName(), link);
        logger.info("Created participant with name: {} and link: {}", participant.getName(), participant.getLink().getToken());

        // Process the responses
        List<Response> responses = submission.getResponses();
        logger.info("Processing {} responses for participant: {}", responses.size(), participant.getName());

        // Submit the answers
        participantService.submitAnswers(participant.getId(), responses);
        logger.info("Answers submitted for participant: {}. Calculating score.", participant.getName());

        // Get the participant's score after submission
        int score = participant.getScore();
        logger.info("Participant {} scored: {}", participant.getName(), score);

        // Return the submission result
        return new SubmissionResult(participant.getName(), score);
    }
}
