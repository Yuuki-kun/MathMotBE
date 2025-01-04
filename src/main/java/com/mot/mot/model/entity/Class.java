package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mot.mot.model.enums.ClassStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class",
    indexes = {
        @Index(name = "idx_class_name", columnList = "class_name"),
    }
)
public class Class {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 255 , nullable = false)
    private String className;
    private Long classGrade;

    @Enumerated(EnumType.STRING)
    private ClassStatus classStatus;

    @Column(length = 1000)
    private String classDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;

    private Date createdAt;

    @OneToMany(mappedBy = "assignedClass", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Exam> exams;
}
