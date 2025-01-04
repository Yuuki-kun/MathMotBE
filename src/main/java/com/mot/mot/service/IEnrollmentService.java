package com.mot.mot.service;

import com.mot.mot.model.entity.Enrollment;
import com.mot.mot.model.request.EnrollmentRequest;
import org.springframework.stereotype.Service;

@Service
public interface IEnrollmentService extends ICrudService<Enrollment> {
    Enrollment enrollStudent(EnrollmentRequest enrollmentRequest);
    Enrollment findByStudentIdAndClassId(Long studentId, Long classId);
}
