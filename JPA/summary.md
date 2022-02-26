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
+ 영속성 컨텍스트 내 변경 감지

Entity Manager를 통해 데이터베이스로부터 엔티티를 불러올 때 영속성 컨텍스트(1차 캐시)에 저장된다. 마찬가지로 엔티티를 저장 및 삭제할 때도 바로 데이터베이스에 반영되는 것이 아니라 1차 캐시에 먼저 영속된다. 그 후 Entity Manager가 커밋되는 시점에 실제로 영속성 커텍스트에 저장된다.(쓰기지연) 

엔티티는 영속, 비영속, 준영속 세가지 상태로 나눌 수 있다.

***

## 2. 엔티티 매핑
**Entity를 데이터베이스 테이블에 매핑할 때 사용되는 Annotation들은 아래와 같다.**
+ @Entity: JPA가 관리하는 클래스. 클래스 이름과 동일한 데이터베이스 테이블로 암묵적으로 매핑된다.
  + 기본 생성자가 필요하다. 생성자를 만들지 않으면 컴파일 시 자동으로 생성해주지만, 직접 정의한 경우 기본 생성자는 반드시 생성되어야 한다.
+ @Table: 엔티티와 매핑할 데이터베이스 테이블을 명시적으로 지정한다
  + name="...": 매핑할 데이터베이스 테이블 이름
  + uniqueConstraints=...: 테이블의 Unique Key 조건을 생성
+ @Column(name="..."): @Entity로 관리되는 클래스의 변수(속성)의 칼럼명을 명시적으로 지정함.
  + nullable= : null값 가능 여부 지정(기본 값 false)
  + length= : 변수가 String일 때, varchar 길이를 지정함.(기본 값 255)
+ @Enumerated(Type.String | Type.Ordinal): @Entity로 관리되는 클래스에 Enum 타입의 변수를 테이블에 매핑한다.
  + Type을 반드시 String으로 설정해야 하는 이유는 무엇인가? ~~타입이 Ordinal일 때 Enum 클래스의 변수가 추가되면 문제됨~~
+ @Temporal: Java의 날짜 타입 객체를 매핑할 때 사용함.
+ @Lob: Lob(String) 타입을 지정함.
+ @Transient: @Entity로 관리되는 클래스 내의 변수(속성)을 데이터베이스 테이블에 매핑하지 않고자 할 때 사용함.

+ 기본 키(Primary Key) 매핑 방법
  + @Id: 엔티티 중 기본 키로 설정할 변수
  + @GeneratedValue(strategy= ..): 기본 키를 자동 생성함. (생략시 기본 키 직접 할당 필요)

+ 기본 키(Primary Key) 자동 생성 전략?
  + IDENTITY 전략: 기본 키 생성을 데이터베이스에 위임함.
    + 그러므로, DB에 아직 저장되지 않은 영속 상태인 엔티티의 기본 키를 조회하면 데이터베이스 조회가 필요하다.
  + SEQUENCE 전략: 시퀀스는 유일한 값을 순서대로 생성하는 데이터베이스 오브젝트임. 아래의 코드 예시를 통해 확인하도록 한다.
  
'''c
  @Entity
  @SequenceGenerator {
    name = "SQG",
    sequenceName = "BS",
    
  
'''
