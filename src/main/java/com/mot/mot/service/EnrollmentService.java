package com.mot.mot.service;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.User;
import com.mot.mot.model.entity.*;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.enums.EnrollmentStatus;
import com.mot.mot.model.request.EnrollmentRequest;
import com.mot.mot.repository.ClassRepository;
import com.mot.mot.repository.EnrollmentRepository;
import com.mot.mot.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class EnrollmentService extends CrudServiceImpl<Enrollment> implements IEnrollmentService  {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    public EnrollmentService(EnrollmentRepository repository, StudentRepository studentRepository, ClassRepository classRepository) {
        super(repository);
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
        this.enrollmentRepository = repository;
    }

    @Override
    public Enrollment create(Object object) {
        if(object instanceof EnrollmentRequest enrollmentRequest){

            Enrollment enrollment = enrollmentRepository.findByStudentIdAndClassId(enrollmentRequest.getStudentId(),
                    enrollmentRequest.getClassId()).orElse(null);

            if(enrollment != null && enrollment.getStatus().equals(EnrollmentStatus.ENROLLED)){
                throw new CustomBadRequestException("Student already enrolled in this class");
            }

            Class classById = Class.builder().id(enrollmentRequest.getClassId()).build();
            Student studentById = Student.builder().studentId(enrollmentRequest.getStudentId()).build();

            var toSaveEnrollment = Enrollment.builder()
                    .student(studentById)
                    .enrolClass(classById)
                    .enrollmentDate(enrollmentRequest.getEnrollmentDate())
                    .status(enrollmentRequest.getStatus())
                    .build();

            return repository.save(toSaveEnrollment);

        }else {
            throw new CustomBadRequestException("Invalid object type for create method");
        }

    }

    @Override
    @Transactional
    public Enrollment enrollStudent(EnrollmentRequest enrollmentRequest) {
        Class joinClass = classRepository.findById(enrollmentRequest.getClassId()).orElseThrow(() -> new CustomBadRequestException("Class not found"));
        Student student = studentRepository.findById(enrollmentRequest.getStudentId()).orElseThrow(() -> new CustomBadRequestException("Student not found"));


        switch (joinClass.getClassStatus()){
            case PUBLIC -> {
                enrollmentRequest.setStatus(EnrollmentStatus.ENROLLED);
            }
            case PRIVATE -> {
                enrollmentRequest.setStatus(EnrollmentStatus.WAITING);
            }
        }
        var en = create(enrollmentRequest);
        en.setEnrolClass(joinClass);
        en.setStudent(student);
        return en;
    }

    @Override
    public Enrollment findByStudentIdAndClassId(Long studentId, Long classId) {
        return enrollmentRepository.findByStudentIdAndClassId(studentId, classId).orElseThrow(()-> new CustomBadRequestException("Enrollment not found"));
    }
}
