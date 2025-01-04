package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JsonBackReference
    private Question question;

    @Override public String toString() { return "Answer{" + "id=" + id + ", correct=" + correct + ", content='" + content + '\'' + ", letter='" + letter + '\'' + '}'; }



}
