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

    private TraceId traceIdHolder; // traceId를 동기화, 동시성 이슈 발생

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

위와 같이 싱글톤 필드 변수를 활용해 로그 추적기를 구현한다면, 동시성 이슈가 발생한다.

만약 두 스레드가 필드 변수에 동시에 접근하면, 로그 추적이 꼬이는 문제가 발생한다. 

**이를 해결하기 위해 등장한 것이 스레드 로컬(ThreadLocal)이다.**


#### 1.2 스레드 로컬을 통한 로그 추적기 구현

위와 크게 달라질 것은 없다. LogTrace를 스레드 로컬로 만들어 싱글톤 빈으로 등록하면 된다.

```java
@Slf4j
public class ThreadLocalLogTrace implements LogTrace {
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>(); // 동시성 이슈 해결

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();

        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if(traceId == null) {
            traceIdHolder.set(new TraceId());
        }
        else {
            traceIdHolder.set(traceId.createNextId());
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


    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                    resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                    addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
                    e.toString());
        }

        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if(traceId.isFirstLevel()) {
            /*
             * 중요! 스레드 종료 시, 반드시 스레드 로컬 필드 변수를 제거해야 한다
             */
            traceIdHolder.remove(); // destroy
        }
        else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }
}

```

스레드 로컬로 생성한다면, 두 스레드가 동시에 접근하더라도 서로 다른 TraceHolder를 가질 수 있다.

그러나, 스레드 로컬을 사용할 때 주의할 점이 한가지 있다.

가령, WAS의 스레드 풀에 3개의 스레드(A, B, C)가 존재한다고 가정하자.

첫번째 클라이언트가 WAS에 접근해 스레드 A를 사용하게 된다. 여기서 스레드 A는 하나의 traceHolder를 갖는다.

첫번째 클라이언트가 WAS 접근을 끝내고 스레드 A의 traceHolder를 제거하지 않았다면, 스레드 A의 traceHolder는 해제되지 않고 여전히 남게 된다.

여기서 두번째 클라이언트가 WAS에 접근해 스레드 A를 사용하게 된다면, traceHolder가 해제되지 않았으므로 의도하지 않은 Action이 발생할 수 있다!

**그러므로, 스레드를 해제할 때 반드시 스레드 로컬 필드변수를 remove() 해야한다.**

***

### 2. 템플릿 메서드 패턴

구현된 로그 추적기의 Controller, Service 등을 다시 점검하자.

```java
@RestController
@RequiredArgsConstructor
public class OrderControllerV3 {

    private final OrderServiceV3 orderService;
    private final LogTrace trace;

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


@Service
@RequiredArgsConstructor
public class OrderServiceV3 {
    private final OrderRepositoryV3 orderRepository;
    private final LogTrace trace;

    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin( "orderService.orderItem()");
            orderRepository.save(itemId);
            trace.end(status);
        }
        catch(Exception e) {
            trace.exception(status, e);
            throw e; // 예외를 다시 던져야함, 아니면 정상흐름으로 동작해버린다.
        }

    }
}
```

로그를 추적하는 코드가 계속해서 반복한다는 것을 알 수 있다.

만약 로그를 추적하는 코드를 변경하라는 요구사항이 발생하면, 모든 클래스의 코드를 수정해야한다.

이는 유지보수를 매우 어렵게 만든다. 그러므로, 다른 방법이 필요하다. 여기서 등장한 것이 **템플릿 메소드 패턴**이다.

클래스에 따라 변하는 코드와 변하지 않는 코드를 구분하도록 한다.

+ 변하는 코드: 비즈니스 로직
+ 변하지 않는 코드: 로그 추적

변하지 않는 코드(로그 추적)를 템플릿으로 만들고, 변하는 코드(비즈니스 로직)는 오버라이드해서 사용하도록 한다. 

```java
public abstract class AbstractTemplate<T> {
    private final LogTrace trace;

    public AbstractTemplate(LogTrace trace) {
        this.trace = trace;
    }

    public T execute(String message) {
        TraceStatus status = null;
        try {
            status = trace.begin(message);

            // 로직 호출
            T result = call();

            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    protected abstract T call();
}


@RestController
@RequiredArgsConstructor
public class OrderControllerV4 {

    private final OrderServiceV4 orderService;
    private final LogTrace trace;

    @GetMapping("/v4/request")
    public String request(@RequestParam String itemId) {

        // 익명 내부 클래스 사용
        AbstractTemplate<String> template = new AbstractTemplate<String>(trace) {
            @Override
            protected String call() {
                orderService.orderItem(itemId);
                return "ok";
            }
        };
        
        return template.execute("orderController.request()");
    }
}
```

이제 로그 추적에 대한 요구사항이 바뀌더라도 유연하게 대응할 수 있는 코드가 되었다.

또한, 로그 추적과 비즈니스 로직을 분리했다는 점에서 객체지향의 단일책임원칙도 지킬 수 있었다.

하지만, 템플릿 메소드 패턴은 상속을 이용하므로 상속의 단점을 그대로 갖고 있다. 

(부모 클래스에 강하게 의존한다는 단점이 있다.) 

***

### 3. 전략 패턴

템플릿 메서드 패턴의 상속의 단점을 피하기 위해 위임을 사용하도록 한다. 이것을 전략 패턴이라고 한다.
