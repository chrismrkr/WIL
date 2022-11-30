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
