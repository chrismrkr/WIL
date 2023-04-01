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

JUnit5에서 제공하는 것 뿐만 아니라 직접 태그를 만들 수 있다.
