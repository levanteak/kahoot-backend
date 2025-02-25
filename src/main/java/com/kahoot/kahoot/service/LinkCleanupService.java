package com.kahoot.kahoot.service;

import com.kahoot.kahoot.model.Link;
import com.kahoot.kahoot.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(LinkCleanupService.class);

    @Autowired
    private LinkRepository linkRepository;

    @Scheduled(fixedRate = 60000) // Запуск раз в 60 секунд
    public void cleanupExpiredLinks() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Starting expired links cleanup at {}", now);

        List<Link> expiredLinks = linkRepository.findAllByExpiresAtBeforeAndIsUsedFalse(now);
        if (expiredLinks.isEmpty()) {
            logger.info("No expired links found at {}", now);
        } else {
            logger.info("Found {} expired links at {}", expiredLinks.size(), now);
            for (Link link : expiredLinks) {
                link.setIsUsed(true);
                linkRepository.save(link);
                logger.info("Link with token {} (ID: {}) marked as used. Expired at {}",
                        link.getToken(), link.getId(), link.getExpiresAt());
            }
        }

        logger.info("Expired links cleanup finished at {}", LocalDateTime.now());
    }
}
