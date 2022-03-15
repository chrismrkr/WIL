## 스프링 핵심 원리

스프링은 자바 언어 기반의 프레임워크로 올바른 객체지향적 애플리케이션을 개발할 수 있게 하는 프레임워크이다.

**객체지향의 특징**
+ 추상화
+ 캡슐화
+ 상속성
+ **다형성**: 역할(인터페이스)와 구현(클래스)를 구분한다는 특성(오버라이딩)

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
    // 의존관계 주입,  class를 지정해주지 않으면 클래스의 가장 상위 부모 클래스인 Object 객체로 반환된다.
    MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
    
    Object orderServiceBean = applicationContext.getBean("orderService");
    OrderService orderService = (orderService)(orderServiceBean);
   
  
    ...
   }
```

스프링 컨테이너로 AppConfig를 등록한다. 왜냐하면 @Configuration이 지정되었기 때문이다.

AppConfig의 멤버함수들은 스프링 빈으로 등록된다. 마찬가지로 @Bean이 있기 때문이다.

**그렇다면, 순수 자바 클래스의 AppConfig와 스프링 컨테이너에서의 AppConfig는 무엇이 다를까?**

***

### 4. 스프링 컨테이너와 스프링 빈

**스프링 컨테이너와 스프링 빈은 각각 컨테이너 상자와 화물로 비유할 수 있다.**

AppConfig를 @Configuration을 통해 컨테이너 상자로 등록하고, AppConfig 클래스 내의 의존관계 주입을 위한 멤버함수를 컨테이너 상자의 화물로 등록하는 것이다.

또한, 스프링 빈(화물)의 이름을 임의로 아래와 같이 지정할 수 있다. 스프링 빈의 이름은 중복될 수 없다.

```java
  @Bean(name="customizedName")
```

#### 4.1 스프링 빈 조회방법

+ 이름 전체 조회
```java
  applicationContext ac = new AnnocationConfigApplicationContext(AppConfig.class);
  
  String[] beanNames = ac.getBeanDefinitionNames();
  
  for(String beanName : beanNames) {
      Object bean = ac.getBean(beanName);
      sout("name: " + beanName + ", object: " + bean);
      
      //bean.getRole()을 통해 bean이 직접 등록한 것인지 원래 있던 것인지 알 수 있다.
      }
```

+ **빈 이름으로 조회**

```java
  MemberService memberService = ac.getBean("memberService", memberService.class);
  assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
```

+ **타입으로만 조회**

```java
  MemberService memberService = ac.getBean(memberService.class);
  HashMap<String, MemberService> memberSerivces = getBeansOfType(memberService.class);
```

**동일한 타입(인터페이스를 구현한 클래스)이 2개 이상이라면 에러가 발생한다.**

그러므로, 이러한 경우는 빈 이름을 지정해주는 것이 옳다.

HashMap과 getBeansOfType()을 통해 해당 타입(인터페이스)의 모든 빈을 가져올 수 있다. 상속관계 조회와 같다.



+ **구체 타입으로 조회**

```java
MemberService memberSerivce = ac.getBean(memberServiceImpl.class);
```

가능하면 사용하지 않는 것이 좋다.

#### 4.2 ApplicationContext의 상속관계

ApplicationContext 상속관계는 아래와 같다.

**AnnotationConfigApplicationContext -> ApplicationContext -> BeanFactory**

앞서 사용한 모든 applicationContext의 기능은 BeanFactory의 기능에 해당된다. 

물론, ApplicationContext는 BeanFactory 뿐만 아니라 여러 인터페이스를 상속 받는다. 아래와 같다.

+ MessageSource: 출력되는 언어를 위치 따라서 다르게 만들 수 있음.(ex. 한국->한글, 미국->영어)
+ EnvironmentCapable: 로컬, 개발, 운영 등의 컨테이너를 구별할 수 있도록 한다.
+ ApplicationEventPublisher: 이벤트 발행 및 구독 모델을 편리하게 지원.
+ ResourceLoader: 파일, 외부 등에서의 리소스를 편리하게 조회.

이러한 ApplicationContext를 상속 받은 클래스가 AnnotaionConfigApplicationContext이다. 

AnnotatinConfigApplicationContext 뿐만 아니라 XML과 같은 다양한 설정 형식을 지원한다. 

스프링 컨테이너는 Java, XML과 같은 형식에 의존하는 것이 아닌 BeanDefinition에 의존하기 때문에 가능하다.

***

### 5. 싱글톤 컨테이너

**OCP, DIP 원칙을 지키기 위한 의존관계 주입을 담당하는 DI 컨테이너에 대해서 알아보았다.**

그러나, 앞선 설계는 클라이언트가 서비스 요청 등을 통해 DI 컨테이너를 필요로 할 때 마다 새로운 컨테이너 객체가 할당되어 메모리가 낭비된다는 문제점이 있다.

이러한 메모리 낭비 문제를 해결하기 위해 등장한 개념이 싱글톤 패턴이다.

싱글톤 패턴은 인스턴스가 하나만 생성되어 클라이언트에게 이를 공유하는 개념이다.

싱글톤 패턴은 클래스 내에서 staic final 키워드를 통해 만들 수 있다.

```java
  
  public class MemberServiceImpl implements MemberService {
      public static final MemberSerivce instance = new MemberServiceImpl();
      
      private MemberServiceImpl() { ... } // 생성자 외부 호출을 막음.
      ...
      }
```

static final 키워드를 추가하면 해당 변수는 컴파일 타임에 클래스 자체에 단 한번만 할당된다. 생성자 호출을 막음으로써 싱글톤 패턴을 달성할 수 있다.


**사실, 스프링 컨테이너와 스프링 빈 항상 싱글 톤으로 관리되므로 신경쓰지 않아도 된다. 이것이 스프링 컨테이너를 사용할 때의 장점 중 하나이다.**

#### 5.1 싱글톤 컨테이너 사용 시 주의점

**싱글톤 컨테이너는 하나의 인스턴스만 생성해 여러 사용자가 공유하므로 동시성 이슈를 주의해야 한다.**

Java에서 멤버변수는 스레드간 공유되고, 지역변수는 각 스레드마다 생성된다. 즉, 멤버변수 사용을 주의해야 한다.

싱글톤 인스턴스는 항상 무상태(stateless)로 설계해야 한다.


+ **무상태(stateless)란?**

공유될 수 있는 변수를 사용하지 않은 것을 의미한다.

#### 5.2 @Configuration과 @Bean

**Configuration을 담당하는 클래스에 @Configuration, 의존 관계 주입을 위한 멤버함수에 @Bean을 붙이면 스프링 컨테이너에 등록되어 의존 관계 주입을 위해 사용할 수 있다는 것을 알았다.**

그렇다면 두 Annotation은 어떤 기능을 담당할까?

+ @Configuration

@Configuration은 AppConfig 클래스 자체 바이트 코드로 전환하여 스프링 빈으로 등록함으로써 싱글톤을 가능하게 한다.

+ @Bean

@Bean은 의존관계 주입을 위한 멤버함수를 스프링 빈으로 등록한다. @Configuration으로 등록된 클래스가 @Bean들을 싱글톤으로 관리하게 된다.

**만약 @Configuration이 없다면, 멤버함수들은 @Bean으로만 등록되어 싱글톤을 보장하지 않게 된다.**

***

### 6. 컴포넌트 스캔과 의존관계 자동 주입

#### 6.1 컴포넌트 스캔

멤버함수에 @Bean을 개발자가 직접 지정하는 것은 실수를 초래할 수 있다. @Bean을 자동으로 등록해주는 것을 **컴포넌트 스캔**이라고 한다.

컴포넌트 스캔을 위해서는 코드를 아래와 같이 변경하면 된다. 

```java 

  @Configuration
  @ComponentScan
  public class AutoAppConfig { }
  
  ...
  
  @Component
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
     ...
  }
  
  @Component
  public class OrderServiceImpl implements OrderService {
     private final DiscountPolicy discountPolicy = new DiscountPolicyImpl();
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
     ...
     
  }
  
  @Component("...사용자 지정 이름 가능")
  public class MemberRepositoryImpl implements MemberRepository {
     private final HashMap<Long, Member> store = new HashMap<>();
     ...
  }
  
```

@Component가 붙은 클래스를 자동으로 Scan해 스프링 빈으로 등록한다. 

@ComponentScan은 @Component 뿐만 아니라 @Controller, @Repository, @Configuration, @Service가 붙은 것 또한 스프링 빈으로 등록한다.

#### 6.2 의존관계 자동 주입

ComponentScan을 통해 스프링 빈으로 등록되었다고 하더라도 의존관계 주입이 필요하다. 

이를 위해 @Autowired를 사용해 스프링 빈에 등록된 것을 자동으로 생성자에 주입되도록 한다.

```java
  @Component
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
     
     @Autowired //ac.getBean(MemberRepository.class)와 같다고 볼 수 있다.
     public MemberServiceImpl(MemberRepository memberRepository) {
          this.memberRepository = memberRepository;
        }
     ...
  }
```

@Autowired는 ac.getBean(MemberRepository.class)과 같다고 볼 수 있다.

***

### 7. 의존관계 주입

**의존관계 주입시 일반적으로 생성자에 @Autowired를 사용한다.** 일반적으로 생성자와 @Autowired를 통해 의존관계를 주입하도록 하자.

+ 생성자를 통한 의존관계 주입이 좋은 이유는?

1. Setter 또는 일반 멤버함수를 통해 설정한다면, 누군가 실행 중에 변경시킬 여지가 있기 때문에 생성자 주입이 바람직하다.

2. 생성자와 final 키워드를 조합하면 불변 객체로 만들 수 있기 때문에 더 안전하다.


+ 롬복 라이브러리를 활용한 생성자 의존관계 주입 간략화
 
@RequiredArgsConstructor Annotation을 통해 @Autowired와 생성자를 없앨 수 있다.

final 키워드로 지정된 멤버변수들을 모아 자동으로 생성자를 만들어 의존관계를 주입한다. 코드는 아래와 같다.

```java
  @Component
  @RequiredArgsConstructors
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository = new MemberRepositoryImpl();
      // @Autowired와 Constructor를 생략해도 된다.
     ...
  }
```

#### 7.1 동일한 인터페이스를 상속받은 스프링 빈이 2개 이상인 경우

아래의 상황을 가정해보자. 


```java
  @ComponentScan
  @Configuration
  public class AutoAppConfig { }

  @Component
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository;
     
     @Autowired
     public MemberServiceImpl(MemberRepository memberRepository) {
         this.memberRepository = memberRepository;
         ...
         }
     ...
  }
  
  /*
   MemberRepository를 멤버변수로 갖는 MemberServiceImpl 클래스가 존재한다.
  그러나, MemberRepository 인터페이스를 상속받은 구현 클래스는 아래와 같다.
  */
  
  @Component
  public class MemoryMemberRepository implements MemberRepository { ... }
  
  @Component
  public class MySQLMemberRepository implements MemberRepository { ... }
```

AutoAppConfig가 @Component Annotation이 붙은 모든 클래스를 스프링 빈으로 등록한다.

이때, 필요한 의존관계를 주입한다. 그렇다면 MemberServiceImpl에는 어떤 MemberRepository의 구현 클래스가 주입되어야 할까? 

이런 경우 NoUniqueBeanDefinition 오류가 발생하게 된다. 해결할 수 있는 방법은 아래와 같다.

+ **멤버변수의 이름을 통한 매칭**

```java
  @Component
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memoryMemberRepository;
     
     ...
  }
```

멤버변수의 이름을 memoryMemberRepository로 변경해 어떤 구현 클래스를 주입받을 것인지 지정한다.


+ **@Qualifier를 통한 매칭**

```java
  @Component
  @Qualifier("memoryMemberRepository")
  public class MemoryMemberRepository implements MemberRepository { ... }
  
  @Component
  @Qualifier("mySQLMemberRepository")
  public class MySQLMemberRepository implements MemberRepository { ... }


  @Component
  public class MemberServiceImpl implements MemberService {
     private final MemberRepository memberRepository;
     
     @Autowired
     public MemberServiceImpl(@Qualifier("memoryMemberRepository") MemberRepository memberRepository) {
         this.memberRepository = memberRepository;
         ...
         }
     ...
  }
```

  @Qualifier를 통해 의존관계 주입을 위한 별칭을 생성한다. 
  
  물론, 빈 이름 자체를 변경하는 것은 아니다. 참조를 생성하는 것과 유사한 것으로 받아 들일 수 있다.


+ **인터페이스를 상속받은 모든 스프링 빈(클래스)이 필요하다면?

Map 자료구조를 사용한다.

```java

public class AllMemberRepository {
    private final Map<String, MemberRepository> repositoryMap;
    private final List<String> repositories;
    
    public AllMemberRepository(Map<String, MemberRepository> repositoryMap, List<String> repositories) {
        ....
        }
  }
  
  main() {
      ApplicationContext ac = new AnnocationConfigApplicationContext(AppConfig.class, AllMemberRepository.class);
    
      AllMemberRepository allMemberRepository = ac.getBean(AllMemberRepository.class);
      
      ...
      }

```

#### 7.2 자동 빈 등록 vs 수동 빈 등록

@ComponentScan, @Component, @Autowired 등의 Annotation을 통해 자동으로 스프링 빈으로 등록할 수 있었고,

@Configuration, @Bean을 통해 수동으로 스프링 빈으로 등록할 수 있었다. 무엇이 더 좋을까?

실무에서 서비스와 관련된 비즈니스 업무 로직은 자동 빈 등록으로 하고, 데이터베이스 연결과 같은 기술지원과 관련된 로직은 수동 빈 등록으로 한다고 한다.

그렇게 하기 위해서는 AppConfig 클래스를 1개가 아니라 요구에 따라 여러개를 만들어 관리하는 것이 바람직하다.

***

### 8. 빈 생명주기 확인: CallBack

스프링 빈을 애플리케이션에서 사용할 때 반드시 의존관계가 주입되어야 한다. 

또한, 빈 객체는 때로는 데이터베이스와 같은 외부 자원과 연동되어야 하는 경우도 있다.

스프링 빈과 외부 자원을 연결 및 해제하기 위해서는 빈 객체가 의존관계가 주입이 끝나는 시점과 할당 해제되는 시점을 알아야 한다.

이를 위해 등장한 것이 콜백이다.(의존관계 주입 -> 초기화 콜백 -> ... -> 종료 전 콜백 -> 할당 해제)

콜백을 사용하는 방법에 대해서 알아보도록 한다.

#### 8.1 스프링 빈에 초기화 및 소멸 메서드 지정

코드를 통해 확인하도록 한다.

```java
  @Configuration
  public class AppConfig {
    ...
    @Bean(initMethod="init", destroyMethod="close")
    public MemberRepository memberRepository() { return new MemberRepositoryImpl(); }
    
    ...
  }
    
  public class MemberRepositoryImpl implements MemberRepository {
      ...
      public void init() {
          ... // 초기화 콜백 시 동작하는 로직 
       }
      
      public void close() {
          ... // 종료 전 콜백 시 동작하는 로직
      }
     ...
  }
```
콜백 함수의 이름을 사용자가 임의로 지정할 수 있다.

또한, 스프링 빈이 스프링 코드에 의존하지 않으므로 **외부 라이브러리에도 적용할 수 있다.**


#### 8.2 @PostConstruct, @PreDestroy

스프링에서만 사용할 수 있는 애노테이션으로 컴포넌트 스캔과 호환된다.

```java
  public class MemberRepositoryImpl implements MemberRepository {
      ...
      @PostConstruct
      public void init() {
          ... // 초기화 콜백 시 동작하는 로직 
       }
      
      @PreDestroy
      public void close() {
          ... // 종료 전 콜백 시 동작하는 로직
      }
     ...
  }
 ```
 
 Annotation만 붙이면 되기 때문에 매우 간단하고 자바 기반이기 때문에 스프링 이외의 컨테이너에서도 동작한다.
 
 그러나, 외부 라이브러리에서는 동작하지 못한다. 이것이 앞서 빈 메서드 지정과의 차이점이다.
 
 그러므로, 가능하면 Annotation을 사용하되, 외부 라이브러리를 사용할 때만 빈 메소드를 사용하도록 한다.
 
 ***
 
 ### 9. 빈 스코프
 
 지금까지 스프링 빈은 모두 싱글톤이였다. 싱글톤의 특성을 가진 빈은 하나의 인스턴스를 모두 공유한다는 특성을 갖는다.
 
 그러나, 호출될 때 마다 공유 인스턴스가 아닌 새로운 객체를 반환 해야할 상황도 있다. 이를 위해서 빈 스코프를 사용한다.
 
 #### 9.1 프로토타입 스코프
 
 싱글톤 스코프의 빈과 달리 프로토타입의 스코프의 빈은 조회할 때마다 새로운 객체를 반환한다.
 
 싱글톤 스코프 빈은 프로그램이 실행되면 AppConfig를 통해 의존관계가 주입된 후, 종료될 때까지 유지된다.
 
 하지만, 프로토타입 스코프 빈은 ac.getBean()시 의존관계를 주입한 후, 클라이언트에 빈을 반환한다. 그러므로, 프로토타입 스코프 빈에 대한 권한은 클라이언트가 갖게 된다.
 
 사용하는 방법은 간단하다. @Scope("prototype") Annotation만 추가하면 된다.
 
 ```java
 @Component
 @Scope("prototype")
 public class PrototypeBean {
    ...
  }
```

하지만, 싱글톤 스코프의 빈과 프로토타입 스코프의 빈을 함께 사용해야 할 상황도 있다.

아래와 같이 싱글톤으로 관리되는 스프링 빈에 프로토 타입 스코프 빈이 존재하는 경우를 생각해보자.

```java

 @Component
 @Scope("prototype")
 public class PrototypeBean {
    int level = 0;
    public void increaseLevel() { level++; }
    public int getLevel() { return level; }
    ...
  }
  
  @Component
  public class testClass {
    
    @Autowired
    private PrototypeBean prototypeBean;
    ...
    
    public int logic() { 
        prototypeBean.increaseLevel();
        return prototypeBean.getLevel();
   }
    
   }
```

그리고, 싱글톤 빈인 PrototypeBean 인스턴스를 아래와 같이 호출한다고 가정하자.

```java
  void main() {
      ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
      testClass test1 = ac.getBean(testClass.class);
      test1.logic();
      
      testClass test2 = ac.getBean(testClass.class);
      test2.logic();
 ```
 
 만약, 사용자가 test1과 test2의 prototypeBean.level이 각 1이 되는 것을 기대했다면 그렇지 않다.
 
 결과는 test1에는 level 1, 그리고 test2에는 level 2가 존재하게 된다. 
 
 이유는, testClass 자체는 싱글톤으로 관리되기 때문에 App이 실행되는 시점에 의존관계가 주입된다.

의존관계 주입 시 PrototypeBean이 주입되기 때문에, 싱글톤 testClass 내의 PrototypeBean 인스턴스는 testClass 인스턴스가 권한을 갖게 된다.

그러므로, testClass 내의 PrototypeBean은 싱글 톤인 testClass를 통해 공유되어 문제가 발생하게 된다.

이를 해결하기 위한 방법은 아래와 같다.

첫번째 방법은 다소 


 

