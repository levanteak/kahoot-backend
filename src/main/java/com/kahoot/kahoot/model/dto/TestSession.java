package com.kahoot.kahoot.model.dto;

import com.kahoot.kahoot.model.Link;
import com.kahoot.kahoot.model.Question;
import lombok.Data;
import java.util.List;

@Data
public class TestSession {
    private Link link;
    private List<Question> questions;

    public TestSession(Link link, List<Question> questions) {
        this.link = link;
        this.questions = questions;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
