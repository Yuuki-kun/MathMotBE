package com.mot.mot.repository;

import com.mot.mot.model.entity.ClassPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassPostRepository extends JpaRepository<ClassPost, Long> {

    @Query("SELECT cp FROM ClassPost cp WHERE cp.parentClass.id = :classId")
    List<ClassPost> findByClassIdPageable(Long classId, Pageable pageable);
}
