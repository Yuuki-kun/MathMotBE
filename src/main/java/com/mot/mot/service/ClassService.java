package com.mot.mot.service;

import com.mot.mot.model.entity.Class;
import com.mot.mot.model.entity.Teacher;
import com.mot.mot.model.request.CreateClassRequest;
import com.mot.mot.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassService extends CrudServiceImpl<Class> {

    @Autowired
    public ClassService(ClassRepository repository) {
        super(repository);
    }


    @Override
    public Class create(Object object) {
        if (object instanceof CreateClassRequest) {
            CreateClassRequest createClassRequest = (CreateClassRequest) object;
            Class toSaveClass = Class.builder()
                    .className(createClassRequest.getClassDto().getClassName())
                    .classDesc(createClassRequest.getClassDto().getClassDesc())
                    .classGrade(createClassRequest.getClassDto().getClassGrade())
                    .classStatus(createClassRequest.getClassDto().getClassStatus())
                    .createdAt(createClassRequest.getClassDto().getCreatedAt())
                    .teacher(Teacher.builder().teacherId(createClassRequest.getTeacherId()).build())
                    .build();

            return this.repository.save(toSaveClass);
        } else {
            throw new IllegalArgumentException("Invalid object type for create method");
        }
    }

}
