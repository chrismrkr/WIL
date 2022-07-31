## 스프링 핵심 원리 - Advanced

스프링의 고급 기능과 디자인 패턴에 대해서 학습한 내용을 정리한다.

이는 유연하고 가독성이 높고, 유지보수가 쉬운 코드를 작성하는데 도움이 될 것으로 기대된다.

***

### 1. 스레드 로컬

스레드 로컬에 대해 설명하기 위해 로그 추적기를 예제로 구현하도록 한다.

#### 1.1 로그 추적기 구현

아래와 같은 MVC 패턴을 추적하도록 간단한 로그 추적기를 구현한다.

```java
[50d69f78] orderController.request()
[50d69f78] |-->orderService.orderItem()
[50d69f78] | |-->orderRepository.save()
[50d69f78] | |<--orderRepository.save() time=1015ms
[50d69f78] |<--orderService.orderItem() time=1015ms
[50d69f78] orderController.request() time=1016ms
```

현재 스레드의 Id와 호출 Level를 계속해서 추적할 수 있도록 파라미터로 넘기는 방식을 사용한다. 구현 코드는 아래와 같다.

```java

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {

    private final OrderServiceV2 orderService;
    private final HelloTraceV2 trace;

    @GetMapping("/v2/request")
    public String request(@RequestParam String itemId) {

        TraceStatus status = null;
        try {
            status = trace.begin("orderController.request()");
            orderService.orderItem(itemId, status);
            trace.end(status);
            return "ok";
        }
        catch(Exception e) {
            trace.exception(status, e);
            throw e; // 예외를 다시 던져야함, 아니면 정상흐름으로 동작해버린다.
        }

    }
}
```

하지만, 매번 파라미터(TraceId(Id, Level))를 전달해야 하므로 가독성과 유지보수성이 좋지 않다. 

물론, 이를 해결하기 위해서 LogTrace를 **싱글톤 필드변수**로 사용할 수 있다. 방법은 아래와 같다.

```java
public interface LogTrace {

    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e);
}

@Slf4j
public class FieldLogTrace implements LogTrace {
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private TraceId traceIdHolder; // traceId를 동기화, 동시성 이슈 발생(어떻게 해결할 것인가?)

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder;
        Long startTimeMs = System.currentTimeMillis();

        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        if(traceIdHolder == null) {
            traceIdHolder = new TraceId();
        }
        else {
            traceIdHolder = traceIdHolder.createNextId();
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }
    ... 
}

/*
 * 싱글톤 빈으로 수동 등록
*/
@Configuration
public class LogTraceConfig {

    @Bean
    public LogTrace logTrace() {
        //return new FieldLogTrace();
        return new ThreadLocalLogTrace();
    }
}


@RestController
@RequiredArgsConstructor
public class OrderControllerV3 {

    private final OrderServiceV3 orderService;
    private final LogTrace trace; // 수동 등록된 싱글톤 빈 DI

    @GetMapping("/v3/request")
    public String request(@RequestParam String itemId) {

        TraceStatus status = null;
        try {
            status = trace.begin("orderController.request()");
            orderService.orderItem(itemId);
            trace.end(status);
            return "ok";
        }
        catch(Exception e) {
            trace.exception(status, e);
            throw e; // 예외를 다시 던져야함, 아니면 정상흐름으로 동작해버린다.
        }

    }
}

```
