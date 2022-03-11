## 스프링 핵심 원리

스프링은 자바 언어 기반의 프레임워크로 올바른 객체지향적 애플리케이션을 개발할 수 있게 하는 프레임워크이다.

**객체지향의 특징**
+ 추상화
+ 캡슐화
+ 상속성
+ **다형성**: 역할(인터페이스)와 구현(클래스)를 구분한다는 특성. 오버라이딩과 유사함.

***

### 1. 올바른 객체지향적 설계란?

+ S(Single Responsibility): 하나의 클래스는 하나의 책임만 있다.
+ **O(OpenClose)**: 확장에는 열려있고 변경에는 닫혀있다.
+ L(Liskov Subsitution): 클래스는 인터페이스의 역할을 지키며 구현해야 한다.
+ I(Interface segregation): 하나의 인터페이스는 하나의 역할만 갖는 것이 좋다.
+ **D(Dependency inversion)**: 클라이언트는 클래스(구현)가 아닌 인터페이스(역할)에 의존한다.

클라이언트는 구현 클래스를 직접 선택하는 것이 아닌 역할 인터페이스를 선택해야 한다.
```java
  Car car1 = new Truck(); // 구현 클래스를 직접 선택했으므로 올바른 객체지향적 설계가 아니다.(OCP, DIP 위반)
```

**스프링 기술을 통해 다형성, OCP 그리고 DIP 원칙을 지킬 수 있다. 올바른 객체지향적 설계하는 것이 스프링 프레임워크를 공부하는 목표이다.**

지금부터 회원, 주문, 회원 서비스, 주문 서비스, 할인 정책, 그리고 회원 저장소 6가지 클래스가 있다고 한다.

회원(Member)와 주문(Order)는 엔티티이므로 따로 인터페이스가 필요없지만, 나머지는 필요하다.

```java
  public interface MemberService { ... }
  public interface OrderService { ... }
  public interface MemberRepository { ... }
  public interface DiscountPolicy { ..}
```

각 인터페이스는 아래의 클래스(구현체)를 갖는다.

```java
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
     ...
  }
   
  public class OrderServiceImpl implements OrderService {
     private final DiscountPolicy discountPolicy = new DiscountPolicyImpl();
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
     ...
     
  }
  
  public class MemberRepositoryImpl implements MemberRepository {
     private final HashMap<Long, Member> store = new HashMap<>();
     ...
  }
  
  public class DiscountPolicyImpl implements DiscountPolicy { ... }
```

+ **final? 변수가 단 한번만 할당 될 수 있음을 의미.**
+ **static? 변수가 컴파일 타임에 할당.**

여기서 아래의 코드를 확인해보자. 

```java
     private final DiscountPolicy discountPolicy = new DiscountPolicyImpl();
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
```

첫째, 인터페이스(역할)에 의존하는 것이 아닌 클래스(구현)에 의존하고 있다. 그러므로, DIP 원칙에 위배된다.

둘째, 구현 클래스가 바뀐다면 클라이언트 코드도 변경되어야 한다. 이는 OCP 원칙에 위배된다.

그러므로, OCP, DIP 원칙을 지키기 위한 새로운 접근 방식이 필요하다.

***

### 2. OCP, DIP 원칙을 위한 첫번째 방법: AppConfig

구현 클래스의 설정을 담당하는 새로운 클래스 AppConfig를 생성한다.

**구현을 설정(Configuration)하는 클래스를 따로 생성하여 분리한다는 점에서 의의가 있다.** 

스프링 컨테이너를 사용하는 방법은 아니다.

```java
  public class AppConfig {
    ...
    public MemberRepository memberRepository() { return new MemberRepositoryImpl(); }
    public DiscountPolicy discountPolicy() { return new DisCountPolicyImpl(); }
    public OrderService orderService() { return new OrderServiceImpl(memberRepository(), discountPolicy()); }
    public MemerService memberService() { return new MemberServiceImpl(memberRepository()); }
    ...
  }
  
  /*
    AppConfig만 수정하면 구현을 바꿀 수 있다.
  */
  
  Main() {
    AppCongfig appConfig = new AppConfig();
    MemberService memberService = appConfig.memberService();
    OrderService orderService = appConfig.orderService();
    ...
   }
```

AppConfig 클래스의 코드를 보면, 인터페이스를 구현한 클래스는 반드시 생성자가 필요하는 것을 알 수 있다.

***

+ IoC(Inversion of Control)란?

IoC는 프로그램의 제어의 흐름을 외부에서 담당하는 것을 의미한다. 

예를 들어, AppConfig을 통해 객체 내부에서 제어의 흐름을 관리하지 않게 되었다. 

이처럼, 의존관계를 외부에서 주입해 제어의 흐름을 관리하는 것을 IoC 컨테이너, 또는 DI 컨테이너라고 한다.


### 3. OCP, DIP 원칙을 위한 두번째 방법: 스프링 컨테이너 사용

**앞서 구현한 AppConfig 클래스를 스프링 컨테이너로 전환하도록 한다. 코드 몇 줄이면 가능하다.**

```java
  @Configuration
  public class AppConfig {
    ...
    @Bean
    public MemberRepository memberRepository() { return new MemberRepositoryImpl(); }
    @Bean
    public DiscountPolicy discountPolicy() { return new DisCountPolicyImpl(); }
    @Bean
    public OrderService orderService() { return new OrderServiceImpl(memberRepository(), discountPolicy()); }
    @Bean
    public MemerService memberService() { return new MemberServiceImpl(memberRepository()); }
    ...
  }
  
  /*
    Annotaion(@Configuration, @Bean)이 추가 된 것 말고는 변화한 것이 없다.
  */
  
  Main() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
    OrderService orderService = applicationContext.getBean("orderService", OrderService.class);
    ...
   }
```

스프링 컨테이너에 AppConfig를 등록한다. 왜냐하면 @Configuration이 지정되었기 때문이다.

AppConfig의 멤버함수들은 스프링 빈으로 등록된다. 마찬가지로 @Bean이 있기 때문이다.

**그렇다면, 순수 자바 클래스의 AppConfig와 스프링 컨테이너에서의 AppConfig는 무엇이 다를까?**

***













