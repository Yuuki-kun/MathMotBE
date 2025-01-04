package com.mot.mot.service;

import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.dto.ClassDto;
import com.mot.mot.model.dto.ClassSearchDto;
import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ClassService extends CrudServiceImpl<Class> implements IClassService {

    private final ClassRepository classRepository;

    @Autowired
    public ClassService(ClassRepository repository) {
        super(repository);
        this.classRepository = repository;
    }

    @Override
    public ClassDto findClassDtoById(Long classId) {
        Class classEntity = super.getById(classId);
        if (classEntity != null) {
            return ClassDto.builder()
                    .id(classEntity.getId())
                    .className(classEntity.getClassName())
                    .classDesc(classEntity.getClassDesc())
                    .classGrade(classEntity.getClassGrade())
                    .classStatus(classEntity.getClassStatus())
                    .createdAt(classEntity.getCreatedAt())
                    .build();
        }
        return ClassDto.builder().id(-1L).build();

    }


    @Override
    public Class create(Object object) {
        if (object instanceof CreateClassRequest createClassRequest) {
            Class toSaveClass = Class.builder()
                    .className(createClassRequest.getClassDto().getClassName())
                    .classDesc(createClassRequest.getClassDto().getClassDesc())
                    .classGrade(createClassRequest.getClassDto().getClassGrade())
                    .classStatus(createClassRequest.getClassDto().getClassStatus())
                    .createdAt(createClassRequest.getClassDto().getCreatedAt())
                    .teacher(Teacher.builder().teacherId(createClassRequest.getTeacherId()).build())
                    .build();
            return repository.save(toSaveClass);
        } else {
            throw new CustomBadRequestException("Invalid object type for create method");
        }
    }

    @Override
    public Page<Class> findAllByStudentId(Long studentId, Pageable pageable) {
        return classRepository.findAllByStudentId(studentId, pageable);
    }

    @Override
    public List<ClassSearchDto> findAllByClassName(String className, Long studentId) {
        return classRepository.findAllByClassName(className, studentId);
    }

    @Override
    public Page<ClassDto> findAllByTeacherId(Long teacherId, Pageable pageable) {
        return classRepository.findAllByTeacherId(teacherId, pageable);
    }

}
