package com.vamshi.notification.kafka.producer;

import com.vamshi.notification.config.KafkaConfig;
import com.vamshi.notification.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public void publish(Long recipientId, NotificationRequest request) {
        String key = String.valueOf(recipientId);
        CompletableFuture<SendResult<String, NotificationRequest>> future =
                kafkaTemplate.send(KafkaConfig.NOTIFICATION_TOPIC, key, request);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish notification event for recipient={}", recipientId, ex);
            } else {
                log.debug("Published notification event offset={} partition={}",
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            }
        });
    }
}
