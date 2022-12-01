# Spring Security

Spring Security는 웹 서비스에 대한 인증과 인가 기능을 제공하는 Spring 계열의 프레임워크이다. 


## 1. 기본 API 및 Filter의 이해

Spring Security를 사용하기 위해서는 Spring boot의 Dependency에 아래를 추가해야 한다.

Maven Project일 때, pom.xml에 아래를 추가하도록 한다. 물론, start.spring.io에서도 추가할 수 있다.

```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
```

### 1.1 Filter의 이해

스프링 시큐리티의 기본 API를 이해하기 전에 Filter에 대해서 이해해야 한다.

MVC 디자인 패턴이란 프론트 컨트롤러로 Http Request를 보내면, 이에 맞는 핸들러 어댑터를 불러온 후, 핸들러 어댑터를 통해 Request에 대한 컨트롤러(핸들러)를 실행한 후, Model에 필요한 파라미터들을 저장한 후, View로 반환하는 것을 의미한다.

Filter란 프론트 컨트롤러가 Http Request를 받기 전에 선제적으로 HTTP 요청을 걸러주는 기능이다.


### 1.2 사용자 정의 보안 기능 구현


### 1.3 인증 API: Form 인증 방식
