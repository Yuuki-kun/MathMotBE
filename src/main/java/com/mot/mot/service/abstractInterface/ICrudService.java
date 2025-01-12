package com.mot.mot.service.abstractInterface;


import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICrudService<T> {
    T create(Object object);
    void update(T object) throws BadRequestException;
    void delete(T object);
    T getById(Long id);
    T getAll();
    Page<T> findAll(Pageable pageable);
}
