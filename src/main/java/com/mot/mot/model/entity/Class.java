package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 255)
    private String className;
    private Long classGrade;
    private Boolean classStatus;

    @Column(length = 1000)
    private String classDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;

    private Date createdAt;
}
