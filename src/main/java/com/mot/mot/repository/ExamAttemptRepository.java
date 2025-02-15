package com.mot.mot.repository;

import com.mot.mot.model.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    @Query("")
    List<ExamAttempt> findAllByExamIdAndUserId(Long examId, Long userId);

}
