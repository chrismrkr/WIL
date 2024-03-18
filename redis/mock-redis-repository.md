# How to Mock Redis Repository

```java
@Slf4j
public class MockRedisRepository implements RedisRepository {
    private final Map<String, String> datas = new HashMap<>();
    private final AtomicBoolean semaphore = new AtomicBoolean(false);

    @Override
    public Optional<Something> findByKey(String key) throws InterruptedException {
        waitingForUnlock(50);
        semaphore.set(true);
        String value = datas.get(key);
        semaphore.set(false);
        if(value == null) {
            return Optional.empty();
        }

        return Optional.of(
                Something.builder().key(key).value(value).build()
        );
    }

    @Override
    public OAuthToken save(Something something) throws InterruptedException {
        waitingForUnlock(50);
        semaphore.set(true);

        Random random = new Random();
        int waitTime = random.nextInt(100) % 30;
        Thread.sleep(waitTime);

        datas.put(something.getKey(), something.getValue());
        semaphore.set(false);
        return Something.builder().
                key(something.getKey())
                .value(something.getValue())
                .build();
    }

    @Override
    public void delete(Something something) throws InterruptedException {
        waitingForUnlock(50);
        semaphore.set(true);
        if(datas.containsKey(something.getKey())) {
            datas.remove(something.getKey());
        }
        semaphore.set(false);
    }
    private boolean isLock() {
        return semaphore.get();
    }
    private void waitingForUnlock(int millis) throws InterruptedException {
        try {
            while(isLock()) {
                log.info("waiting for lock");
                Thread.sleep(millis);
            }
        } catch(InterruptedException e) {
            throw e;
        }
    }
}
```
