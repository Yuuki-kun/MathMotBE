package com.mot.mot.service;


public interface ICrudService<T> {
    T create(Object object);
    T update(T object);
    void delete(T object);
    T getById(Long id);
    T getAll();
}
