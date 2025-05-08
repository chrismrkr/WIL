# MyBatis 요약

참고 자료 : https://mybatis.org/mybatis-3/ko/sqlmap-xml.html

## MyBatis란?

Java에서는 DB에 접근하기 위해 JDBC API를 사용할 수 있다. MyBatis는 mapper 파일, Dao 인터페이스를 활용하여 JDBC API를 간접적으로 사용하여 DB에 접근할 수 있는 기술이다.

그 뿐만 아니라 다양한 고급 기능도 존재한다.

***

## 주요 개념

### SqlSession

MyBatis에서 제공하는 해당 인터페이스의 메소드를 사용해서 DB에 SQL을 실행할 수 있다. SELECT, INSERT, DELETE, UPDATE, COMMIT, ROLLBACK 등 대다수의 SQL 관련 명령어를 지원한다.

스프링에서는 SqlSession에 대한 구현체로 SqlSessionTemplate을 제공하고 있다.

### SqlSessionFactory

SqlSession을 생성하기 위한 팩토리 클래스이다. SqlSessionFactory를 통해서 DataSource, mapper class path 등을 설정할 수 있다.

스프링에서는 SqlSessionFactoryBean을 구현체로 제공하고 있다.

### 사용 방법

스프링에서는 SqlSessionFactoryBean(SqlSessionFactory), 그리고 SqlSession(SqlSessionTemplate)을 빈으로 등록하여 사용할 수 있다.

***

## 동작 방식

스프링에서 제공하는 SqlSessionFactoryBean과 SqlSessionTemplate의 동작 방식은 아래와 같다.

SqlSessionFactoryBean을 스프링 빈으로 등록하는 시점에 아래 내용을 함께 저장한다.

+ 1. DB 관련 환경변수
+ 2. TransactionManager, DataSource
+ 3. 기타 MyBatis Mapper 설정

그리고, SqlSessionTemplate는 SqlSessionFactoryBean을 매개변수로 하여 스프링 빈으로 생성된다.

스프링 빈으로 등록된 SqlSessionTemplate을 통해서 SQL이 실행될 때, InvocationHandler에 의해 SQL 실행 로직 전 후로 TransactionManager의 Connection 획득 및 COMMIT & ROLLBACK 작업이 실행된다.

그러므로, @Transactional 어노테이션과 통합된 수 있다.

또한, SqlSessionTemplate은 싱글 톤으로 등록되었지만, SQL이 실행될 때 마다 새로운 객체를 생성하므로 Thread safe 하다.

***

## Mapper XML 사용 방법

공식 문서에서 사용 방법을 어렵지 않게 이해할 수 있다. 그 중에서 다소 특별한 기능에 한하여 정리한 내용은 아래와 같다.




