package com.mot.mot.repository;

import com.mot.mot.model.dto.CorrectAnswerDto;
import com.mot.mot.model.dto.ExamInfoDto;
import com.mot.mot.model.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {


    //cau query se khong tra lai exam neu assignedClass la null khi dung JOIN, vi vay phai dung LEFT JOIN
//    @Query("SELECT new com.mot.mot.model.dto.ExamInfoDto(e.id, e.title, e.description, e.published, e.startDate, e" +
//            ".endDate, e.createdDate, e.timeLimit, e.retakeLimit, e.note, " +
//            "CASE WHEN e.assignedClass IS NULL THEN null ELSE e.assignedClass.id END, " +
//            "CASE WHEN e.assignedClass IS NULL THEN 'N/A' ELSE e.assignedClass.className END) " +
//            "FROM Exam e" +
//            "ORDER BY e.createdDate DESC")
    @Query("SELECT new com.mot.mot.model.dto.ExamInfoDto(e.id, e.title, e.description, e.published, e.startDate, e" +
            ".endDate, e.createdDate, e.timeLimit, e.retakeLimit, e.note, " +
            "CASE WHEN e.assignedClass IS NULL THEN null ELSE e.assignedClass.id END, " +
            "CASE WHEN e.assignedClass IS NULL THEN 'N/A' ELSE e.assignedClass.className END, e.examType, " +
            "e.autoCloseAfter) " +
            "FROM Exam e LEFT JOIN e.assignedClass " +
            "ORDER BY e.createdDate DESC")
    Page<ExamInfoDto> findAllByOrderByDateDesc(Pageable pageable);

    //mysql: SELECT * FROM exam WHERE start_date > now() AND start_date < DATE_ADD(now(), INTERVAL 30 MINUTE);
    @Query("SELECT e FROM Exam e WHERE e.startDate > :now AND e.startDate < :minutesLater")
    List<Exam> findExamsStartingSoon(@Param("now") LocalDateTime now,
                                     @Param("minutesLater") LocalDateTime minutesLater);



    //select exam, question, answers with short fields
    //@Query("SELECT e FROM Exam e " + "JOIN FETCH e.questions q " + "JOIN FETCH q.answers a " + "WHERE e.id = :examId")
    //=> loi multiple bags do fetch 2 list questions va answers
    //multiple bags

//    Exam findExamWithQuestionsAndAnswersById(@Param("examId") Long examId);

    @Query(
    "SELECT new com.mot.mot.model.dto.ExamInfoDto(ex.id, ex.title, ex.description, ex.published, ex.startDate, ex.endDate, ex.createdDate, " +
            "ex.timeLimit, ex.retakeLimit, ex.note, enrCl.id, enrCl.className, ex.examType, ex.autoCloseAfter) " +
            "FROM Enrollment enr JOIN enr.enrolClass enrCl JOIN enrCl.exams ex " +
            "WHERE enr.student.studentId = :studentId " +
            "ORDER BY ex.createdDate DESC"
    )
    Page<ExamInfoDto> findAllPageableByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    //k dung duoc and trong join trong jpql
    @Query(
            "SELECT new com.mot.mot.model.dto.CorrectAnswerDto(q.id, a.id, q.point) " +
                    "FROM Exam e JOIN e.questions q JOIN q.answers a " +
                    "WHERE e.id = :examId and a.correct = true"
    )
    List<CorrectAnswerDto> findCorrectAnswersByExamId(@Param("examId") Long examId);

}

