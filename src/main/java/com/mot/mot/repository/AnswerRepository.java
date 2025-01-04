package com.mot.mot.repository;

import com.mot.mot.model.entity.Answer;
import com.mot.mot.model.entity.Question;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)  // Sử dụng Pessimistic Locking
    @Query("SELECT q FROM Answer q WHERE q.id = :id")
    Optional<Answer> findByIdForUpdate(@Param("id") Long id);
}
