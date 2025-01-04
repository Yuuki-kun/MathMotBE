package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mot.mot.model.enums.ExamType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exam")
public class Exam {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean published;

    private Date startDate;

    private Date endDate;

    private Date createdDate;

    private Long timeLimit;

    private Integer retakeLimit;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private Long autoCloseAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_class_id")
    @JsonIgnore
    private Class assignedClass;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();



}
