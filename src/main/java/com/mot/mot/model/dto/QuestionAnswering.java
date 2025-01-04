package com.mot.mot.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswering {

    private Long questionId;

    private Long answerId;

}
