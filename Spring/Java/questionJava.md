
+ 클래스 내부에 inner 클래스 정의 시 static 클래스로 정의하는 것이 좋다. 이유는?

-> inner 클래스는 외부 클래스에 대한 숨은 참조를 갖고 있다. 가비지 컬렉터가 inner 클래스를 수거할 때, 외부 클래스를 수거하지 못하여 메모리 누수가 발생할 수 있다.

+ final 예약어란?

-> 불변 상태로 만드는 예약어이다. 변수의 경우 재할당될 수 없고 클래스의 경우 상속될 수 없다.

+ 인터페이스와 추상 클래스를 사용하는 이유는? 

-> 인터페이스는 확장을 위해 사용되고, 추상 클래스는 여러 클래스의 공통 기능을 추상화한다.

+ 인터페이스의 디폴트 메소드란 무엇인가?

-> 인터페이스에는 추상 메소드만 정의할 수 있었지만 Java8부터 static, default 메소드를 정의할 수 있게 되었다.

default 메소드는 인터페이스 메소드의 기본 구현을 제공한다. 

+ java가 컴파일되는 과정은? 인터프리터와 컴파일러의 차이점은?

-> .java 파일을 컴파일러를 통해 .class로 컴파일한다. 그리고 .class 파일을 JVM 메모리에 로딩한다.

마지막으로 각 운영체제에 맞는 바이트 코드로 인터프리팅한다.

+ 동기처리 비동기처리 / blocking과 non-blocking

-> 동기와 비동기는 작업을 순서대로 처리할 것인지에 대한 개념이고, blocking과 non-blocking은 현재 task 진행이 다른 task 진행을 막을 것인지에 대한 개념이다.

+ String, StringBuffer, StringBuilder의 차이점은? Thread safe란?

-> thread-safe는 멀티 스레드 환경에서 동기화 작업이 가능하다는 것을 의미한다.

String과 StringBuffer는 thread-safe하다. StringBuilder는 그렇지 않다.

String은 불변 객체이고, StringBuffer와 StringBuilder는 가변객체이다. 그러므로, 수정이 필요할 때는 String < StringBuffer < StringBuilder 순으로 성능이 좋다.

StringBuffer는 메소드에 synchronized 키워드를 통해 critical-region을 설정한다.

+ java의 접근 제어자의 종류와 특징은?

-> private, public, protected, default가 존재한다.

public은 접근 제한이 없다. protected는 상속을 통해 자손 클래스에서 접근할 수 있다.

default는 동일한 패키지 내에서만 접근할 수 있다. private은 동일 클래스 내에서만 접근할 수 있다.

+ OOP의 4가지 특성은?

-> 추상화, 캡슐화, 상속성, 다형성


+ OOP의 5대 원칙은?

-> 단일 책임의 원칙

확장에는 열려있고 수정에는 닫혀있다. 

상위 클래스에서 동작하는 것은 하위 클래스에서도 동작해야한다. 

인터페이스 메소드는 최소한으로 한다.

구체 클래스보다 인터페이스와 같은 추상적인 것에 의존해야 한다.


+ JVM의 구조에 대해서 구체적으로 설명하시오

-> .class로 컴파일된 파일을 클래스 로더를 통해 Runtime Data Area에 로딩한다. JVM에는 실행엔진(Execution Engine)과  런타임 데이터 영역이 존재한다.

**실행엔진**

1. 인터프리터 : 바이트 코드를 한 줄씩 읽어서 실행시킨다. 여러번 호출되어도 매번 해석하여 실행하므로 속도가 느리다.
2. JIT 컴파일러 : 반복되는 코드를 컴파일하여 캐싱한다. 인터프리팅 방식을 보완한다.
3. 가비지컬렉터 : JVM은 Heap 영역을 자동으로 관리한다. 

**런타임 데이터 영역**
1. 메소드 영역: 클래스, static 변수, constant pool을 관리
2. Heap 영역 : 런타임에 할당되는 변수를 관리
3. Stack 영역 : 스레드마다 할당되는 공간
4. PC : 현재 실행 중인 명령어 흐름을 저장하는 메모리
5. Native Method Stack Area : 자바 외 언어로 된 코드를 관리하는 메모리


+ CheckedException과 UncheckedException 차이점은?

+ HashMap, HashTable, ConcurrentHashMap의 차이는?
+ 리플렉션 / 자바 다이나믹 프록시에 대해서 설명하시오.
+ 가비지컬렉터의 동작방식에 대해서 설명하시오. GC 알고리즘은?
+ 제네릭이란 무엇이고 사용하는 이유를 설명하세요.

-> 임의의 타입 T를 정의하고, 컴파일 타임에 이것이 결정되도록 한다. 타입체크와 형 변환이 생략되므로 코드가 간결해지고 추상성을 높였다.

제네렉이 존재하는 메소드는 오버로딩이 불가능하다. 이를 해결하기 위해 extends와 super 키워드를 제공한다.

```? extends``` 키워드를 통해 특정 클래스의 자손 클래스까지 제네릭(T)로 설정할 수 있게 한다.

반대로  ```? super``` 키워드를 통해 특정 클래스의 조상 클래스까지 제네릭(T)로 사용할 수 있게 하였다.

대표적으로, Collections.sort()와 Comparator<? super T>에서 사용된다.

객체 뿐만 아니라 메서드에서도 제네릭을 사용할 수 있고 이를 제네릭 메소드라고 한다.

```java
static <T> void sort(List<T> list, Comparator<? super T> c); 
```

static에는 제네릭 타입을 사용할 수 없지만(와일드카드 ?만 사용 가능), 위와 같은 제너릭 메소드에서는 사용이 가능하다.



