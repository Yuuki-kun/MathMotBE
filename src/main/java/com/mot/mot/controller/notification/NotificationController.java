package com.mot.mot.controller.notification;

import com.mot.mot.model.dto.NotificationDto;
import com.mot.mot.model.entity.Notification;
import com.mot.mot.model.entity.NotificationType;
import com.mot.mot.service.abstractInterface.IEnrollmentService;
import com.mot.mot.service.abstractInterface.INotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) throws BadRequestException {
        Notification notification = notificationService.getById(notificationId);
        notification.setRead(true);
        if(notification.getNotificationType().equals(NotificationType.INFORMATION)){
            notification.setProcessed(true);
        }
        notificationService.update(notification);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //handle notification
    @PostMapping("/handle/{notificationId}")
    public ResponseEntity<Integer> handleNotification(@PathVariable Long notificationId) throws BadRequestException {
        return ResponseEntity.ok(notificationService.handleNotification(notificationId));
    }

}
