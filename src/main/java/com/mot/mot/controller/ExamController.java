package com.mot.mot.controller;

import com.mot.mot.model.dto.CreateExamResponse;
import com.mot.mot.model.dto.ExamInfoDto;
import com.mot.mot.model.entity.Exam;
import com.mot.mot.model.entity.ExamAttempt;
import com.mot.mot.repository.StudentRepository;
import com.mot.mot.service.abstractInterface.IExamAttemptService;
import com.mot.mot.service.abstractInterface.IExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("exams")
@RequiredArgsConstructor
@Slf4j
public class ExamController {
    private final IExamService examService;
    private final StudentRepository studentRepository;
    private final IExamAttemptService examAttemptService;

    @GetMapping("/exam/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ExamInfoDto> getExam(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getFullExamInfo(id));
    }

    @GetMapping
    //hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')
    @PreAuthorize("hasRole('ROLE_ADMIN'), hasRole('ROLE_USER')")
    public ResponseEntity<Page<ExamInfoDto>> getExams(Pageable pageable) {
        Page<ExamInfoDto> examsPage = examService.findAllOrderByDateDesc(pageable);
        return ResponseEntity.ok(examsPage);
    }

    @GetMapping("student/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<ExamInfoDto>> getExamsByStudent(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(examService.findAllPageableByStudentId(id, pageable));
    }

    @GetMapping("/starting-soon")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<Exam>> getExamsStartingSoon() {
        return ResponseEntity.ok(examService.findExamsStartingSoon());
    }

    @GetMapping("student/starting-soon/{studentId}/{minutesLater}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ExamInfoDto>> getExamsStartingSoonByStudent(@PathVariable Long studentId, @PathVariable int minutesLater) {
        return ResponseEntity.ok(examService.findExamsStartingSoonByStudentId(studentId, minutesLater));
    }

    @PutMapping("/exam/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Integer> updateExam(@PathVariable Long id, @RequestBody ExamInfoDto examInfoDto) throws BadRequestException {

        return ResponseEntity.ok(examService.updateExamInfo(id, examInfoDto));
    }


    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreateExamResponse> uploadExam(@RequestParam("file") MultipartFile file) throws Exception {
        System.out.println("ExamController.uploadExam file name = "+file.getOriginalFilename());

        var rs= examService.readExam(file);
        System.out.println("RETURNED QUESTIONS: "+rs);
        return ResponseEntity.ok(rs);

    }


    @PostMapping("/student/start/{examId}/{studentId}")
    public ResponseEntity<?> startExam(@PathVariable Long examId, @PathVariable Long studentId) {
        var exam = examService.getById(examId);
        if(exam == null) {
            return ResponseEntity.badRequest().body("Exam not found");
        }

        var student = studentRepository.findById(studentId);
        if(student.isEmpty()) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        var examAttempt =
                ExamAttempt.builder().attemptDate(new Date()).exam(exam).student(student.get()).point(0f).finished(false).build();

        var ea = examAttemptService.create(examAttempt);

        return ResponseEntity.ok(ea.getId());
    }

}
