package com.mot.mot.controller.notification;

import com.mot.mot.errorHandler.CustomNotFoundException;
import com.mot.mot.model.dto.NotificationDto;
import com.mot.mot.model.entity.Notification;
import com.mot.mot.model.request.EnrollmentRequest;
import com.mot.mot.service.IEnrollmentService;
import com.mot.mot.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final IEnrollmentService enrollmentService;
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotifications(userId, pageable));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //handle notification
    @PostMapping("/handle/{notificationId}")
    public ResponseEntity<Long> handleNotification(@PathVariable Long notificationId) throws BadRequestException {
        var notification = notificationService.getById(notificationId);
        if(notification==null) throw new CustomNotFoundException("Notification not found");

        notification.setRead(true);

        switch (notification.getNotificationType()){
            case CONFIRM_JOIN_CLASS:
                EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                        .classId(notification.getTargetId())
                        .studentId(notification.getSenderId())
                        .enrollmentDate(new Date())
                        .build();
                var enrollmentResult = enrollmentService.create(enrollmentRequest);
                notificationService.update(notification);
                return ResponseEntity.ok(enrollmentResult.getId());
            default:
                throw new BadRequestException("Invalid notification type");
        }

    }

}
