package com.mot.mot.service;

import com.mot.mot.model.dto.NotificationDto;
import com.mot.mot.model.entity.Notification;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface INotificationService extends ICrudService<Notification> {
    List<NotificationDto> getNotifications(Long userId, Pageable pageable);

    void sendNotificationToUser(Long userId, NotificationDto notificationDto);

    Integer handleNotification(Long notificationId) throws BadRequestException;
}



