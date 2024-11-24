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
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean correct;

    private String letter;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
