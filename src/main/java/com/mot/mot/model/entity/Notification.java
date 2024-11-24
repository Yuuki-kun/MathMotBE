package com.mot.mot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mot.mot.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String message;

    private boolean isRead;

    private Date createdDate;

    //student id, teacher id, class id
    private Long senderId;

    private String senderName;

    private Long targetId;
}
