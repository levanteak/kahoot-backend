package com.kahoot.kahoot.controller;

import com.kahoot.kahoot.model.*;
import com.kahoot.kahoot.model.dto.LinkRequest;
import com.kahoot.kahoot.model.dto.*;
import com.kahoot.kahoot.model.dto.QuestionDTO;
import com.kahoot.kahoot.model.dto.QuestionRequestDTO;
import com.kahoot.kahoot.repository.UserRepository;
import com.kahoot.kahoot.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.kahoot.kahoot.config.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "API for Admin actions")
@CrossOrigin("http://localhost:3000")
//@CrossOrigin("https://kahoot-frontend-phi.vercel.app")
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

    public AdminController(QuestionService questionService) {
        this.questionService = questionService;
    }


    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        Optional<User> optionalUser = repository.findByUsername(body.getUsername());

        if (optionalUser.isEmpty()) {
            logger.warn("Login failed: User {} not found", body.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username or password");
        }

        User user = optionalUser.get();

        // Логируем пароли для отладки
        logger.info("Stored password hash in DB: {}", user.getPassword());
        logger.info("Entered password: {}", body.getPassword());
        logger.info("Encoded entered password: {}", passwordEncoder.encode(body.getPassword()));

        if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Incorrect password for user {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username or password");
        }

        String token = tokenService.generateToken(user);
        logger.info("User {} logged in successfully", user.getUsername());
        return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), token));
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
    public QuestionDTO addQuestion(@RequestBody QuestionRequestDTO questionRequestDTO) {
        logger.info("Adding question to quiz with ID: {}", questionRequestDTO.getQuizId());
        return questionService.addQuestion(questionRequestDTO);
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
