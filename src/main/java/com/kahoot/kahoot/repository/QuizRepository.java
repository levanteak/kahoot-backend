package com.kahoot.kahoot.repository;

import com.kahoot.kahoot.model.Participant;
import com.kahoot.kahoot.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT p FROM Participant p WHERE p.link.quiz.id = :quizId ORDER BY p.score DESC")
    List<Participant> getResults(@Param("quizId") Long quizId);

}
