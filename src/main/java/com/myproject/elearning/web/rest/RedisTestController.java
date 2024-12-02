package com.myproject.elearning.web.rest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/redis-test")
@RestController
public class RedisTestController {

    static final Logger logger = LoggerFactory.getLogger(RedisTestController.class);

    RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/set")
    public ResponseEntity<Object> setValue(@RequestParam String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value);
        return ResponseEntity.ok("Giá trị đã được lưu vào Redis");
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getValue(@RequestParam String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return ResponseEntity.ok(value);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteValue(@RequestParam String key) {
        redisTemplate.delete(key);
        return ResponseEntity.ok(key + " is gone");
    }

    @PostMapping("/set-list")
    public ResponseEntity<Object> setList(@RequestParam String key, @RequestBody List<String> values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values.toArray());
            logger.info("Đã lưu list vào key: {}, số phần tử: {}", key, values.size());
            return ResponseEntity.ok("Đã lưu list thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi lưu list: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi lưu list");
        }
    }

    @GetMapping("/get-list")
    public ResponseEntity<Object> getList(@RequestParam String key) {
        try {
            List<Object> values = redisTemplate.opsForList().range(key, 0, -1);
            if (values == null || values.isEmpty()) {
                logger.warn("Không tìm thấy list tại key: {}", key);
                return ResponseEntity.notFound().build();
            }
            logger.info("Đã lấy list từ key: {}, số phần tử: {}", key, values.size());
            return ResponseEntity.ok(values);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy list: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi lấy list");
        }
    }

    @PostMapping("/set-hash")
    public ResponseEntity<Object> setHash(@RequestParam String key, @RequestBody Map<String, String> values) {
        try {
            redisTemplate.opsForHash().putAll(key, values);
            logger.info("Đã lưu hash vào key: {}, số field: {}", key, values.size());
            return ResponseEntity.ok("Đã lưu hash thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi lưu hash: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi lưu hash");
        }
    }

    @GetMapping("/get-hash")
    public ResponseEntity<Object> getHash(@RequestParam String key) {
        try {
            Map<Object, Object> values = redisTemplate.opsForHash().entries(key);
            if (values.isEmpty()) {
                logger.warn("Không tìm thấy hash tại key: {}", key);
                return ResponseEntity.notFound().build();
            }
            logger.info("Đã lấy hash từ key: {}, số field: {}", key, values.size());
            return ResponseEntity.ok(values);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy hash: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi lấy hash");
        }
    }

    @PostMapping("/set-expire")
    public ResponseEntity<Object> setExpire(@RequestParam String key, @RequestParam long seconds) {
        try {
            Boolean result = redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(result)) {
                logger.info("Đã set expire cho key: {} trong {} giây", key, seconds);
                return ResponseEntity.ok("Đã set expire thành công");
            }
            logger.warn("Không tìm thấy key để set expire: {}", key);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Lỗi khi set expire: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi set expire");
        }
    }

    @GetMapping("/keys")
    public ResponseEntity<Object> getKeys(@RequestParam(defaultValue = "*") String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            logger.info(
                    "Tìm thấy {} keys với pattern: {}",
                    Objects.requireNonNull(keys).size(),
                    pattern);
            return ResponseEntity.ok(keys);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách keys: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách keys");
        }
    }

    @PostMapping("/publish")
    public ResponseEntity<Object> publish(@RequestParam String channel, @RequestParam String message) {
        try {
            redisTemplate.convertAndSend(channel, message);
            logger.info("Đã publish message tới channel: {}", channel);
            return ResponseEntity.ok("Đã publish message thành công");
        } catch (Exception e) {
            logger.error("Lỗi khi publish message: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi publish message");
        }
    }
}
