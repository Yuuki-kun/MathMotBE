package com.mot.mot.model.dto;

import com.mot.mot.model.enums.ClassStatus;
import com.mot.mot.model.enums.EnrollmentStatus;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClassSearchDto {
    private Long id;
    private String className;

    private String teacher;
   private ClassStatus classStatus;
   private EnrollmentStatus enrollmentStatus;
}
