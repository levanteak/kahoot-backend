package com.kahoot.kahoot.repository;

import com.kahoot.kahoot.model.Participant;
import com.kahoot.kahoot.model.Question;
import com.kahoot.kahoot.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    boolean existsByParticipantAndQuestion(Participant participant, Question question);
}
