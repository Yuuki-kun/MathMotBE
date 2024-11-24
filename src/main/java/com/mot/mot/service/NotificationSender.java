////package com.mot.mot.service;
////
////import com.mot.mot.controller.notification.NotificationController;
////import com.mot.mot.model.entity.Notification;
////import com.mot.mot.repository.NotificationRepository;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////@Service
////public class NotificationSender {
////    @Autowired
////    private NotificationRepository notificationRepository;
////
////    @Autowired
////    private NotificationController notificationController; // Inject controller để gọi hàm SSE
////
////    class NotificationRequest {
////        private String message;
////        private Long teacherId;
////
////        public String getMessage() {
////            return message;
////        }
////
////        public void setMessage(String message) {
////            this.message = message;
////        }
////
////        public Long getTeacherId() {
////            return teacherId;
////        }
////
////        public void setTeacherId(Long teacherId) {
////            this.teacherId = teacherId;
////        }
////    }
////
////    public Notification create(NotificationRequest request) {
////        // Lưu thông báo
////        Notification notification = new Notification();
////        notification.setMessage(request.getMessage());
////        notification.setTeacherId(request.getTeacherId());
////        notification.setCreatedAt(LocalDateTime.now());
////
////        Notification savedNotification = notificationRepository.save(notification);
////
////        // Gửi thông báo qua SSE cho teacher
////        notificationController.sendNotification(
////                savedNotification.getTeacherId(),
////                "Bạn có thông báo mới: " + savedNotification.getMessage()
////        );
////
////        return savedNotification;
////    }
//}
