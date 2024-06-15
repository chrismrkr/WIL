# Redis-Spring Boot 연동 예제

## 1. Redis 환경설정 파일 생성

```java
/* RedisConfiguration */
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisProperties redisProperties;
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
```

```properties
# application.properties
spring.redis.host=localhost
spring.redis.port=6379
```

## 2. Redis Repository Class

```java
@Repository
@RequiredArgsConstructor
public class SomethingRedisRepositoryImpl implements SomethingRepository {
    private final RedisTemplate redisTemplate;
    @Override
    public Optional<Something> findByKey(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        Object value = valueOperations.get(key);
        if(Objects.isNull(value)) {
            return Optional.empty();
        }

        Something result = Something.builder()
                .key(key)
                .value(value)
                .build();
        return Optional.of(result);
    }

    @Override
    public Something save(Something something) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        SomethingEntity redisEntity = SomethingEntity.from(something);
        valueOperations.set(redisEntity.getKey(), redisEntity.getValue());

        return redisEntity.to();
    }

    @Override
    public void delete(Something something) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(something.getKey());
    }
}
```
