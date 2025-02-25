package com.kahoot.kahoot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kahoot.kahoot.model.Response;
import lombok.Data;
import java.util.List;

@Data
public class Submission {

    private String participantName;


    private List<Response> responses;

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
}
