package com.vamshi.notification.service;

import com.vamshi.notification.dto.NotificationRequest;
import com.vamshi.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse create(NotificationRequest request);

    NotificationResponse getById(Long id);

    Page<NotificationResponse> getByRecipient(Long recipientId, Pageable pageable);

    NotificationResponse markAsRead(Long id);

    void deleteById(Long id);

    long countPending(Long recipientId);
}
