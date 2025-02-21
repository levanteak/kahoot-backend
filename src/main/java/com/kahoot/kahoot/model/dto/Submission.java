package com.kahoot.kahoot.model;

import lombok.Data;
import java.util.List;

@Data
public class Submission {
    private String participantName;
    private List<Response> responses;
}
