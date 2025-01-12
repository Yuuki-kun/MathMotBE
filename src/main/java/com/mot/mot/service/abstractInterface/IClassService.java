package com.mot.mot.service.abstractInterface;

import com.mot.mot.model.dto.ClassDto;
import com.mot.mot.model.dto.ClassSearchDto;
import com.mot.mot.model.entity.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IClassService extends ICrudService<Class> {
    Page<Class> findAllByStudentId(Long studentId, Pageable pageable);

    List<ClassSearchDto> findAllByClassName(String className, Long studentId);

    Page<ClassDto> findAllByTeacherId(Long teacherId, Pageable pageable);

    ClassDto findClassDtoById(Long classId);
}
