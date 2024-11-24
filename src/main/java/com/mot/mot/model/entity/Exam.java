package com.mot.mot.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private String description;

    private Boolean published;

    private Date startDate;

    private Date endDate;

    private Date createdDate;

    private Long timeLimit;

    private Boolean canRetake;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_class_id")
    private Class assignedClass;


}
