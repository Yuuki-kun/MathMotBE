package com.mot.mot.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mot.mot.model.entity.Teacher;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassDto {

    private Long id;
    private String className;
    private Long classGrade;
    private Boolean classStatus;
    private String classDesc;
    private Date createdAt;

}
