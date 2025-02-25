package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.User;
import com.kahoot.kahoot.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<String> login(String username, String password, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr(); // Получаем IP-адрес клиента
        logger.info("Login attempt from IP: {}, Username: {}", ipAddress, username);

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Login failed: empty username or password from IP: {}", ipAddress);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password must not be empty");
        }

        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            logger.info("Login successful for username: {} from IP: {}", username, ipAddress);
            return ResponseEntity.ok("Login successful");
        } else {
            logger.warn("Failed login attempt for username: {} from IP: {}", username, ipAddress);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid username or password");
        }
    }
}
