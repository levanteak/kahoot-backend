package com.kahoot.kahoot.model;

import lombok.Data;

@Data
public class SubmissionResult {
    private String participantName;
    private int score;

    public SubmissionResult(String participantName, int score) {
        this.participantName = participantName;
        this.score = score;
    }
}
