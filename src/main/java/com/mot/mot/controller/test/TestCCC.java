package com.mot.mot.controller.test;

import com.mot.mot.model.entity.Class;
import com.mot.mot.model.request.EnrollmentRequest;
import com.mot.mot.repository.ClassRepository;
import com.mot.mot.repository.ExamRepository;
import com.mot.mot.repository.NotificationRepository;
import com.mot.mot.service.ClassService;
import com.mot.mot.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class TestCCC {

    private final ClassService classService;
    private final EnrollmentService enrollmentService;
    private final ClassRepository c;
    private final NotificationRepository notificationRepository;
    private final ExamRepository examRepository;
    @GetMapping("/test-api")
    public String testApi(){
        return "Test API";
    }

    @GetMapping("/test-api-find-all")
    ResponseEntity<Page<Class>> findByPage(Pageable pageable) {
        return ResponseEntity.ok(classService.findAll(pageable));
    }

    @PostMapping("/test-api-enr")
    ResponseEntity<?> findByPageEnr(@RequestBody EnrollmentRequest enrollmentRequest) {
        return ResponseEntity.ok(enrollmentService.create(enrollmentRequest).getId());
    }

    @GetMapping("/test-api-find-all-enr/{studentId}")
    ResponseEntity<?> findByPageEnr(@PathVariable Long studentId, Pageable pageable) {
        return ResponseEntity.ok(classService.findAllByStudentId(studentId,pageable));
    }

    @GetMapping("/test-api-find-noti/{userId}")
    ResponseEntity<?> findByPageNoti(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(notificationRepository.findAllByUserIdOrderByCreatedDateDesc(userId,pageable));
    }

    @GetMapping("/test-api-find-classs")
    ResponseEntity<?> findByPageClass(Pageable pageable) {
        return ResponseEntity.ok(examRepository.findAllPageableByStudentId(1L, pageable));
    }


}
