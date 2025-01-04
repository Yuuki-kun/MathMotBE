package com.mot.mot.model.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerAttemptDto {
    private Long examId;
    private Long examAttemptId;
    private List<QuestionAnswering> questionAnswerings;
}
