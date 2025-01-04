package com.mot.mot.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttemptResultResponse {
    private Long examId;
    private Long examAttemptId;
    private Float totalPoint;
    private Integer numberOfCorrectAnswer;
    private Integer numberOfWrongAnswer;
    private Integer numberOfNotAnsweredQuestion;
    private List<AnswerAttemptResultResponse> answerAttemptResultResponses;

}
