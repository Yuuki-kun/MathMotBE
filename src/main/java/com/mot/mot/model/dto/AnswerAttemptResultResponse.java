package com.mot.mot.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerAttemptResultResponse {
    private Long questionId;
    private Long selectedAnswerId;
    private Integer questionOrderNumber;
    private String selectedAnswerLetter;
    private String correctAnswerLetter;
    private Boolean correct;
    private String correctAnswerDetails;
    private String typeOfQuestion;
    private float point;
}
