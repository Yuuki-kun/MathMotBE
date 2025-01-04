package com.mot.mot.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private Long id;

    private String title;

    private String type;

    private Float point;

    private Integer level;

    private List<AnswerDto> answers = new ArrayList<>();

    private Integer orderNumber;
}
