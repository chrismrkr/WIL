# JPA(JAVA Persistence API)

## 1. JPA 소개
**JPA란 JAVA Persistent API의 약자로 관계형 데이터베이스를 객체지향 언어인 Java로 운영 및 개발할 때 발생하는 패러다임의 불일치를 해결하기 위한 기술이다.**

### 1.1 환경설정 
**./src/main/resources/META-INF/persistence.xml 파일을 생성해 WAS와 연동할 DB 정보를 입력한다. 필수적인 정보는 아래와 같다.**
+ DB 드라이버
+ DB 사용자 아이디 및 비밀번호
+ DB URL(IP)
+ DB 방언(ex. H2, Oracle, MySQL)

또한 ./pom.xml을 생성해 하이버네이트와 데이터베이스 의존관계를 설정한다.

### 1.2 JPA 간단 동작방식
**persistence.xml에 설정한 persistence Unit(영속성 유닛)당 하나의 persistence.EntityManager를 생성할 수 있다. 즉, 하나의 데이터 베이스마다 하나의 영속성 컨텍스트를 설정할 수 있다는 것을 의미한다. 영속성 유닛은 여러 Entity Manager를 생성할 수 있고 Entity Manager 사이에는 스레드 공유하면 트랜잭션에 문제를 일으킬 수 있다.**

### 1.3 JPA의 중요 특징: 영속성 컨텍스트
+ 1차 캐시
+ 쓰기 지연
