package com.mot.mot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class CrudServiceImpl<T> implements ICrudService<T> {

    protected JpaRepository<T, Long> repository;
    public CrudServiceImpl() {
    }
    public CrudServiceImpl(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

//    @Override
//    public T create(T object) {
//        return this.repository.save(object);
//    }

    @Override
    public void update(T object) {
        repository.save(object);
    }

    @Override
    public void delete(T object) {
    }

    @Override
    public T getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public T getAll() {
        return null;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

//    public abstract T create(T object);
}
