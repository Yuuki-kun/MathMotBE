package com.mot.mot.model.dto;

import com.mot.mot.model.entity.Exam;
import com.mot.mot.model.entity.Student;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttemptDto {


    private Long id;

    private Date attemptDate;

    private Float point;

    private Boolean finished;

}
