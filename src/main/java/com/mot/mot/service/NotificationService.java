package com.mot.mot.service;

import com.mot.mot.controller.notification.NotificationController;
import com.mot.mot.errorHandler.CustomBadRequestException;
import com.mot.mot.errorHandler.CustomNotFoundException;
import com.mot.mot.model.dto.NotificationDto;
import com.mot.mot.model.entity.Notification;
import com.mot.mot.repository.EnrollmentRepository;
import com.mot.mot.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService extends CrudServiceImpl<Notification> implements INotificationService {
    private final NotificationRepository notificationRepository;
    public NotificationService(NotificationRepository repository) {
        super(repository);
        this.notificationRepository = repository;
    }

    @Override
    public Notification create(Object object) {
        if(object instanceof Notification notification){
            return repository.save(notification);
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
                        .build()
        ).collect(Collectors.toList());
    }



}
