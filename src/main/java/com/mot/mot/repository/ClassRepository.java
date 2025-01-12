package com.mot.mot.repository;


import com.mot.mot.model.dto.ClassDto;
import com.mot.mot.model.dto.ClassSearchDto;
import com.mot.mot.model.entity.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long>{

    //find enrolled class by student id
    @Query(
            value = "SELECT c.* FROM Class c JOIN Enrollment e ON c.id = e.class_id WHERE e.student_id = ?1 AND e.status = 'ENROLLED'",
            countQuery = "SELECT COUNT(1) FROM Class c JOIN Enrollment e ON c.id = e.class_id WHERE e.student_id = ?1",
            nativeQuery = true
    )
    Page<Class> findAllByStudentId(Long studentId, Pageable pageable);


    @Query(
            "SELECT new com.mot.mot.model.dto.ClassSearchDto(c.id, c.className, u.fullName, c.classStatus, e.status) " +
                    "FROM Class c JOIN c.teacher t JOIN t.user u LEFT JOIN Enrollment e ON e.enrolClass = c " +
                    "AND e.student.studentId = ?2 "+
                    "WHERE c.className LIKE %?1% escape '!'"
    )
    List<ClassSearchDto> findAllByClassName(String className, Long studentId);

    @Query(
            "SELECT new com.mot.mot.model.dto.ClassDto(c.id, c.className, c.classGrade, c.classStatus, c.classDesc, " +
                    "c.createdAt, eimg.url) " +
                    "FROM Class c JOIN c.teacher t " +
                    "LEFT JOIN EmbedImage eimg on eimg.relatedId = c.id AND eimg.relatedTable = 'class' "+
                    "WHERE t.teacherId = ?1"
    )
    Page<ClassDto> findAllByTeacherId(Long teacherId, Pageable pageable);

    @Query(

                "SELECT new com.mot.mot.model.dto.ClassDto(c.id, c.className, c.classGrade, c.classStatus, c.classDesc, " +
                        "c.createdAt, eimg.url, u.fullName) " +
                        "FROM Class c JOIN c.teacher t LEFT JOIN User u on t.user.id = u.id " +
                        "LEFT JOIN EmbedImage eimg on eimg.relatedId = c.id AND eimg.relatedTable = 'class' "+
                        "WHERE c.id = ?1"
    )
    ClassDto findClassById(Long classId);

}
