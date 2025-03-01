package com.kahoot.kahoot.model.dto;




import com.kahoot.kahoot.model.Link;
import lombok.Data;
import java.util.List;

@Data
public class TestSession {
    private Link link;
    private List<QuestionDTO> questions;

    public TestSession(Link link, List<QuestionDTO> questions) {
        this.link = link;
        this.questions = questions;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}
