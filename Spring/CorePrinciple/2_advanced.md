## 스프링 핵심 원리 - Advanced

스프링의 고급 기능인 스레드 로컬, AOP와 디자인 패턴에 대해서 학습한 내용을 정리한다.

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

만약 두 스레드가 TraceHolder 필드 변수에 동시에 접근하면, 로그 추적이 꼬이는 문제가 발생한다. 

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



public class ProxyPatterntest {
    @Test
    void test() {
        Subject cacheProxy = new CacheProxy(new RealSubject());
        ProxyPatternClient client = new ProxyPatternClient(cacheProxy);
        client.execute();
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

특징은 주요 비즈니스 로직을 담은 컨트롤러, 서비스, 리포지토리는 스프링 빈으로 관리되지 않고 프록시가 스프링 빈으로 관리된다는 점이다.

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

소스와 환경설정 코드를 확인하면, GET /v1/request HTTP 요청시 발생하는 흐름은 아래와 같다.

HTTP 요청 -> 프록시 컨트롤러(로그 추적) -> 컨트롤러 -> 프록시 서비스(로그 추적) -> 서비스 -> 프록시 리포지토리(로그 추적) -> 리포지토리

#### 5.4 로그 추적기 적용2 : 구체 클래스 기반 프록시

앞서 5.3 에서의 로그 추적기는 인터페이스의 **다형성**을 이용해서 구현했다. 

뿐만 아니라, 클래스 상속에서도 다형성을 활용해 프록시 기반 로그 추적기를 만들 수 있다. 이를 구체 클래스 기반 프록시라고 하자.

구체 클래스 기반 프록시를 로그 추적기에 적용해보도록 하자.

```java
@Slf4j
@RequestMapping
@ResponseBody
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;

    public OrderControllerV2(OrderServiceV2 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v2/request")
    public String request(@RequestParam(name="itemId") String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "ok";
    }
}

public class OrderServiceV2 {
    private final OrderRepositoryV2 orderRepository;

    public OrderServiceV2(OrderRepositoryV2 orderRepositoryV2) {
        this.orderRepository = orderRepositoryV2;
    }

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}

public class OrderRepositoryV2 {
    public void save(String itemId) {
        if(itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생!");
        }
        sleep(1000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


/* ------------------------------------------------------------------------- */


public class OrderControllerConcreteProxy extends OrderControllerV2 {
    private final OrderControllerV2 controller;
    private final LogTrace trace;

    public OrderControllerConcreteProxy(OrderControllerV2 controller, LogTrace trace) {
        super(null);
        this.controller = controller;
        this.trace = trace;
    }

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.save()");
            String request = controller.request(itemId);
            trace.end(status);
            return request;
        } catch(Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return "ok";
    }
}


public class OrderServiceConcreteProxy extends OrderServiceV2 {

    private final OrderServiceV2 service;
    private final LogTrace trace;
    public OrderServiceConcreteProxy(OrderServiceV2 service, LogTrace trace) {
        super(null);
        this.service = service;
        this.trace = trace;
    }

    @Override
    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderService.orderItem()");
            service.orderItem(itemId);
            trace.end(status);
        } catch(Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}

public class OrderRepositoryConcreteProxy extends OrderRepositoryV2 {
    private final OrderRepositoryV2 repository;
    private final LogTrace trace;

    public OrderRepositoryConcreteProxy(OrderRepositoryV2 repository, LogTrace trace) {
        this.repository = repository;
        this.trace = trace;
    }

    @Override
    public void save(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderRepository.save()");
            repository.save(itemId);
            trace.end(status);
        } catch(Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}

/* ------------------------------------------------------------------------------------------ */

@Configuration
public class ConcreteProxyConfig {
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 orderControllerV2 = new OrderControllerV2(orderServiceV2(logTrace));
        return new OrderControllerConcreteProxy(orderControllerV2, logTrace);
    }

    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 orderServiceV2 = new OrderServiceV2(orderRepositoryV2(logTrace));
        return new OrderServiceConcreteProxy(orderServiceV2, logTrace);
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 orderRepositoryV2 = new OrderRepositoryV2();
        return new OrderRepositoryConcreteProxy(orderRepositoryV2, logTrace);
    }
}
```

**정리**

인터페이스를 활용한 방법은 항상 무작정 인터페이스를 생성해야 한다는 단점이 있다.

또한, 클래스 상속을 이용한 방법은 클래스가 N개라면 프록시 클래스를 N개 생성해야 한다는 단점이 있다.

이를 개선 할 수 있는 방법이 필요하다.

예를 들어, 아래와 같이 코드를 작성 할 수 있으면 이 문제를 해결할 수 있다.

```python

로그_호출1()
비즈니스_로직() # -> 이 부분에 controller.logic(), service.logic(), repository.logic()을 추상화한다.
로그_호출2()

```
***

### 6. 동적 프록시 기술

5장의 마지막 부분에서 제시한 방법으로 문제를 해결할 수 있다. 이를 동적 프록시 기술이라고 한다.

#### 6.1 리플렉션

자바의 리플렉션부터 살펴보자. 리플렉션은 런타임에 클래스나 메소드의 정보를 바인딩하는 기술을 의미한다.

```java
public class Hello {
   public String callA() {
       log.info("callA");
       return "A";
   }

   public String callB() {
       log.info("callB");
       return "B";
   }
}
```

만약 위와 같은 클래스가 있다고 하자. 만약, Hello 클래스의 메소드를 런타임에 동적으로 실행하려면 아래와 같이 할 수 있다.

```java
    private void dynamicCall(Method method, Object target) throws Exception {
        log.info("start");
        Object result = method.invoke(target);
        log.info("end");
    }

    void reflection2() throws Exception {
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        // callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }
```

리플렉션을 통해 5장의 프록시의 한계를 극복할 수 있을 것으로 보인다.

그러나, 컴파일 타임에 에러를 찾을 수 없으므로 에러가 발생한다면 런타임에 발생하게 된다.

이는 프로그램할 때 큰 어려움을 초래할 수 있다. 

#### 6.2 JDK 동적 프록시: InvocationHandler 인터페이스

리플렉션 대신, 자바 언어에서 제공하는 JDK 동적 프록시 기술을 이용할 수 있다. 동적 프록시를 사용하면 런타임에 프록시 객체를 개발자 대신에 생성한다.

아래와 같은 인터페이스와 클래스가 있다고 하자.

```java
public interface AInterface {
    String call();
}
@Slf4j
public class AImpl implements AInterface {
    @Override
    public String call() {
        log.info("A 호출");
        return "A";
    }
}

public interface BInterface {
    String call();
}
@Slf4j
public class BImpl implements BInterface{
    @Override
    public String call() {
        log.info("B 호출");
        return "B";
    }
}
```

각 클래스의 call()을 동적 프록시를 통해 호출할 수 있도록 테스트 코드를 아래와 같이 작성할 수 있다.

```java
@Slf4j
public class InvocationHandlerImpl implements InvocationHandler {
    
    private final Object target;
    
    public InvocationHandlerImpl(Object target) {
        this.target = target;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);
        return result;
    }
}

@Slf4j
public class JDKDynamicProxyTest {

    @Test
    void testA() {
        AInterface targetA = new AImpl();
        InvocationHanlder handlerA = new InvocationHandlerImpl(targetA);
        AInterface proxyA = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), 
                                                                new Class[]{AInterface.class},
                                                                handlerA);
        proxyA.call();                                         
    }
    
    @Test
    void testB() {
        BInterface targetB = new BImpl();
        InvocationHanlder handlerB = new InvocationHandlerImpl(targetB);
        BInterface proxyB = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), 
                                                                new Class[]{BInterface.class},
                                                                handlerB);
        proxyB.call();                                         
    }
}
```

AImpl.call()이 호출되는 과정은 아래와 같다.

+ 1. 동적 프록시를 적용할 핸들러를 만든다. -> new InvocationHandlerImpl(targetA)
+ 2. 핸들러를 주입해 동적 프록시를 생성한다.
+ 3. 동적 프록시를 통해 call()을 호출한다.

#### 6.3 InvocateHandler 로그 추적기 적용

동적 프록시를 통해 5장 끝에서 제시한 해결방안을 구체화할 수 있다.

```python
로그_호출1()
비즈니스_로직() # -> 이 부분을 동적 프록시를 통해 호출하도록 만들 수 있다!
로그_호출2()

```

아래의 코드는 로그 추적기에 동적 프록시를 적용한 코드이다.

```java
public class LogTraceFilterHandler implements InvocationHandler {
    private final Object target;
    private final LogTrace trace;
    private final String[] patterns;
    
    public LogTraceFilterHandler(Object target, LogTrace trace, String... patterns) {
        this.target = target;
        this.trace = trace;
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TraceStatus status = null;
        
        String methodName = method.getName();
        if(!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            return method.invoke(target, args);
        }
        
        try {
            String message = method.getDeclaringClass().getSimpleName() +
                                    "." + method.getName() + "()";
            status = trace.begin(message);
            
            Object result = target.invoke(method, args);
            
            trace.end(status);
            return result;
            
        } catch(Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}

@Configuration
public class DynamicProxyFilterConfig {
    public static final String[] PATTERNS = {"request*", "order*", "save*"};


    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1 orderControllerV1 = new OrderControllerV1Impl(orderServiceV1(logTrace));
        OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                new Class[]{OrderControllerV1.class},
                new LogTraceFilterHandler(orderControllerV1, logTrace, PATTERNS));
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                new Class[]{OrderServiceV1.class},
                new LogTraceFilterHandler(orderService, logTrace, PATTERNS));
        return proxy;
    }
    
    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        OrderRepositoryV1 target = new OrderRepositoryV1Impl();
        InvocationHandler handler = new LogFilterHandler(target, logTrace, PATTERNS);
        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepository.class.getClassLoader(), 
                         new Class[]{OrderRepository.class},
                         handler);
        return proxy;
    }

}

```

잘 해결했다. 그러나, target은 모두 인터페이스가 존재해야 한다. 

즉, Controller, Service, 그리고 Repository 모두 인터페이스가 필요하다.

CGLIB 오픈 소스를 이용하면 인터페이스 없이 구체 클래스만을 통해서 동적 프록시를 생성할 수 있다.

```java
@Slf4j
public class CglibTest {
    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService) enhancer.create();

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
        proxy.call();
    }
}
```

InvocationHanlder와 CGLIB 모두 단점이 있다. 다음 장에서 이를 해결할 수 있는 스프링 프록시 팩토리에 대해서 알아보도록 한다.

***

### 7. 스프링에서 지원하는 프록시

인터페이스가 있는 구현체인 경우 InvocationHandler를 사용하고, 없는 경우에는 MethodInterceptor를 사용해야 했다.

이러한 복잡함을 줄이기 위해 스프링에서 Advice를 제공한다.

+ Advice : 부가기능을 추상화한 개념
+ Pointcut : 부가기능을 어떤 서비스에 적용할지 필터링 역할
+ Advisor : Advice와 Pointcut을 각 1개씩 갖는 역할

위의 개념을 활용하여 Controller - Service - Repository에 로그 추적 부가기능을 아래와 같이 적용할 수 있다.


첫번째로 부가기능을 담당할 LogTrace 클래스를 생성한다. MethodInterceptor 인터페이스를 구현해야 한다.

```java
public class LogTraceAdvice implements MethodInterceptor {
    private final LogTrace logTrace;
    public LogTraceAdvice(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TraceStatus status = null;
        try {
            Method method = invocation.getMethod();
            String message = method.getDeclaringClass().getSimpleName()+"."+method.getName()+"()";
            status = logTrace.begin(message);

            Object result = invocation.proceed(); // 비즈니스 로직 실행

            logTrace.end(status);
            return result;
        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```

두번째로 Controller - Service - Repository를 수동 빈으로 등록하되 프록시로 등록한다.

```java
@Slf4j
@Configuration
public class ProxyFactoryConfigV1 {
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1Impl orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
        ProxyFactory proxyFactory = new ProxyFactory(orderController);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        
        OrderControllerV1 proxy = (OrderControllerV1)proxyFactory.getProxy();
        log.info("ProxyFactory Proxy={}, target={}", proxy.getClass(), orderController.getClass());
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1 orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        ProxyFactory proxyFactory = new ProxyFactory(orderService);
        proxyFactory.addAdvisor(getAdvisor(logTrace));
        
        OrderServiceV1 proxy = (OrderServiceV1)proxyFactory.getProxy();
        log.info("ProxyFactory Proxy={}, target={}", proxy.getClass(), orderService.getClass());
        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
        ProxyFactory factory = new ProxyFactory(orderRepository);
        factory.addAdvisor(getAdvisor(logTrace));

        OrderRepositoryV1 proxy = (OrderRepositoryV1)factory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderRepository.getClass());
        return proxy;
    }

    private Advisor getAdvisor(LogTrace logTrace) {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice); // advisor는 pointcut, advice를 각 1개씩 갖는다.
    }
}
```

부가기능을 적용하고자 하는 클래스가 인터페이스를 갖던 말던 advice를 통해 부가기능을 적용할 수 있었다.

그러나, 부가기능을 적용하는 Bean마다 반드시 프록시를 생성해야 한다는 점과 자동 빈 등록일 경우에는 프록시를 등록할 수 없다는 단점이 있다.

이러한 문제를 빈 후처리기로 해결할 수 있다.

### 8. 빈 후처리

#### 8.1 BeanPostProcessor 인터페이스 구현을 통한 빈 후처리기 직접 생성

BeanPostProcessor 인터페이스 구현하여 클래스를 생성한 후, 이를 빈으로 등록하여 빈 후처리기로 사용할 수 있다.

빈 컨테이너에 등록한 후, 빈 후처리기가 Bean이 특정 조건을 만족한다면 빈 객체 대신에 프록시를 등록한다.

```java
Slf4j
@RequiredArgsConstructor
public class PackageLogTracePostProcessor implements BeanPostProcessor {
    private final String basePackage;
    private final Advisor advisor;
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //프록시 적용 대상 여부 체크
        //프록시 적용 대상이 아니라면 원본을 그대로 반환
        String packageName = bean.getClass().getPackageName();
        if(!packageName.startsWith(basePackage)) {
            return bean;
        }

        //프록시 대상이면 프록시를 만들어서 반환
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvisor(advisor);

        Object proxy = proxyFactory.getProxy();
        log.info("create proxy: target={}, proxy={}", bean.getClass(), proxy.getClass());
        return proxy;
    }
}

@Slf4j
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class BeanPostProcessorConfig {
    @Bean
    public PackageLogTracePostProcessor logTracePostProcessor(LogTrace logTrace) {
        return new PackageLogTracePostProcessor("hello.proxy.app", getAdvisor(logTrace));
    }

    private Advisor getAdvisor(LogTrace logTrace) {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
```

이를 통해 빈을 등록할 때, 프록시 객체를 반환하는 귀찮은 별도의 설정 로직을 제거하고, 자동 빈 등록일 때도 프록시 객체가 등록되도록 변경할 수 있었다.

#### 8.2 스프링에서 제공하는 빈 후처리기

스프링에서는 빈 후처리기를 이미 제공한다. 그러므로, BeanPostProcessor 인터페이스를 구현할 필요가 없다.

gradle 빌드 기준으로 ```implementation 'org.springframework.boot:spring-boot-starter-aop'```를 추가하여 자동 프록시 생성기를 추가할 수 있다.

자동 프록시 생성기는 아래와 같이 동작한다.

1. Advisor를 @Bean으로 등록한다. Advisor에는 advice와 pointcut이 존재한다.
2. 빈 객체가 생성될 때, 모든 advisor를 조회한다.
3. pointcut을 통해 프록시를 적용할지 결정한다.
4. 프록시를 적용해야 한다면 프록시를 생성해서 빈으로 등록한다.

**그러므로, 개발자가 할 일은 advisor를 Bean으로 등록만 하면 된다.**

또한 pointcut은 프록시가 생성될 때와 사용될 때 advice 적용 여부를 판단한다.

```java
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {
//    @Bean
    public Advisor advisor1(LogTrace logTrace) {
        // pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        // advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    @Bean
    public Advisor advisor2(LogTrace logTrace) {
        // pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");
        // advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}
```

advisor1의 경우에는 메소드에 request, order, save가 존재하는 모든 클래스를 프록시로 등록하므로 좀 더 정밀한 pointcut 조건이 필요하다.

이때 사용하는 것이 **AspectJExpressionPointcut**이다. AOP에 특화된 pointcut 표현식이다. 자세한 것은 추후 다루도록 한다.

참고로 위의 예제에서는 Bean 중복을 막기 위해서 주석처리가 필요했다.

### 9. @Aspect AOP

Advisor 빈을 코드로 생성하는 방법 대신에 @Aspect 애노테이션을 활용해 선언적 방식으로 쉽게 만들 수 있다.

```java
@Slf4j
@Aspect
@RequiredArgsConstructor
public class LogTraceAspect {
    private final LogTrace logTrace;

    @Around("execution(* hello.proxy.app..*(..))") // 포인트컷
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable { // 어드바이스
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            Object result = joinPoint.proceed(); // 비즈니스 로직 실행

            logTrace.end(status);
            return result;
        } catch(Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```

@Around를 통해 포인트컷을 생성하고, execute 함수를 통해 어드바이스를 생성한다.

해당 클래스에 @Aspect가 붙어있으면 자동 프록시 생성기는 이를 어드바이저로 등록한다.

**@Aspect가 붙은 클래스를 어드바이저로 저장하는 과정**

컨테이너에 등록된 Bean 중에서 @Aspect가 붙은 Bean을 조회하여 이를 Advisor로 등록한다.

**프록시를 생성하는 방법**

컨테이너에 등록된 Advisor와 @Aspect 기반으로 생성된 Advisor를 모두 조회한 후, 포인트컷을 통해 프록시 적용 대상인지 확인한다.

만약 프록시 적용 대상이라면, 빈 객체를 프록시 빈 객체로 바꾸어서 컨테이너에 등록한다.

### 10. 스프링 AOP 개념



