package com.kahoot.kahoot.controller;

import com.kahoot.kahoot.model.Link;
import com.kahoot.kahoot.model.Participant;
import com.kahoot.kahoot.model.Question;
import com.kahoot.kahoot.model.Response;
import com.kahoot.kahoot.model.dto.Submission;
import com.kahoot.kahoot.model.dto.SubmissionResult;
import com.kahoot.kahoot.model.dto.TestSession;
import com.kahoot.kahoot.service.LinkService;
import com.kahoot.kahoot.service.ParticipantService;
import com.kahoot.kahoot.service.QuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz Controller", description = "API for managing quizzes")
@CrossOrigin("https://kahoot-frontend-phi.vercel.app")
//@CrossOrigin("http://localhost:3000")

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

        // Get the link using the token (linkId)
        Link link = linkService.getLinkByToken(linkId);
        logger.info("Found link with token: {}, associated with quiz: {}", link.getToken(), link.getQuiz().getTitle());

        // Get the random questions for the quiz
        List<Question> questions = questionService.getRandomQuestions(link.getQuiz().getId(), link.getQuiz().getNumQuestions());
        logger.info("Fetched {} random questions for quiz: {}", questions.size(), link.getQuiz().getTitle());

        // Return the test session
        return new TestSession(link, questions);
    }

    @PostMapping("/{linkId}/submit")
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
