package com.vamshi.notification.service;

import com.vamshi.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCacheService {

    private static final String KEY_PREFIX = "notification:";
    private static final Duration TTL = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;

    public void put(Long id, NotificationResponse response) {
        redisTemplate.opsForValue().set(KEY_PREFIX + id, response, TTL);
        log.debug("Cached notification id={}", id);
    }

    public Optional<NotificationResponse> get(Long id) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + id);
        if (value instanceof NotificationResponse response) {
            return Optional.of(response);
        }
        return Optional.empty();
    }

    public void evict(Long id) {
        redisTemplate.delete(KEY_PREFIX + id);
        log.debug("Evicted notification cache id={}", id);
    }
}
