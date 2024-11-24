package com.mot.mot.repository;


import com.mot.mot.model.entity.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClassRepository extends JpaRepository<Class, Long>{

    @Query(
            value = "SELECT c.* FROM Class c JOIN Enrollment e ON c.id = e.class_id WHERE e.student_id = ?1",
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

}
