package com.mot.mot.repository;

import com.mot.mot.model.dto.NotificationDto;
import com.mot.mot.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {



    @Query(value = "SELECT n FROM Notification n WHERE n.user.id = ?1 ORDER BY n.createdDate DESC",
            countQuery = "SELECT COUNT(1) FROM Notification n WHERE n.user.id = ?1",
            nativeQuery = false)
    Page<Notification> findAllByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);


}
