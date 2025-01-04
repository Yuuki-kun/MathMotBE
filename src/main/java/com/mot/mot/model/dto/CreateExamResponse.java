package com.mot.mot.model.dto;

import com.mot.mot.model.entity.Question;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamResponse {
    private Long id;
    private String title;
    private String description;
    private List<Question> questions;
    private int totalQuestions;
}
