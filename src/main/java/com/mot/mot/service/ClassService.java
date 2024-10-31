package com.mot.mot.service;

import com.mot.mot.model.entity.Class;
import com.mot.mot.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassService extends CrudServiceImpl<Class>{

    private final ClassRepository repository;

    @Autowired
    public ClassService(ClassRepository repository) {
        super(repository);
        this.repository = repository;
    }



}
