# Mockito

Mock 객체는 프로그래머에 의해 행동이 관리하는 객체이고,

Mockito는 Mock 객체를 쉽게 만들고 관리할 수 있는 방법을 제공한다. **객체 간에 의존관계가 있는 서비스를 테스트할 때 유용하게 사용된다.**

예를 들어, 특정 서비스가 DB 또는 외부 API를 호출하고, 테스트가 필요할 때, 항상 이를 호출할 수 없으므로 개발자가 외부 API 또는 DB가 어떻게 호출될지 직접 예측하여 mock 객체를 생성하여 테스트할 수 있다.

스프링 부트 2.2+ 버전에서 자동으로 Mockito 라이브러리가 추가된다.

## 1. Mock 객체 생성 방법

### 1.1 프로그래밍적 방식
```java
   MemberService memberService = Mockito.mock(MemberService.class);
   StudyRepository studyRepository = Mockito.mock(StudyRepository.class);
```

### 1.2 선언적 방식
```java
@ExtendWith(MockitoExtension.class)
class StudyServiceTest {
    @Mock MemberService memberServiceInstance;
    @Mock StudyRepository studyRepositoryInstance;
    
    void createStudyService2(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }
```
## 2. Mock 객체 Stubbing

Stubbing이란 Mock 객체의 행동과 결과를 프로그래머가 조작하는 것을 의미한다.

Mock 객체의 행동에 대한 결과를 아래와 같이 프로그래머가 조작할 수 있다.

```java
   Member expectedMember = new Member();
   expectedMember.setId(1L);
   expectedMember.setEmail("kangok@email.com");

   // stubbing : memberService.findById(1L)시 리턴할 값을 임의로 결정해둔다. 1L은 expectedMember를 반환함
   Mockito.when(memberService.findById(1L)).thenReturn(Optional.of(expectedMember));
```

또한, Mock 객체의 행동에 대한 결과를 아래와 같이 매번 다르게 만들 수 있다.

```java
   Member expectedMember = new Member();
   expectedMember.setId(1L);
   expectedMember.setEmail("kangok@email.com");

   // stubbing : memberService.findById(1L)시 리턴할 값을 임의로 결정해둔다. 1L은 expectedMember를 반환함
   Mockito.when(memberService.findById(Mockito.any()))
           .thenReturn(Optional.of(expectedMember))
           .thenThrow(new RuntimeException())
           .thenReturn(Optional.empty());
```

## 3. Mock 객체 결과 확인

Mock 객체가 어떻게 사용되었는지 확인하는 방법도 있다. 자세한 방법은 아래와 같다.

### 3.1 특정 메소드가 어떻게 호출되었는지 확인하기

memberService(Mock 객체)가 notify(study) 메소드를 1번 실행했는지 확인하는 예시이다.
```java
   Mockito.verify(memberService, Mockito.times(1)).notify(study);
```

memberService(Mock 객체)가 validate(Mockito.any()) 메소드를 한번도 실행하지 않았는지 확인하는 예시이다.
```java
   Mockito.verify(memberService, Mockito.never()).validate(Mockito.any());
```

### 3.2 특정 메소드가 어떤 순서로 호출되었는지 확인하기

memberService(Mock 객체)가 notify(study) -> notify(member) 순서로 메소드를 호출했는지 확인하는 예시이다.
```java
   // memberService 특정함수를 어떤 순서로 호출했는지 확인
   InOrder inOrder = Mockito.inOrder(memberService);
   inOrder.verify(memberService).notify(study);
   inOrder.verify(memberService).notify(member);
```

## 4. BDD 스타일 Mockito API

BDD(Behavior Driven Development) 스타일이란 Mock 객체의 행동을 given, when, then 3단계로 구분하여 정의하여 개발하는 것을 의미한다.

이것이 테스트에서도 잘 활용된다. BDDMockito 클래스의 정적 멤버함수로 제공된다.

```java
    @Test
    void study(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "Java");
        BDDMockito.given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then : mock 상태를 확인한다.
        Assertions.assertEquals(StudyStatus.OPENED, study.getStudyStatus());
        Assertions.assertNotNull(study.getOpenedDateTime());
        BDDMockito.then(memberService).should().notify(study);
        BDDMockito.then(memberService).shouldHaveNoMoreInteraction();
    }
```


