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

### 연관관계 매핑 SELECT 방법

예를 들어, 블로그와 작가 엔티티가 있다고 가정하자. 그렇다면 블로그와 작가는 서로 N:1 관계로 외래키를 통해 서로 매핑될 수 있다.

만약 특정 작가에 대한 엔티티 정보, 그리고 작가가 작성한 블로그에 대한 엔티티 정보 조회가 필요한 상황에서 아래와 같이 쿼리를 실행할 수 있다.

```xml
<resultMap id="blogResult" type="Blog">
  <association property="author" column="author_id" javaType="Author" select="selectAuthor"/>
</resultMap>

<select id="selectBlog" resultMap="blogResult">
  SELECT * FROM BLOG WHERE ID = #{id}
</select>

<select id="selectAuthor" resultType="Author">
  SELECT * FROM AUTHOR WHERE ID = #{id}
</select>
```

위와 같은 경우, 조회된 작가(Author) 인원 만큼 블로그(Blog)를 조회해야 하는 N+1 문제가 발생할 수 있다.

이를 해결하기 위해서 MyBatis는 조회 시 Join을 하도록 권장하고, 결과를 적절한 방법으로 매핑할 것을 권고하고 있다. 방법은 아래와 같다.

```xml
<select id="selectBlog" resultMap="blogResult">
  select
    B.id            as blog_id,
    B.title         as blog_title,
    B.author_id     as blog_author_id,
    A.id            as author_id,
    A.username      as author_username,
    A.password      as author_password,
    A.email         as author_email,
    A.bio           as author_bio
  from Blog B left outer join Author A on B.author_id = A.id
  where B.id = #{id}
</select>
```

```xml
<resultMap id="blogResult" type="Blog">
  <id property="id" column="blog_id" />
  <result property="title" column="blog_title"/>
  <association property="author" javaType="Author">
    <id property="id" column="author_id"/>
    <result property="username" column="author_username"/>
    <result property="password" column="author_password"/>
    <result property="email" column="author_email"/>
    <result property="bio" column="author_bio"/>
  </association>
</resultMap>
```

1:N 관계에서도 마찬가지로 Join 과 적절한 resultMap 매핑을 사용할 것을 권고하고 있다.


***

## 동적 쿼리




