package com.mot.mot.service;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.entity.Enrollment;
import com.mot.mot.model.request.EnrollmentRequest;
import com.mot.mot.repository.ClassRepository;
import com.mot.mot.repository.EnrollmentRepository;
import com.mot.mot.repository.StudentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService extends CrudServiceImpl<Enrollment> implements IEnrollmentService  {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    public EnrollmentService(EnrollmentRepository repository, StudentRepository studentRepository, ClassRepository classRepository) {
        super(repository);
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
    }

    @Override
    public Enrollment create(Object object) {
        if(object instanceof EnrollmentRequest enrollmentRequest){

            var joinedStudent = studentRepository.findById(enrollmentRequest.getStudentId());
            if(joinedStudent.isEmpty()){
                throw new CustomBadRequestException("Student not found");
            }

            var joinedClass = classRepository.findById(enrollmentRequest.getClassId());
            if(joinedClass.isEmpty()){
                throw new CustomBadRequestException("Class not found");
            }

            var toSaveEnrollment = Enrollment.builder()
                    .student(joinedStudent.get())
                    .enrolClass(joinedClass.get())
                    .enrollmentDate(enrollmentRequest.getEnrollmentDate())
                    .build();


            return repository.save(toSaveEnrollment);

        }else {
            throw new CustomBadRequestException("Invalid object type for create method");
        }

    }
}
