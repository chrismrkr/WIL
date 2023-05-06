# 이펙티브 자바

Java의 심화적인 내용을 다루도록 한다.

## 1. Builder 패턴

빌더 패턴은 객체 생성 방식의 가독성을 높이기 위해 등장한 패턴이다. 예를 들어, 아래의 필드를 갖는 회원 엔티티 클래스가 존재한다고 하자.

```java
public class Member {
  private String username;
  private String password;
  private String email;
  private int age;
  private String birthday;
}
```

생성자를 통해 아래와 같이 객체를 생성할 수 있다.

```java
public class Member {
  private String username;
  private String password;
  private String email;
  private int age;
  private String birthday;
  
  public Member(String username, String password, String email, int age, String birthday) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.age = age;
    this.birthday = birthday;
  }
}

public void main() {
  Member member = new Member("user", "1234", "xxxx@...com", 30, "1998-11-11");
}
```

위 방식에는 몇가지 문제가 있다.

첫번째는 가독성이 떨어진다. 개발자가 Member 객체를 생성할 때, 항상 클래스의 생성자 함수가 어떻게 되어 있는지 확인해야 한다.

두번째는 유연성이 떨어진다. 개발자가 특정 필드만을 설정해서 객체를 생성하려고 하면, 매번 새로운 생성자 함수를 만들어야 한다.

극단적으로 필드가 총 5개이므로 기본 생성자까지 포함해 총 5 + 5*4/2*1 + 5*4*3/3*2*1 + 5*4*3*2/4*3*2*1 + 1 개의 생성자가 필요하다.

이러한 경우에 빌더 패턴이 필요하다. 아래와 같이 코드를 수정할 수 있다.

```java
public class Member {
  private String username;
  private String password;
  private String email;
  private int age;
  private String birthday;
  
  public static Builder builder() {
    return new Builder();
  }
  
  private Member(Builder builder) {
    this.username = builder.username;
    this.password = builder.password;
    this.age = builder.age;
    this.email = builder.email;
    this.birthday = builder.birthday;
  }
  
  private static class Builder {
    private String username;
    private String password;
    private String email;
    private int age;
    private String birthday;
        
    public Builder username(String username) {
      this.username = username;
      return this;
    }
    
    public Builder password(String password) {
      this.password = password;
      return this;
    }
    
    public Builder email(String email) {
      this.email = email;
      return this;
    }
    
    public Builder age(int age) {
      this.age = age;
      return this;
    }
    
    public Builder birthday(String birthday) {
      this.birthday = birthday;
      return this;
    }
    
    public Member build() {
      return new Member(this);
    }
  }
}

public void main() {
  Member member = Member.builder()
                        .username("user")
                        .password("1234")
                        .build();
}
```

코드가 길어졌다는 단점이 있다. 그러나, 객체를 생성하는 입장에서는 생성자 방식보다 더 직관적으로 사용할 수 있다.

이러한 패턴이 가능한 이유는 static inner class가 outer class의 private에 접근할 수 있는 특성이 있기 때문이다. 

**static** inner class가 아닌 inner class라면, outer class의 객체가 생성된 이후에만 내부 클래스에 접근할 수 있다.

그러나, static inner class이므로 outer class 객체가 생성되기 이전에 클래스만 로드되어도 inner class에 접근할 수 있다. 

## 2. 람다식과 함수형 인터페이스, 그리고 스트림(Stream)

### 2.1 람다식과 함수형 인터페이스

람다식은 함수형 인터페이스로 참조할 수 있다.

람다식을 다루기 위해서는 1개의 일반 메소드가 선언된 인터페이스가 필요하고, 이를 함수형 인터페이스라고 한다. override, static, default 메소드는 제외한다.

@FunctionalInterface를 붙여서 컴파일 시점에 확인할 수 있다.

예를 들어서, 아래 코드를 실행하고자 한다면,

```java
runLambda( () -> { System.out.println("hello"); } )
```

아래와 같이 정의하면 된다.
```java
public interface Lambda {
  public void run();
}

void runLambda(Lambda lambda) {
  lambda.run();
}
```

람다식은 익명클래스이고 인터페이스로 참조할 수 있다.

람다를 사용하는 것이 익명 클래스를 사용하는 것보다 간결하므로 특별한 경우(ex. 추상 메소드가 여러 개인 인터페이스)가 아닐 때는 람다를 쓰는 것을 권장한다.

람다식 사용하면서도 코드의 가독성이 떨어지면 메소드 참조(ex. Integer::parseInt)를 사용하자.

Java8 이상에서 기본적으로 제공하는 함수형 인터페이스는 아래와 같다.

+ Runnable의 void run() : 매개변수 X, 반환 X
+ Supplier\<T> T get() : 매개변수 X, 반환 O
+ Consumer\<T> void accept(T t) : 매개변수 O, 반환 X
+ Function\<T, R> R apply(T t) : 매개변수 O, 반환 O
+ Predicate\<T> boolean test(T t) : 매개변수 O, 반환 O

이외에도 여러가지가 있다. 예를 들어, long을 받아 int를 반환하는 함수형 인터페이스는 LongToIntFunction이다.

이처럼, 직관적인 이름을 가진 표준 함수형 인터페이스가 java.util.function에 많으므로 참고하도록 한다.

주의할 점은 박싱된 기본 타입(Long, Integer 등)을 함수형 인터페이스에 사용하면 성능이 많이 느려질 수 있다는 것이다.

그러나, 표준형 함수형 인터페이스를 사용할 수 있음에도 불구하고 직접 작성한 Comparator\<T>를 생각해보자.

Comparator\<T>는 ToIntBiFunction\<T, U>와 동일한 형태이다.

```java
Collections.sort(arr, new Comparator<Integer>((o1, o2) -> return o1 < o2) );
```

그럼에도 Comparator를 사용하는 이유는 이름이 용도를 잘 설명하여 가독성을 높이기 때문이다. 또한, 해당 인터페이스에서 유용한 디폴트 메소드를 제공한다.

## 2.2 스트림(Stream)

스트림(Stream)의 중간연산자에서 함수형 인터페이스를 사용한다.
  
**스트림(Stream)이란 Collection이나 배열과 같은 서로 다른 데이터 소스마다 다른 방식으로 다뤄하는 문제점을 해결하기 위해 등장했다.**

Stream을 사용하면 서로 다른 데이터 소스도 동일한 방법으로 다룰 수 있다. 이에 따라 재사용성도 높아진다.

```java
Stream<String> strStream1 = strList.stream();
Stream<String> strStream2 = Arrays.stream(strArr);

strStream1.sorted().foreach((str) -> System.out.println(str));
strStream2.sorted().foreach((str) -> System.out.println(str));
```
  
Stream의 특징은 아래와 같다.

  
+ 원본 데이터 소스를 변경하지 않음
+ 일회용 : 한번 사용하면 재활용이 불가능함
+ 스트림 연산 : DB의 SELECT 쿼리를 하는 것과 유사함
+ 중간연산 : 연산 결과가 Stream인 연산. 연속해서 사용 가능
+ 최종연산 : 연산 결과가 Stream이 아닌 연산. 연속해서 사용 불가능
+ IntStream, DoubleStream 등 기본형 스트림 존재 : Stream\<Integer>를 대체할 수 있음
  
#### 2.2.1 스트림 생성방법

자주 쓰이는 것은 아래와 같다.
  
+ Collection : Collection.stream()으로 생성
+ 배열 : Arrays.stream(배열)로 생성
+ 두 스트림 연결 : Stream.concat(stream1, stream2)으로 생성
  

#### 2.2.2 스트림의 중간연산

자주 쓰이는 것은 아래와 같다.
  
+ 스트림 자르기 : skip(long n), limit(long maxSize)
+ 요소 걸러내기 : filter(Predicate\<T> predicate), distinct()
+ 정렬          : sorted(Comparater\<T> comparater)
+ 변환          : map(Function\<T, R> mapper), 원하는 필드만 뽑아내기 위해 사용
+ 조회          : peek((obj) -> System.out.println(obj.getAttr())

mapToInt(), mapToDouble(), mapToLong()도 존재하고 각각 IntStream, DoubleStream, LongStream을 반환한다.

Stream\<T[]>를 Stream\<T>로 변환하기 위해서는 flatMap()을 사용한다.

#### 2.2.3 Optional\<T>

**Optional 객체에서도 Stream과 동일하게 중간연산을 적용할 수 있다.**

Optional\<T>는 제너릭 클래스인 T 타입 객체를 갑싸는 래퍼 클래스이다. 반환 값이 null인지 매번 if로 확인하지 않고 Optional을 통해 간단히 처리할 수 있다.

Optional.of(instance)로 생성할 수 있다.

Optional에서 객체를 가져오기 위해서는 .get(), .orElse(Supplier\<R> other) 등을 사용할 수 있다.
 
#### 2.2.4 스트림의 최종 연산

최종 연산이란 스트림을 소모해서 최종 결과를 만드는 함수이다. 중간 연산과 달리 최종 연산 후에는 스트림을 더이상 사용할 수 없다.

+ forEach(Consumer\<T> action) : 스트림을 소모하며 action을 반복적으로 수행
+ count, sum, average, min, max 통계 연산 : min, max는 Comparater\<T> 필요 


## 3. 모든 객체(Object)의 공통 메소드

### 3.1 equals 함수는 일반 규약을 지켜 재정의(override)한다.

아래 중 하나에 해당되는 경우는 equals 함수를 재정의할 필요가 없다.

+ 각 인스턴스는 항상 고유하다. 즉, 필드에 의존하지 않는다.
+ 인스턴스의 동일성을 검사할 일이 없다.
+ 상위 클래스에서 재정의한 equals 함수가 하위 클래스에도 들어맞는다.
+ 클래스가 private 또는 package-private이므로 equals를 호출할 일이 없다.

equals 함수를 재정의할 때는 아래의 규약을 지킨다.

+ 반사성 : x.equals(x)는 true를 반환
+ 대칭성 : x.equals(y) == y.equals(x)
+  : x.equals(y) == true 이고 y.equals(z) == true 이면, x.equals(z)는 true이다.
+ 일관성 : x.equals(y)는 x 또는 y가 변하지 않는 한 항상 같은 값을 반환한다.

아래의 코드를 살펴보며 equals 함수를 재정의하는 것의 까다로움을 느껴보자.

```java
public class Point {
  private final int x, y;
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Point)) return false;
    
    Point obj = (Point)obj;
    return this.x==obj.x && this.y==obj.y;
  }
}

public class ColorPoint extends Point {
  private final Color color;
  public ColorPoint(int x, int y, Color color) {
    super(x, y);
    this.color = color;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof ColorPoint)) return false;
    return super.equals(obj) && (ColorPoint obj).color == this.color;
  }
}

class UnitTest {
  @Test
  void symmetryTest() {
    Point point = new Point(1, 2);
    ColorPoint colorPoint = new ColorPoint(1, 2, COLOR_RED);
    
    Assertions.assertTrue(point.equals(colorPoint)); // pass
    Assertions.assertTrue(colorPoint.equals(point)); // fail
  }
}
```

instanceof는 해당 객체가 비교하는 클래스의 객체인지를 비교한다.

주의해야할 점은 부모 객체는 자식 클래스의 instanceof가 아니지만(false), 자식 객체는 부모 클래스의 instanceof이다.(true)

이에 따라, 대칭성 테스트(symmetryTest)를 통과할 수 없으므로 equals 함수는 수정되어야 한다.

```java
public class ColorPoint extends Point {
  private final Color color;
  public ColorPoint(int x, int y, Color color) {
    super(x, y);
    this.color = color;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Point)) return false;
    if(!(obj instanceof ColorPoint)) return super.equals(obj);
    return super.equals(obj) && (ColorPoint obj).color == this.color;
  }
}
```

위와 같이 변경하면 대칭성을 만족한다. 그러나, 이행성을 만족할 수 없다.

```java
class UnitTest {
  @Test
  void transivityTest() {
    ColorPoint p1 = new ColorPoint(1, 2, COLOR_RED);
    Point p2 = new Point(1, 2);
    ColorPoint p3 = new ColorPoint(1, 2, COLOR_BLUE);
    
    Assertions.assertTrue(p1.equals(p2)); // pass : true
    Assertions.assertTrue(p2.equals(p3)); // pass : true
    Assertions.assertTrue(p1.equals(p3)); // fail : false
  }
}
```

해법은 뭘까? 결론은, **구체 클래스를 확장해 새로운 값을 추가하면서 equals 함수 재정의 시 일반 규약을 만족시킬 방법은 없다.**

instanceof 대신에 getClass()를 사용할 수 있지만, 이는 메소드가 하위 타입에서도 잘 작동해야한다는 리스코프 치환 법칙을 위배한다.

유일한 해법은 상속 대신에 컴포지션(합성)을 사용하여 해결할 수 있다. 물론, 추상 클래스는 개별 인스턴스를 선언할 수 없으므로 논외이다.

**결론: 필요한 경우가 아닐 때는 equals를 재정의하지 않는다. 만약, equals를 재정의할 때는 일반 규약에 유념하고 테스트 코드 작성을 철저히 해야한다.**

### 3.2 equals를 재정의하려면 hashCode도 재정의하자.

equals를 재정의했지만 hashCode를 재정의하지 않는다면 객체를 Collection 원소로 사용할 때 문제가 발생한다. 

```java
Map<PhoneNumber, String> map = new LinkeedHashMap<>();
m.put(new PhoneNumber("010", "1111", "2222"), "honggildong");

Assertions.assertEquals(m.get(new PhoneNumber("010", "1111", "2222")), "honggildong"); // fail : m.get은 null을 반환함
```

위의 예제에서 m.get은 null을 반환한다. 왜냐하면, hashCode를 재정의하지 않았기 때문이다. get 메소드는 hashCode를 이용해 검색한다.

해시 값이 충돌한다면, Map은 overflow chain을 통해 이를 막을 수 있지만 성능을 고려했을 때 중복되지 않는 해시 값을 생성해야 한다.

+ 기본 타입 필드에 31을 곱하여 해시 값에 더한다.
+ 참조 타입 필드는 필드의 해시 값을 재귀적으로 계산하여 더한다.
+ 컬렉션 타입 필드는 각 원소에 31일 곱하여 더한다.

만약, 클래스가 변이고 계산 비용이 크다면, 해시 값을 캐싱하는 것도 좋은 전략이다.

또한, 해시 값을 계산할 때는 핵심 필드를 생략해서는 안된다. 

핵심 필드를 생략해서 계산하면 인스턴스의 해시 값이 몇개의 필드에만 의존하게 되고, 이는 해시 값 충돌로 이어져 성능을 악화시킬 수 있다.

### 3.3 toString을 항상 재정의한다.

기본 toString 메서드는 ```클래스이름_@해시코드```로 표현되므로 실용적이지 않다.

그러나, toString은 println, printf, 연결 연산자, assert, 또는 디버거 등에서 자동으로 사용되므로 재정의해야한다.

3가지에 유의하며 toString을 재정의하도록 한다.

+ 1. 객체의 주요 정보를 모두 반환하도록 만든다. 즉, 객체가 스스로를 완벽히 설명하는 문자열을 반환하도록 해야 한다.
+ 2. 포맷을 문서화한다면, 그것을 사용하는 개발자들은 그 포맷에 얽히게 되므로 주의해야 한다.
+ 3. toString 반환 값에 포함된 정보를 알 수 있는 API를 따로 제공하자. 그렇지 않으면, toString 반환 값을 사용하는 개발자는 항상 파싱해야 한다. 

참고로, 하위 클래스에서 공유할 추상 클래스를 생성할 때는 반드시 toString을 재정의한다.
