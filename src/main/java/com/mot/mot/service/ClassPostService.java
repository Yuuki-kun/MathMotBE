package com.mot.mot.service;

import com.mot.mot.model.entity.ClassPost;
import com.mot.mot.repository.ClassPostRepository;
import com.mot.mot.service.abstractInterface.IClassPostService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ClassPostService extends CrudServiceImpl<ClassPost> implements IClassPostService {

    private final ClassPostRepository classPostRepository;

    @Autowired
    public ClassPostService(ClassPostRepository repository) {
        super(repository);
        this.classPostRepository = repository;
    }

    @Override
    public ClassPost create(Object object) {
        if(object instanceof ClassPost classPost){
            return repository.save(classPost);
        }
        return ClassPost.builder().id(-1L).build();
    }

    @Override
    public void update(ClassPost object) throws BadRequestException {

    }

    @Override
    public void delete(ClassPost object) {

    }

    @Override
    public List<ClassPost> findByClassIdPageable(Long classId, Pageable pageable) {

        return this.classPostRepository.findByClassIdPageable(classId, pageable);
    }
}
