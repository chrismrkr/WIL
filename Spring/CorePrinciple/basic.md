## 스프링 핵심 원리

스프링은 자바 언어 기반의 프레임워크로 올바른 객체지향적 애플리케이션을 개발할 수 있게 하는 프레임워크이다.

**객체지향의 특징**
+ 추상화
+ 캡슐화
+ 상속성
+ **다형성**: 역할(인터페이스)와 구현(클래스)를 구분한다는 특성. 오버라이딩과 유사함.

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
  
  public class MemberRepositoryImpl implements <emberRepository {
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





