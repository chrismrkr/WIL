# Contents

### 1. 스프링 기본

스프링 기본 기능(1_basic.md) 주제별 정리(DI, Ioc 포함)

#### 1. 객체지향의 장점 및 바람직한 객체지향적 설계의 특징

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#1-%EC%98%AC%EB%B0%94%EB%A5%B8-%EA%B0%9D%EC%B2%B4%EC%A7%80%ED%96%A5%EC%A0%81-%EC%84%A4%EA%B3%84%EB%9E%80

#### 2. 의존관계 주입(Dependency Injection)을 위한 첫번째 방법 : 설정 클래스(AppConfig.class)와 비즈니스 클래스의 분리

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#2-ocp-dip-%EC%9B%90%EC%B9%99%EC%9D%84-%EC%9C%84%ED%95%9C-%EC%B2%AB%EB%B2%88%EC%A7%B8-%EB%B0%A9%EB%B2%95-appconfig

#### 3. 의존관계 주입(Dependency Injection)을 위한 두번째 방법 : 스프링 컨테이너 사용

스프링 컨테이너에 수동으로 빈을 등록하는 방법(@Configuration, @Bean)을 소개한다.

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#3-ocp-dip-%EC%9B%90%EC%B9%99%EC%9D%84-%EC%9C%84%ED%95%9C-%EB%91%90%EB%B2%88%EC%A7%B8-%EB%B0%A9%EB%B2%95-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%BB%A8%ED%85%8C%EC%9D%B4%EB%84%88-%EC%82%AC%EC%9A%A9

#### 4. 스프링 빈을 수동으로 찾는 방법 : ApplicationContext 사용

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#4-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%BB%A8%ED%85%8C%EC%9D%B4%EB%84%88%EC%99%80-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B9%88

#### 5. 싱글톤 컨테이너의 특징

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#5-%EC%8B%B1%EA%B8%80%ED%86%A4-%EC%BB%A8%ED%85%8C%EC%9D%B4%EB%84%88

#### 6. 스프링 빈 컨테이너 자동 생성 방법과 의존관계 주입 : @ComponentScan, @Component, @Autowired

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#6-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-%EC%8A%A4%EC%BA%94%EA%B3%BC-%EC%9D%98%EC%A1%B4%EA%B4%80%EA%B3%84-%EC%9E%90%EB%8F%99-%EC%A3%BC%EC%9E%85

#### 7. 다양한 의존관계 주입 방법 : 생성자 주입 및 @Qualifier 사용 등

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#7-%EC%9D%98%EC%A1%B4%EA%B4%80%EA%B3%84-%EC%A3%BC%EC%9E%85

#### 8. 빈 생명주기 관리 : @PreDestroy, @PostContruct

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#8-%EB%B9%88-%EC%83%9D%EB%AA%85%EC%A3%BC%EA%B8%B0-%ED%99%95%EC%9D%B8-callback

#### 9. 다양한 빈 스코프

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/1_basic.md#9-%EB%B9%88-%EC%8A%A4%EC%BD%94%ED%94%84

***

### 2. 스프링 고급

스프링 고급 기능(2_advanced.md) 주제별 정리(AOP 포함)

#### 1. 스레드 로컬

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#1-%EC%8A%A4%EB%A0%88%EB%93%9C-%EB%A1%9C%EC%BB%AC

#### 2. 디자인 패턴 : 템플릿 메소드 패턴

공통 로직을 처리하는 부분을 추상 클래스로 만들고, 이를 비즈니스 로직을 담은 클래스에서 사용하는 패턴

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#2-%ED%85%9C%ED%94%8C%EB%A6%BF-%EB%A9%94%EC%84%9C%EB%93%9C-%ED%8C%A8%ED%84%B4

#### 3. 디자인 패턴 : 전략 패턴

공통 로직을 처리하는 부분을 인터페이스로 만들고, 이를 비즈니스 로직을 담은 클래스에서 위임받아 사용하는 패턴

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#3-%EC%A0%84%EB%9E%B5-%ED%8C%A8%ED%84%B4

#### 4. 디자인 패턴 : 템플릿 콜백 패턴

공통 로직 처리하는 부분을 인터페이스로 만들고, 이를 비즈니스 로직을 담은 클래스의 매개변수로 사용하는 패턴

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#4-%ED%85%9C%ED%94%8C%EB%A6%BF-%EC%BD%9C%EB%B0%B1-%ED%8C%A8%ED%84%B4

#### 5. 디자인 패턴 : 프록시 패턴과 데코레이터 패턴

동일한 인터페이스를 이용하여 공통 로직 클래스와 비즈니스 로직 클래스를 모두 구현하는 패턴. 클라이언트는 인터페이스만 참조하면 된다.

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#5-%ED%94%84%EB%A1%9D%EC%8B%9C-%ED%8C%A8%ED%84%B4%EA%B3%BC-%EB%8D%B0%EC%BD%94%EB%A0%88%EC%9D%B4%EC%85%98-%ED%8C%A8%ED%84%B4


#### 6. 동적 프록시 기술

인터페이스가 존재하는 경우 JDK 동적 프록시(InvocationHandler), 구현 클래스가 존재하는 경우 CGLIB(MethodInterceptor)를 사용한다.

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#6-%EB%8F%99%EC%A0%81-%ED%94%84%EB%A1%9D%EC%8B%9C-%EA%B8%B0%EC%88%A0

#### 7. 스프링에서 지원하는 프록시

동적 프록시의 복잡함을 해소하기 위해 Advice 개념 등장

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#7-%EC%8A%A4%ED%94%84%EB%A7%81%EC%97%90%EC%84%9C-%EC%A7%80%EC%9B%90%ED%95%98%EB%8A%94-%ED%94%84%EB%A1%9D%EC%8B%9C

#### 8. 빈 후처리기

자동으로 생성된 스프링 빈에도 Advice를 적용하기 위한 방법

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#8-%EB%B9%88-%ED%9B%84%EC%B2%98%EB%A6%AC

#### 9. @Aspect AOP

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#9-aspect-aop

#### 10. 스프링 AOP 개념

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#10-%EC%8A%A4%ED%94%84%EB%A7%81-aop-%EA%B0%9C%EB%85%90

#### 11. 스프링 AOP 구현

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#11-%EC%8A%A4%ED%94%84%EB%A7%81-aop-%EA%B5%AC%ED%98%84

#### 12. 스프링 AOP - 포인트컷

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#12-%EC%8A%A4%ED%94%84%EB%A7%81-aop---%ED%8F%AC%EC%9D%B8%ED%8A%B8%EC%BB%B7


#### 13. 스프링 AOP 주의사항

내부 호출 문제에 주의해야 한다.

https://github.com/chrismrkr/WIL/blob/main/Spring/CorePrinciple/2_advanced.md#13-%EC%8A%A4%ED%94%84%EB%A7%81-aop-%EC%A3%BC%EC%9D%98%EC%82%AC%ED%95%AD
