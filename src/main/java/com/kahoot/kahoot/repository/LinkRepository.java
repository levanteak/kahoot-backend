package com.kahoot.kahoot.repository;

import com.kahoot.kahoot.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByToken(String token);

    List<Link> findAllByExpiresAtBeforeAndIsUsedFalse(LocalDateTime now);
}