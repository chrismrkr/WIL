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
public class OAuthTokenRedisRepositoryImpl implements OAuthTokenRepository {
    private final RedisTemplate redisTemplate;
    @Override
    public Optional<OAuthToken> findByAccessToken(String accessToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String refreshToken = valueOperations.get(accessToken);
        if(Objects.isNull(refreshToken)) {
            return Optional.empty();
        }

        OAuthToken result = OAuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return Optional.of(result);
    }

    @Override
    public OAuthToken save(OAuthToken oAuthToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        OAuthTokenRedisEntity redisEntity = OAuthTokenRedisEntity.from(oAuthToken);
        valueOperations.set(redisEntity.getAccessToken(), redisEntity.getRefreshToken());

        return redisEntity.to();
    }

    @Override
    public void delete(OAuthToken oAuthToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(oAuthToken.getAccessToken());
    }
}
```
