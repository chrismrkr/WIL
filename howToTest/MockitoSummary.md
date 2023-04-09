# Mockito

Mock 객체는 프로그래머가 직접 행동을 관리하는 객체이고,

Mockito는 Mock 객체를 쉽게 만들고 관리할 수 있는 방법을 제공한다.

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
