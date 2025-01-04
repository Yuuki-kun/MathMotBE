package com.mot.mot.service;

import com.mot.mot.controller.notification.NotificationController;
import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.errorHandler.CustomNotFoundException;
import com.mot.mot.model.dto.NotificationDto;
import com.mot.mot.model.entity.Enrollment;
import com.mot.mot.model.entity.Notification;
import com.mot.mot.model.enums.EnrollmentStatus;
import com.mot.mot.repository.EnrollmentRepository;
import com.mot.mot.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService extends CrudServiceImpl<Notification> implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EnrollmentRepository enrollmentRepository;
    public NotificationService(NotificationRepository repository,
                               SimpMessagingTemplate simpMessagingTemplate,
                               EnrollmentRepository enrollmentRepository
        ) {
        super(repository);
        this.notificationRepository = repository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Notification create(Object object) {
        if(object instanceof Notification notification){

            var savedNotification = repository.save(notification);
            sendNotificationToUser(notification.getUser().getId(), NotificationDto.builder()
                    .id(savedNotification.getId())
                    .message(savedNotification.getMessage())
                    .isRead(savedNotification.isRead())
                    .createdDate(savedNotification.getCreatedDate())
                    .notificationType(savedNotification.getNotificationType().name())
                    .senderId(savedNotification.getSenderId())
                    .senderName(savedNotification.getSenderName())
                    .build());
            return savedNotification;
        }else{
            throw new CustomBadRequestException("Invalid object type for create method");
        }
    }

    @Override
    public List<NotificationDto> getNotifications(Long userId, Pageable pageable){
        return notificationRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable).stream().map(
                notification -> NotificationDto.builder()
                        .id(notification.getId())
                        .message(notification.getMessage())
                        .isRead(notification.isRead())
                        .createdDate(notification.getCreatedDate())
                        .notificationType(notification.getNotificationType().name())
                        .senderId(notification.getSenderId())
                        .senderName(notification.getSenderName())
                        .processed(notification.getProcessed())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public void sendNotificationToUser(Long userId, NotificationDto notificationDto) {
        log.info("Sending notification to user: {} with payload {}", userId, notificationDto);
        simpMessagingTemplate.convertAndSendToUser(userId.toString(),"/notifications", notificationDto);
    }

    @Override
    @Transactional
    public Integer handleNotification(Long notificationId) throws BadRequestException {
        var notification = getById(notificationId);

        if(notification==null) throw new CustomNotFoundException("Notification not found");

        switch (notification.getNotificationType()){
            case CONFIRM_JOIN_CLASS:
                Enrollment enrollment = enrollmentRepository.findByStudentIdAndClassId(notification.getSenderId(),
                        notification.getTargetId()).orElseThrow(()-> new CustomBadRequestException("Enrollment not found"));

                if(enrollment.getStatus() != null && enrollment.getStatus() != EnrollmentStatus.ENROLLED){
                    enrollment.setStatus(EnrollmentStatus.ENROLLED);
                }
                enrollmentRepository.save(enrollment);
                notification.setRead(true);
                notification.setProcessed(true);
                update(notification);
                return 200;
            default:
                throw new BadRequestException("Invalid notification type");
        }

    }


}
