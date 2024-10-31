package com.mot.mot.service;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class CrudServiceImpl<T> implements ICrudService<T> {

    protected JpaRepository<T, Long> repository;

    public CrudServiceImpl() {
    }
    public CrudServiceImpl(
            JpaRepository<T, Long> repository
    ) {
        this.repository = repository;
    }

    @Override
    public T create(T object) {
        return this.repository.save(object);
    }

    @Override
    public T update(T object) {
        return null;
    }

    @Override
    public void delete(T object) {
    }

    @Override
    public T getById(Long id) {
        return null;
    }

    @Override
    public T getAll() {
        return null;
    }


}
