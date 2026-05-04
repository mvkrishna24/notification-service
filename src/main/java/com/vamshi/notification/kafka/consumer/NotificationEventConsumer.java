package com.vamshi.notification.kafka.consumer;

import com.vamshi.notification.config.KafkaConfig;
import com.vamshi.notification.dto.NotificationRequest;
import com.vamshi.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaConfig.NOTIFICATION_TOPIC,
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, NotificationRequest> record, Acknowledgment ack) {
        log.info("Received notification event key={} partition={} offset={}",
                record.key(), record.partition(), record.offset());
        try {
            notificationService.create(record.value());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Error processing notification event key={}", record.key(), ex);
        }
    }
}
