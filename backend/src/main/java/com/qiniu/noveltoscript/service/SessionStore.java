package com.qiniu.noveltoscript.service;

import com.qiniu.noveltoscript.model.NovelSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class SessionStore {

    private static final String KEY_PREFIX = "novel:session:";
    private static final Duration TTL = Duration.ofHours(6);

    private final RedisTemplate<String, Object> redisTemplate;

    public SessionStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(NovelSession session) {
        redisTemplate.opsForValue().set(key(session.sessionId()), session, TTL);
    }

    public Optional<NovelSession> load(String sessionId) {
        Object raw = redisTemplate.opsForValue().get(key(sessionId));
        return Optional.ofNullable((NovelSession) raw);
    }

    public void delete(String sessionId) {
        redisTemplate.delete(key(sessionId));
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}
