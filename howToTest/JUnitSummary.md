# JUnit

JUnit5는 자바 개발자들이 단위테스트를 작성할 때 사용하는 테스트 프레임워크로, Java8 이상에서 동작하고 스프링 부트 2.2+ 버전에서 기본적으로 의존성이 추가되어 있다.

스프링 부트를 사용하지 않는다면 의존성을 추가하여 사용할 수 있다.

## 1. 기본 애노테이션

```java
@Test : 메소드에 설정하며 테스트 단위를 지정
@BeforeAll, @AfterAll : 클래스의 모든 테스트가 실행되기 전, 완료된 후에 진행할 테스트를 설정한다. 기본적으로 static 선언이 필요하다.
@BeforeEach, @AfterEach : 클래스의 각 테스트가 실행되기 전, 완료된 후에 진행할 테스트를 설정한다.
@Disabled : 실행하지 않을 테스트에 지정한다.
```

## 2. 테스트에 이름 표시하는 방법
```java
@DisplayNameGeneration : 테스트의 이름을 다른 것으로 레퍼런스하기 위해 사용한다.
@DisplayName : 메소드에 지정하여 테스트의 이름을 변경한다.
```

예를 들어 아래와 같이 사용할 수 있다. _를 공백으로 변경한다.
```java
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {
    @Test
    void create1_new_study() { ... }
}
```

## 3. Assertions
org.junit.jupiter.api.Assertions에서 제공하는 것 중, 자주 사용되는 함수는 아래와 같다.
```java
assertEquals : 기댓값과 실제값이 같은지 비교한다.
assertNotNull : 값이 null인지 확인한다.
assertTrue : 조건이 참인지 확인한다.
assertAll : 모든 구문을 한꺼번에 실행하여 확인한다.
assertThrows : 예외가 발생하는지 확인한다.
assertTimeout : 특정 시간 안에 실행되는지 확인한다.
```
assertAll, assertThrows 예시는 아래와 같다.
```java
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStudyStatus(), "스터디를 처음 만들면 상태가 DRAFT 이어야 한다."),
                () -> assertTrue(study.getLimit() > 0 , "스터디 최대 참석 인원은 0보다 커야한다.")
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
```

**IntelliJ에서 ctrl + p 옵션을 통해 어떤 파라미터가 필요한지 알 수 있다.**

## 4. 조건에 따라 테스트 실행하기

특정 조건을 만족하는 경우에만 테스트를 실행하도록 한다.

assumeTrue(조건), assumingThat(조건, executable)을 통해 프로그래밍적으로 조건 테스트를 할 수 있다. 아래 예시를 참고하자.
```java
    @Test
    @DisplayName("특정 조건에 만족하는 경우 테스트 실행")
    void assumptionTest() {
        String testEnv = System.getenv("TEST_ENV");
        System.out.println("testEnv: " + testEnv);
        
        assumeTrue("LOCAL".equalsIgnoreCase(testEnv)); // 환경변수가 LOCAL 이어야 아래 테스트 실행

        Study study = new Study();
        assertNotNull(study);
        assertEquals(StudyStatus.DRAFT, study.getStudyStatus());
    }
```    

@Enable__, @Disabled__을 통해 선언적으로 조건 테스트를 할 수 있다. 아래 예시를 참고하자.
```java
    @Test
    @DisplayName("OS가 windows인 경우에만 실행")
    @EnabledOnOs(OS.WINDOWS)
    void osAssumptionTest() {
        Study study = new Study();
        assertNotNull(study);
    }
 ```

## 5. 태깅과 필터링

테스트 그룹을 만들어서 원하는 그룹만 테스트를 실행시키는 기능이다.

메소드에 @Tag를 붙인 후, 파일 설정의 edit -> configuration에서 지정하여 테스트를 실행할 수 있다.

## 6. 커스텀 태그

JUnit5에서 제공하는 것 뿐만 아니라 직접 태그를 만들 수 있다. 아래 예시를 통해 방법을 알 수 있다.

메소드에 공통적으로 붙는 애노테이션이 많아질 때 유용해보인다.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@Tag("fast")
public @interface FastTest {
}

@FastTest
@DisplayName("스터디 fast Test")
void tagTest1() {
    Study study = new Study(100);
    assertThat(study.getLimit()).isGreaterThan(0);
}
```

## 7. 반복 테스트(중요)

예제를 통해 살펴보면 반복 테스트 방법을 직관적으로 알 수 있다.
 
```java
public class RepeatedTest1 {
    @DisplayName("repeat test1")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeat(RepetitionInfo repetitionInfo) {
        System.out.println("test" + repetitionInfo + "/" + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("repeat test2")
    @ParameterizedTest(name="{index} {displayName}, message={0}")
    @ValueSource(strings = {"날씨가", "많이", "더워지고", "있네요."})
    void parameterizedTest(String message) {
        System.out.println(message);
    }
}
```
@RepeatedTest와 @ParameterizedTest, @ValueSource를 주목해서 살펴보면 된다.

이외에도 다양한 반복 테스트 방법이 있다.

주로 인자를 객체에 바인딩하여 테스트를 해야하므로 주의깊게 주의깊게 살펴보아야 한다.

ArgumentConverter.class를 정의해서 @ValueSource에서 제공되는 값을 생성자를 통해 객체에 바인딩하여 매개변수로 전달할 수 있다.

```java
    @DisplayName("스터디 만들기")
    @ParameterizedTest(name="{index} {displayName} message={0}")
    @ValueSource(strings = {"날씨가", "많이", "더워지고", "있습니다."})
    @EmptySource
    @NullSource
    void parameterizedTest1(String message) {
        System.out.println(message);
    }

    @DisplayName("스터디 만들기 : ArgumentConverter 사용")
    @ParameterizedTest(name="{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 40})
    void parameterizedTest2(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }
    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "can only convert to Study");
            // Constructor를 통해서 변환됨
            return new Study(Integer.parseInt(source.toString()));
        }
    }
```

ArgumentAccessor 또는 ArgumentAggregator를 통해 @Source의 인자를 조합하여 객체에 바인딩하는 방법도 있다. 
```java
    @DisplayName("스터디 만들기 : ArgumentAccessor 사용")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTest4(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println(study);
    }

    @DisplayName("스터디 만들기 : ArgumentAggregator 사용")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void parameterizedTest5(@AggregateWith(StudyArgumentsAggregator.class) Study study) {
        System.out.println(study);
    }

    static class StudyArgumentsAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
    }
```

## 8. 테스트 인스턴스 전략 변경

JUnit은 테스트마다 새로운 인스턴스를 생성한다. 그러므로, 클래스의 필드변수를 기본적으로 공유할 수 없다.

왜냐하면, 테스트간 의존성이 없어야하기 때문이다.

그러나, 필드변수를 공유해야할 때도 있다.

즉, 테스트마다 새로운 인스턴스를 생성하는 것이 아니라 인스턴트를 공유할 필요성이 있다는 뜻이다.

이때는 클래스에 아래의 애노테이션을 붙이자.

```java
@TestInstance(LifeCycle.PER_CLASS)
```

## 9. 테스트 순서 

시나리오를 테스트하는 등의 테스트 순서를 지정할 필요가 있을 수 있다.

아래의 애노테이션을 클래스에 붙이도록 하자.

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
```

MethodOrderer.OrderAnnotation.class 뿐만 아니라 다른 구현체도 존재한다.

## 10. junit-platform.properties

test쪽의 Annotation 설정을 일괄 변경하고 싶을 때 사용하도록 한다.

## 11. JUnit5 확장 모델

예제를 통해 살펴보자.

시간이 오래 걸리는 테스트인데 @SlowTest가 없는 테스트를 찾아내서 경고 메세지를 출력하고자 한다면 아래와 같이 코드를 작성하면 된다.

```java
public class SlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static long THRESHOLD = 1000L;
    public SlowTestExtension(long THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
    }
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        ExtensionContext.Store store = getStore(extensionContext);
        store.put("START_TIME", System.currentTimeMillis());
    }
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        ExtensionContext.Store store = getStore(extensionContext);
        Long startTime = store.remove("START_TIME", long.class);
        long duration = System.currentTimeMillis() - startTime;
        if(duration > THRESHOLD && !extensionContext.getTags().contains("slow")) {
            System.out.printf("Please consider mark method [%s] with @SlowTest\n", extensionContext.getRequiredTestMethod().getName());
        }
    }

    ExtensionContext.Store getStore(ExtensionContext extensionContext) {
        String testClassName = extensionContext.getRequiredTestClass().getName();
        String testMethodName = extensionContext.getRequiredTestMethod().getName();
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));
        return store;
    }
}
```

```java
//@ExtendWith(SlowTestExtension.class) /* 선언적인 방법 */
public class SlowTestSamples {

    @RegisterExtension /* THRESHOLD를 지정하고 싶을 때 사용할 수 있다. 프로그래밍적인 방법 */
    static SlowTestExtension slowTestExtension = new SlowTestExtension(1000L);

    @SlowTest
    void slowTest1() throws InterruptedException {
        Thread.sleep(1010);
    }
    @SlowTest
    void slowTest2() throws InterruptedException {
        Thread.sleep(1010);
    }
    @Test
    void slowTest3() throws InterruptedException {
        Thread.sleep(1010);
    }
}
```

## 12. 테스트 커버리지

테스트 대상을 얼마나 커버했는지를 나타내는 지표이다.

단위 테스트의 경우, 클래스와 컴포넌트 단위로 테스트가 진행된다.

테스트의 커버리지는 일반적으로 소스의 라인을 기준으로 한다. 이를 구문 커버리지라고 한다.

구문 커버리지 이외에도 조건 커버리지와 결정 커버리지가 있다. 구문 커버리지를 주로 테스트 커버리지로 사용한다.

구글의 테스트 관련 아티클에 따르면,

테스트 커버리지는 맹신하면 안되고 다른 테스트 수단에 보조적으로 사용해야 한다고 한다.

## 13. 단위 테스트 Tip

1. 엔티티, repository 테스트는 DB와 실제로 연동하여 테스트한다.
2. 서비스, 프레젠테이션 계층 테스트는 각각 repository, service를 Mocking하여 테스트한다.


그럼에도 높은 테스트 커버리지를 유지하며 개발하는 문화는 장기적으로 결함이 적은 결과를 만들어낸다고 한다.

그러므로, 테스트 커버리지를 적절하게 유지하되 너무 맹신하지 않고, 특별한 코드 실행 path와 edge 케이스를 고려하는 것이 중요하다고 한다.

**적절한 테스트 커버리지 유지 + 특정 코드 실행 Path 및 Edge 케이스 고려 + 필요한 경우 메소드 및 모듈간 통합 테스트**가 좋은 테스트 전략으로 보인다.
