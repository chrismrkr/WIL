# Chaos Monkey

카오스 엔지니어링이란 시스템 환경에서의 불확실성을 파악하고, 이에 대한 해결방안을 찾는 방법을 의미한다.

카오스 엔지니어링 Tool 중 하나가 Chaos Monkey이다.

예를 들어, 응답 지연, 예외 발생, 애플리케이션 강제 종료, 그리고 메모리 누수 등과 같은 상황을 발생시켜 해결방안을 찾을 수 있게 만든다.


## 1. Chaos Monkey for Spring Boot 설정

**1.** Chaos Monkey를 스프링 부트에서 사용하기 위해 2가지 의존성을 추가하면 된다.

+ chaos-monkey-spring-boot
+ spring-boot-starter-actuator

**2.** Chaos Monkey를 활성화한다. application.properties에 아래를 추가한다.

+ spring.profiles.active=choas-monkey

또는, Run Configurations에서 빌드시 chaos-monkey를 활성화하는 모드를 생성하면 된다.

**3.** 스프링부트 actuator 엔드포인트 활성화

아래를 applicaiton.properties에 추가한다.

+ management.endpoints.chaosmonkey.enabled=true
+ management.endpoints.web.exposure.inclue=health,info,chaosmonkey

## 2. 예제. 응답 지연 상황 재현
