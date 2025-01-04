package com.mot.mot.repository;

import com.mot.mot.model.entity.Question;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(
            value = "SELECT * FROM question WHERE exam_id = ?3 order by id LIMIT ?2 OFFSET ?1",
//            value = "SELECT * FROM question WHERE exam_id = ?3 order by id LIMIT ?1, ?2",

            nativeQuery = true
    )

//    @Query(value = "SELECT * FROM questions WHERE exam_id = :examId ORDER BY id LIMIT :size OFFSET :offset", nativeQuery = true)

    List<Question> getQuestionsByOffsetAndSizeAndExamId(int offset, int size, Long examId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)  // Sử dụng Pessimistic Locking
    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findByIdForUpdate(@Param("id") Long id);
}
