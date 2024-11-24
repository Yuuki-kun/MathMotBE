package com.mot.mot.controller.student;

import com.mot.mot.controller.notification.NotificationController;
import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.model.User;
import com.mot.mot.model.entity.Notification;
import com.mot.mot.model.entity.NotificationType;
import com.mot.mot.model.request.EnrollmentRequest;
import com.mot.mot.repository.StudentRepository;
import com.mot.mot.service.IClassService;
import com.mot.mot.service.IEnrollmentService;
import com.mot.mot.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("student/enrollment")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {
    private final IClassService classService;
    private final IEnrollmentService enrollmentService;
    private final INotificationService notificationService;

    private final StudentRepository studentRepository;

    @GetMapping("/classes/{studentId}")
    ResponseEntity<?> findByPageEnr(@PathVariable Long studentId, Pageable pageable) {
        return ResponseEntity.ok(classService.findAllByStudentId(studentId,pageable));
    }

    @PostMapping("/enroll")
    ResponseEntity<?> findByPageEnr(@RequestBody EnrollmentRequest enrollmentRequest) {
        System.out.println(enrollmentRequest.getClassId());
        System.out.println(enrollmentRequest.getStudentId());
        System.out.println(enrollmentRequest.isDirect());
        if(enrollmentRequest.isDirect()){
            var savedEnrollmentId = enrollmentService.create(enrollmentRequest).getId();
            if(savedEnrollmentId == null || savedEnrollmentId < 0){
                throw new RuntimeException("Enrollment failed");
            }
            return ResponseEntity.ok(savedEnrollmentId);
        }else {
            var joinClass = classService.getById(enrollmentRequest.getClassId());
            if(joinClass==null){
                throw new CustomBadRequestException("Class not found");
            }
            var classTeacher = joinClass.getTeacher();

            var student = studentRepository.findById(enrollmentRequest.getStudentId());
            if(student.isEmpty()){
                throw new CustomBadRequestException("Student not found");
            }

            var newEnrollmentNotificationRequest = Notification.builder().notificationType(NotificationType.CONFIRM_JOIN_CLASS)
                    .user(User.builder().id(classTeacher.getUser().getId()).build())
                    .senderId(enrollmentRequest.getStudentId())
                    .senderName(student.get().getUser().getFullName())
                    .targetId(enrollmentRequest.getClassId())
                    .isRead(false)
                    .message("Yêu cầu tham gia lớp học.")
                    .createdDate(new Date())
                    .build();
            var savedNotification = notificationService.create(newEnrollmentNotificationRequest);
            if(savedNotification.getId() == null || savedNotification.getId() < 0){
                throw new CustomBadRequestException("Notification failed");
            }

            return ResponseEntity.ok(savedNotification.getId());
        }
    }
}
