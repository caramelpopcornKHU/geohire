package com.geohire.service;

import com.geohire.dto.NotificationResponse;
import com.geohire.entity.Notification;
import com.geohire.entity.User;
import com.geohire.repository.NotificationRepository;
import com.geohire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /** 알림 생성 (내부 호출용) */
    @Transactional
    public void create(Long userId, String title, String message, String type, Long referenceId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        if (!n.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        n.setRead(true);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged());
        notifications.forEach(n -> n.setRead(true));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
