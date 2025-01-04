package com.mot.mot.model.dto;

import com.mot.mot.model.entity.NotificationType;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;


    private String notificationType;

    private String message;

    private boolean isRead;

    private Date createdDate;

    private Long targetId;

    //student id, teacher id, class id
    private Long senderId;

    private String senderName;

    private String senderImageUrl;

    private Boolean processed;
}
