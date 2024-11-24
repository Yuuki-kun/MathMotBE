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
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String title;

    private String type;

    private Float point;


    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;
}

