package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private Integer level;

    private Integer orderNumber;


    @ManyToOne
    @JoinColumn(name = "exam_id")
    @JsonIgnore
    private Exam exam;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Answer> answers = new ArrayList<>();

    @Override public String toString() { return "Question{" + "id=" + id + ", title='" + title + '\'' + ", point=" + point + ", type='" + type + '\'' + '}'; }


}

