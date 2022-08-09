## 스프링 핵심 원리 - Advanced

스프링의 고급 기능과 디자인 패턴에 대해서 학습한 내용을 정리한다.

이는 가독성이 높고 유지보수에 적합한 코드를 작성하는데 도움이 될 것으로 기대된다.

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

템플릿 메서드 패턴에서는 **변하지 않는 것(로그 추적)을 부모 클래스에 만들고, 변하는 것(비즈니스 로직)을 자식 클래스에서 구현해 부모 클래스를 상속**하도록 했다.

전략 패턴에서는 **변하지 않는 것을 Context로 만들고, Context 클래스 내부에서 변하는 것 Strategy를 위임**받도록 만든다.

이때, Strategy는 인터페이스로 만들어 비즈니스 로직이 변화함에 따라 다형성을 이용할 수 있도록 만들었다. 

아래 코드를 보면 더 명확하게 이해할 수 있다.

```java
@Slf4j
public class ContextV1 {
    private Strategy strategy;

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        strategy.call();
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}

public interface Strategy {
    void call();
}


@Slf4j
public class StrategyLogic1 implements Strategy{
    @Override
    public void call() {
        log.info("비즈니스 로직 1 실행");
    }
}
```

***

### 4. 템플릿 콜백 패턴

전략 패턴에서는 변하지 않는 부분(로그 추적)을 클래스로 만들고, 변하는 부분(비즈니스 로직)을 인터페이스로 위임받았다.

이제 인터페이스를 위임하는 것이 아니라 파라미터로 전달하는 방식으로 고쳐보도록 하자.

```java
@Slf4j
public class ContextV2 {
    public void execute(Strategy strategy) {

        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        strategy.call();
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
```

만약, 코드를 실행하게 된다면 아래와 같은 로직으로 흐르게 된다.

context.execute() => strategy.call() 

이처럼, 다른 코드를 인수로 넘기는 방식을 **콜백(CallBack)** 이라고 한다.

스프링에서 등장하는 XxxTemplate은 대부분 콜백 패턴으로 만들어졌다고 볼 수 있다.

마지막으로 로그 추적기를 아래와 같이 템플릿 콜백 패턴으로 변경할 수 있다.


```java
@RestController
public class OrderControllerV5 {

    private final OrderServiceV5 orderService;
    private final TraceTemplate template;

    @Autowired
    public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
        this.orderService = orderService;
        this.template = new TraceTemplate(trace);
    }

    @GetMapping("/v5/request")
    public String request(@RequestParam String itemId) {
        return template.execute("orderController.request()", new TraceCallBack<>() {
            @Override
            public String call() {
                orderService.orderItem(itemId);
                return "ok";
            }
        });
    }
}

@Service
public class OrderServiceV5 {
    private final OrderRepositoryV5 orderRepository;
    private final TraceTemplate template;

    @Autowired
    public OrderServiceV5(OrderRepositoryV5 orderRepository, LogTrace trace) {
        this.orderRepository = orderRepository;
        this.template = new TraceTemplate(trace);
    }

    public void orderItem(String itemId) {
        template.execute("orderService.orderItem()", () -> {
            orderRepository.save(itemId);
            return null;
        });

    }
}

@Repository
public class OrderRepositoryV5 {

    private final TraceTemplate template;

    public OrderRepositoryV5(LogTrace trace) {
        this.template = new TraceTemplate(trace);
    }

    public void save(String itemId) {
        template.execute("orderRepository.save()", () -> {
            if(itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생!");
            }
            sleep(1000);
            return null;
        });
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

***

### 5. 프록시 패턴과 데코레이션 패턴

로그 추적과 같은 부가적인 기능을 추가하고자 한다면, 템플릿 메서드 패턴에서는 결국 클라이언트 코드를 조금이라도 변경해야 하는 문제점이 있다. 

만약, 클라이언트 코드가 많다면, 부가 기능을 추가하는 것 또한 불필요한 작업이 될 수 있다. 

이를 개선하기 위해 새로운 패턴에 대해서 고민할 필요가 있다.

#### 5.1 프록시 패턴

프록시 패턴은 클라이언트가 Main Server에 **접근하는 것을 제어**하기 위해 사용되는 패턴이다.

프록시와 서버가 있고, 둘은 동일한 인터페이스를 구현하고 있다고 가정하자.

클라이언트는 인터페이스에 의존하므로, 요청 시 프록시로부터 응답을 받는지 아니면 서버로부터 응답을 받는지 구별하지 않아도 된다.

특정 조건에는 프록시로부터 응답을 받고 그렇지 않은 경우는 서버로부터 응답을 받는다. 아래 코드를 통해 확인할 수 있다.

```java
public interface Subject { /* 인터페이스 */
    String operation();
}

@Slf4j
public class CacheProxy implements Subject { /* 프록시 서버 */
    private Subject target;
    private String cacheValue;

    public CacheProxy(Subject target) {
        this.target = target;
    }
    
    @Override
    public String operation() {
        log.info("프록시 호출");
        if(cacheValue == null) {
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}

@Slf4j
public class RealSubject implements Subject { /* 메인 서버 */
    @Override
    public String operation() {
        log.info("실제 객체 호출");
        sleep(1000);
        return "data";
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ProxyPatternClient { /* 클라이언트 */
    private Subject subject;

    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    public void execute() {
        subject.operation();
    }
}

```

코드를 해석하면 프록시 서버의 target Value가 유효하거나 그렇지 않은 경우에 따라 클라이언트가 응답을 받는 곳은 다르다.

그러나, Proxy와 Main Server 모두 동일한 인터페이스를 구현하고 있으므로 클라이언트는 신경쓰지 않아도 된다. 


#### 5.2 데코레이터 패턴

데코레이터 패턴은 프록시 패턴과 유사하지만 기능적으로 다르다.

데코레이터 패턴은 접근을 제어하는 것이 아닌 **부가 기능을 제공**하기 위해 사용한다.

```java

public interface Component {
    String operation();
}

@Slf4j
public class RealComponent implements Component {
    @Override
    public String operation() {
        log.info("RealComponent 실행");
        return "data";
    }
}
 
@Slf4j
public class MessageDecorator implements Component{
    public Component component;

    public MessageDecorator(Component component) {
        this.component = component;
    }
    @Override
    public String operation() {
        log.info("MessageDecorator 실행");
        String operation = component.operation();
        String decoResult = "****" + operation + "****";
        log.info("MessageDecorator 꾸미기 적용 전 ={}, 후 {}", operation, decoResult);
        return null;
    }
}


@Slf4j
public class DecoratorPatternClient {
    private Component component;

    public DecoratorPatternClient(Component component) {
        this.component = component;
    }

    public void execute() {
        String result = component.operation();
        log.info("reuslt={}", result);
    }
}    

 ...
 @Test
 void decorator1() {
        RealComponent realComponent = new RealComponent();
        MessageDecorator messageDecorator = new MessageDecorator(realComponent);
        DecoratorPatternClient decoratorPatternClient = new DecoratorPatternClient(messageDecorator);
        decoratorPatternClient.execute();;
 }
    
...
```

Componet들이 연쇄적으로 연결된 패턴인 것을 확인할 수 있다.


#### 5.3 로그 추적기 적용

지금까지 익혔던 프록시, 데코레이터 패턴을 로그 추적기에 도입해보도록 하자.

프록시를 로그 추적기, 메인을 비즈니스 로직이라고 생각하면 수월하다. 둘은 모두 동일한 인터페이스를 구현하고 있다.

아래 코드를 확인하자.

```java
@RequiredArgsConstructor
public class OrderControllerInterfaceProxy implements OrderControllerV1 {
    private final OrderControllerV1 target;
    private final LogTrace logTrace;

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderController.request()");
            String result = target.request(itemId);
            logTrace.end(status);
            return result;

        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return target.noLog();
    }
}

@RequiredArgsConstructor
public class OrderServiceInterfaceProxy implements OrderServiceV1 {
    private final OrderServiceV1 target;
    private final LogTrace logTrace;
    @Override
    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderService.orderItem()");
            target.orderItem(itemId);
            logTrace.end(status);

        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}


@RequiredArgsConstructor
public class OrderRepositoryInterfaceProxy implements OrderRepositoryV1 {
    private final OrderRepositoryV1 target;
    private final LogTrace logTrace;
    @Override
    public void save(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderRepository.request()");
            target.save(itemId);
            logTrace.end(status);

        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```

프록시를 스프링 빈으로 등록하는 아래의 코드도 확인하자.

```java
@Configuration
public class InterfaceProxyConfig {
    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {
        OrderControllerV1Impl target = new OrderControllerV1Impl(orderService(logTrace));
        return new OrderControllerInterfaceProxy(target, logTrace);
    }

    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        OrderServiceV1Impl target = new OrderServiceV1Impl(orderRepository(logTrace));
        return new OrderServiceInterfaceProxy(target, logTrace);
    }

    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1Impl target = new OrderRepositoryV1Impl();
        return new OrderRepositoryInterfaceProxy(target, logTrace);
    }
}
```
