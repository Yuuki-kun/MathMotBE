package com.mot.mot.repository;

import com.mot.mot.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.enrolClass.id = :classId")
    Optional<Enrollment> findByStudentIdAndClassId(Long studentId, Long classId);

    @Query("SELECT COUNT(1) FROM Enrollment e WHERE e.enrolClass.id = :classId AND e.status = 'ENROLLED'")
    int countStudentsByEnrolClassId(Long classId);

}
