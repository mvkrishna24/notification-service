package com.vamshi.notification.websocket;

import com.vamshi.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(Long userId, NotificationResponse notification) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);
        log.debug("WebSocket notification sent to userId={}", userId);
    }

    public void broadcast(NotificationResponse notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
