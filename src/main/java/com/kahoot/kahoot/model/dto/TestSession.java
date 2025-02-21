package com.kahoot.kahoot.model;

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
}
