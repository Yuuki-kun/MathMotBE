package com.mot.mot.service.abstractInterface;

import com.mot.mot.model.entity.ClassPost;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClassPostService extends ICrudService<ClassPost>{
    List<ClassPost> findByClassIdPageable(Long classId, Pageable pageable);
}
