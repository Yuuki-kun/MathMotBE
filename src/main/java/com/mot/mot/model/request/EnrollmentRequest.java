package com.mot.mot.model.request;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    private Long id;
    @Min(value = 0, message = "Student ID khong hop le")
    private Long studentId;
    @Min(value = 0, message = "Class ID khong hop le")
    private Long classId;

    private Date enrollmentDate;

    //join class directly or wait for approval
    private boolean isDirect;
    private Long userId;
}
