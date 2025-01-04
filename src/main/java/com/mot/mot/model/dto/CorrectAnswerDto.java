package com.mot.mot.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorrectAnswerDto {
    private Long examId;
    private Long questionId;
    private Float point;

    //correct answer's id
    private Long answerId;


    public CorrectAnswerDto( Long questionId, Long answerId, Float point) {
        this.questionId = questionId;
        this.answerId = answerId;
        this.point = point;
    }
}
