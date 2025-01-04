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
//    @Query với nativeQuery = true:
//    Đây là một Native SQL Query, nghĩa là đang viết trực tiếp câu lệnh SQL dựa trên cấu trúc cơ sở dữ liệu.
//    Phù hợp khi cấu trúc bảng và cột trong cơ sở dữ liệu không khớp 1:1 với cấu trúc entity trong JPA.
//    countQuery:
//    Bắt buộc khi sử dụng phân trang (Pageable), để JPA biết cách tính tổng số bản ghi (cho việc chia trang).
//    Page<Class>:
//    JPA sẽ tự động áp dụng LIMIT và OFFSET dựa trên đối tượng Pageable được truyền vào.
    Page<Class> findAllByStudentId(Long studentId, Pageable pageable);

//    @Query(
//            value = "SELECT c FROM Class c WHERE c.className LIKE %?1%"
//    )
//    @Query(
//            "SELECT new com.mot.mot.model.dto.ClassSearchDto(c.id, c.className, u.fullName, c.classStatus) " +
//                    "FROM Class c JOIN User u ON c.teacher.teacherId = u.teacher.teacherId " +
//                    "WHERE c.className LIKE %?1%"
//    )
    //=> k the join truc tiep 2 table ko duoc dinh nghia quan he, co the dung cau lenh tren bang nativeQuery
    @Query(
            "SELECT new com.mot.mot.model.dto.ClassSearchDto(c.id, c.className, u.fullName, c.classStatus, e.status) " +
                    "FROM Class c JOIN c.teacher t JOIN t.user u LEFT JOIN Enrollment e ON e.enrolClass = c " +
                    "AND e.student.studentId = ?2 "+
                    "WHERE c.className LIKE %?1% escape '!'"
    )
    List<ClassSearchDto> findAllByClassName(String className, Long studentId);

    @Query(
            "SELECT new com.mot.mot.model.dto.ClassDto(c.id, c.className, c.classGrade, c.classStatus, c.classDesc, c.createdAt) " +
                    "FROM Class c JOIN c.teacher t " +
                    "WHERE t.teacherId = ?1"
    )
    Page<ClassDto> findAllByTeacherId(Long teacherId, Pageable pageable);



}
