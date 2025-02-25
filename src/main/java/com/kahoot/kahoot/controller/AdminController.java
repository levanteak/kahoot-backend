package com.kahoot.kahoot.controller;

import com.kahoot.kahoot.model.Link;
import com.kahoot.kahoot.model.Participant;
import com.kahoot.kahoot.model.Question;
import com.kahoot.kahoot.model.Quiz;
import com.kahoot.kahoot.model.dto.LinkRequest;
import com.kahoot.kahoot.model.dto.LoginRequest;
import com.kahoot.kahoot.model.dto.QuestionRequestDTO;
import com.kahoot.kahoot.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "API for Admin actions")
//@CrossOrigin("http://localhost:3000")
@CrossOrigin("https://kahoot-frontend-phi.vercel.app")
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        logger.info("Admin login attempt with username: {}", loginRequest.getUsername());
        ResponseEntity<String> result = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), request);
        logger.info("Admin login result for username {}: {}", loginRequest.getUsername(), result.getBody());
        return result;
    }


    @PostMapping("/quiz")
    public Quiz createQuiz(@RequestBody Quiz quiz) {
        logger.info("Creating quiz with title: {}", quiz.getTitle());
        Quiz createdQuiz = quizService.createQuiz(quiz);
        logger.info("Quiz created with ID: {}", createdQuiz.getId());
        return createdQuiz;
    }

    @GetMapping("/quizzes")
    public List<Quiz> getAllQuizzes() {
        logger.info("Fetching all quizzes");
        return quizService.getAllQuizzes();
    }


    @PostMapping("/questions")
    public Question addQuestion(@RequestBody QuestionRequestDTO questionRequestDTO) {
        logger.info("Adding question to quiz with ID: {}", questionRequestDTO.getQuizId());
        Question question = questionService.addQuestion(questionRequestDTO);
        logger.info("Question added with ID: {}", question.getId());
        return question;
    }

    @GetMapping("/results/{quizId}")
    public List<Participant> getResults(@PathVariable Long quizId) {
        logger.info("Fetching results for quiz with ID: {}", quizId);
        List<Participant> participants = quizService.getResults(quizId);
        logger.info("Fetched results for {} participants in quiz with ID: {}", participants.size(), quizId);
        return participants;
    }

    @PostMapping("/links")
    public Link createLink(@RequestBody LinkRequest linkRequest) {
        logger.info("Creating link for quiz with ID: {} and expiration time: {} minutes", linkRequest.getQuizId(), linkRequest.getExpirationMinutes());
        Link link = linkService.createLink(linkRequest.getQuizId(), linkRequest.getExpirationMinutes());
        logger.info("Link created with token: {}", link.getToken());
        return link;
    }
}
