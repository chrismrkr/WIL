# JPA(JAVA Persistence API)
***
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
### Entity를 데이터베이스 테이블에 매핑할 때 사용되는 Annotation들은 아래와 같다.
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


### 기본 키(Primary Key) 매핑 방법
+ @Id: 엔티티 중 기본 키로 설정할 변수
+ @GeneratedValue(strategy= ..): 기본 키를 자동 생성함. (생략시 기본 키 직접 할당 필요)


### 기본 키(Primary Key) 자동 생성 전략?
+ IDENTITY 전략: 기본 키 생성을 데이터베이스에 위임함.
  + 그러므로, DB에 아직 저장되지 않은 영속 상태인 엔티티의 기본 키를 조회하면 데이터베이스 조회가 필요하다.
  
  
+ SEQUENCE 전략: 시퀀스는 유일한 값을 순서대로 생성하는 데이터베이스 오브젝트임. 아래의 코드 예시를 통해 확인하고 작동 과정을 살펴보도록 한다.
  
  ```java
  @Entity
  @SequenceGenerator (
    name = "SQG",
    sequenceName = "BS",
    initialValue = 1, allocationSize = 50
    )
   public class Board {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "SQG")
   private Long Id;
   
   ...
   }
  ```
    + 데이터베이스에 "BS"라는 시퀀스 오브젝트를 생성한다. 초기 값은 1이고 한번 호출될 때마다 50씩 증가한다.
    + 첫 em.persist() 시 DB로 부터 초기값 1과 50을 가져와 영속성 컨텍스트에 저장한다.
    + em.persist()가 발생할 때 마다 영속성 컨텍스트에 저장된 1부터 51까지의 값을 순서대로 지정한다. 그러므로, IDENTITY 전략에 비해 네트워킹으로 인한 비용이 적다.
    + em.persist()시 영속성 컨텍스트에 저장된 값이 모두 사용되었다면, 마찬가지로 101을 가져와 영속성 컨텍스트에 저장한다.
  + 이 과정을 반복한다.

+ TABLE 전략: 키 생성 전용 테이블을 하나 만들고 칼럼을 만들어 데이터베이스 시퀀스를 흉내내도록 한다.
  ```java
  @Entity
  @TableGenerator (
    name = "TableGenerate",
    table = "TG",
    pkColumnValue = "TG_pk", allocationSize = 1)
    )
   public class Board {
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE,
                    generator = "TableGenerate")
   private Long Id;
   
   ...
   }
  ```
 바람직한 기본 키 생성 전략은? Long형 + (IDENTITY | SEQUENCE | TABLE) + 자체 로직


***

## 3. 연관관계 매핑
**연관관계 매핑 시, 연관관계의 주인이 되는 엔티티가 항상 외래키(FK)를 속성으로 갖는다.**

### 3.1 다대일(N:1) 매핑
ex. 회원(N)과 부서(1), 회원은 2개 이상의 부서에 포함될 수 없다.
+ 단방향 매핑
```java
@Entity
public class Member {
  ...
  @ManyToOne
  @JoinColumn(name="DEPARTMENT_ID") // 종속되는 엔티티의 기본 키 칼럼명
  private Department department;
  ...
  }
 ```
 
+ 양방향 매핑
 ```java
 @Entity
 public class Department {
  ...
  @OneToMany(mappedby = "department")
  private ArrayList<Member> members = new ArrayList<>();
  ...
  ```
 + 연관관계의 주인이 되는 엔티티(N)
 가 외래키를 관리한다. 즉, Member 엔티티에서 Department 외래키를 관리한다. 양방향 매핑 시 연관관계의 주인이 되는 부분에서만 저장해도 실제 데이터베이스에 반영된다. 그러나, 객체 지향적 관점에서 양방향 모두 저장해주는 것이 옳다.

```java
void main() {
  Department department = new Department();
  Member member1 = new Member();
  Member member2 = new Member();
  
  member1.setDepartment(department);
  member2.setDepartment(department); 
  em.persist(member1);
  em.persist(member2);
  
  department.setMember(member1);
  department.setMember(member2); 
  // 이 코드는 없어도 데이터베이스에서는 아무 영향이 없다.
  // 그러나, 객체 관점에서는 세팅을 해주는 것이 옳다.
  
  em.persist(department);
```


### 3.2 일대다(1:N) 매핑
**일대다 관계와 다대일 관계 모두 외래 키는 항상 다(N)쪽 테이블에 존재한다. 그러나, 연관관계의 주인은 외래키가 없는 일(1)이고, 일(1)에서 다(N)의 외래키를 관리한다는 점에서 다대일 매핑과 차이가 있다.**

+ 단방향 매핑: 원칙적으로 일대다(1:N) 매핑은 단방향만 존재한다.
```java
@Entity
public class Team {
  ...
   @OneToMany
   @JoinColumn(name="Member_ID")
   private List<Member> members = new ArrayList<>(); // List 뿐만 아니라 Set, HashMap 등의 자료구조도 사용가능.
   ...
   }

....
```

+ 양방향 매핑: 원칙적으로 일대다 양방향 매핑은 불가능하다. 그러나, 아래와 같이 바꿀 수있다.
```java
@Entity
public class Member {
  ...
  @ManyToOne
  @JoinColumn(name="team_id", insertable=false, updatable=false)
  private Team team;
  ...
  }
```
+ 일대다(1:N) 매핑된 Team과 Member 엔티티가 있고, team1에 속한 member1, member2 엔티티를 저장한다면?
+ 다대일(N:1) 매핑인 경우 3개의 insert 쿼리만 발생한다. 그러나, 일대다(1:N) 매핑에서 3개의 insert 쿼리 후, 2개의 update 쿼리가 추가적으로 발생한다. 관리와 성능 상 어려움이 존재하므로 가급적이면 다대일 매핑을 사용하자.
 
 ### 3.3 일대일(1:1) 매핑
 **일대일(1:1) 관계는 양쪽 엔티티간 사로 하나의 관게만을 갖는다. 예를 들어, 회원(Member) 엔티티와 팀(Team) 엔티티가 존재할 때, 팀은 반드시 한 회원에 의해서만 관리되고 회원 또한 2개 이상의 팀을 관리할 수 없다면 이는 일대일 관계에 해당된다.**
주 테이블(회원)에서 외래키(팀)을 관리하는 방법과 대상 테이블(팀)에서 외래키(회원)을 관리하는 방법 2가지가 존재한다.
+ 주 테이블에서 관리

```java
@Entity
public class Member {
  ...
   @OneToOne
   @JoinColumn(name="TEAM_ID")
   private Team team;
   ...
   }

// 만약 양방향으로 매핑하고자 한다면 대상 테이블에 매핑되는 엔티티에도 아래와 같이 설정한다.

@Entity
public class Team {
  ...
  @OneToOne(mappedby="team")
  private Member member;
  ...
}
```

 주 테이블에서 관리하는 전략은 객체지향적 개발 관점에서 바람직한 설계이다.
 
 + 대상 테이블에서 관리
 ```java
@Entity
public class Team {
  ...
   @OneToOne
   @JoinColumn(name="MEMBER_ID")
   private Member member;
   ...
   }

// 대상 테이블에서 관리한다면, 반드시 양방향 매핑되어야 한다.

@Entity
public class Member {
  ...
  @OneToOne(mappedby="member")
  private Team team;
  ...
}
```

대상 테이블에서 관리하는 전략은 데이터베이스 설계자 입장에서 바람직한 설계이다. 만약 팀은 반드시 한명의 회원에 의해 관리되어야 하나, 한 회원이 여러 팀을 관리할 수 있다고 요구사항이 변경되었다고 가정하자. 이는 일대일 관계에서 일대다 관계로 변화된 것을 의미한다. 대상 테이블에서 외래키를 관리한 경우, 쉽게 로직을 변경할 수 있지만, 주 테이블에서 관리했다면 그렇지 않다. 
  
