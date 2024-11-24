package com.mot.mot.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICrudService<T> {
    T create(Object object);
    void update(T object);
    void delete(T object);
    T getById(Long id);
    T getAll();
    Page<T> findAll(Pageable pageable);
}
