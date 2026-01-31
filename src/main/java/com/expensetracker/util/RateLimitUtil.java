package com.expensetracker.util;

import com.expensetracker.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitUtil {
    
    @Value("${app.rate-limit.capacity}")
    private int capacity;
    
    @Value("${app.rate-limit.refill-tokens}")
    private int refillTokens;
    
    @Value("${app.rate-limit.refill-duration}")
    private int refillDuration;
    
    @Value("${app.rate-limit.enabled}")
    private boolean enabled;
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }
    
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
                capacity,
                Refill.intervally(refillTokens, Duration.ofSeconds(refillDuration))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    public void checkRateLimit(String key) {
        if (!enabled) {
            return;
        }
        
        Bucket bucket = resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for key: {}", key);
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }
    }
}
