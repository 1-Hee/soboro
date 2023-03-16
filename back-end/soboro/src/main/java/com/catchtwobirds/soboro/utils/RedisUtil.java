package com.catchtwobirds.soboro.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 이 코드는 Redis 데이터베이스를 사용하여 데이터를 읽고 쓰는 RedisUtil 클래스를 정의하는 Java 코드입니다. <br>
 * RedisUtil 클래스는 StringRedisTemplate과 RedisTemplate<String, Object> 필드를 가지고 있습니다. <br>
 * getData 메서드는 StringRedisTemplate의 opsForValue() 메서드를 사용하여 Redis에서 특정 키에 대한 값을 읽어옵니다. <br>
 * setData 메서드는 StringRedisTemplate의 opsForValue() 메서드를 사용하여 Redis에 특정 키에 대한 값을 설정합니다. <br>
 * setDataExpire 메서드는 StringRedisTemplate의 opsForValue() 메서드를 사용하여 Redis에 특정 키에 대한 값을 설정하고, 지정된 시간 후에 만료되도록 설정합니다. <br>
 * delData 메서드는 RedisTemplate의 delete() 메서드를 사용하여 Redis에서 특정 키와 연결된 데이터를 삭제합니다. <br>
 */

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public String getData(String key){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key,String value){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key,value);
    }

    public void setDataExpire(String key,String value,long duration){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key,value,expireDuration);
    }

    public void delData(String key){
        redisTemplate.delete(key);
    }
}
