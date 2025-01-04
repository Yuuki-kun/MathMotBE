package com.mot.mot.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "answer_attempt")
public class AnswerAttempt {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "exam_attempt_id")
    private ExamAttempt examAttempt;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    private Boolean correct;

    private Float point;

}
