# JPA(JAVA Persistence API)
***
## 1. JPA 소개
**JPA란 JAVA Persistent API의 약자이다.**

Java는 데이터베이스와 연동하여 개발할 때, JDBC API를 사용해야 한다.

그러나, JDBC를 사용하면 매번 WAS-DB 사이에 connection을 생성하고 해제해야 한다는 단점이 있고, SQL 의존적인 개발을 해야한다.

SQL 의존적인 개발을 하면 Java의 객체지향의 특성을 제대로 살릴 수 없다. 이러한 문제를 해결한 Java의 DB 매핑 기술이 JPA이다.

### 1.1 환경설정 
**./src/main/resources/META-INF/persistence.xml 파일을 생성해 WAS와 연동할 DB 정보를 입력한다. 필수적인 정보는 아래와 같다.**
+ DB 드라이버
+ DB 사용자 아이디 및 비밀번호
+ DB URL(IP)
+ DB 방언(ex. H2, Oracle, MySQL)

또한 ./pom.xml을 생성해 하이버네이트와 데이터베이스 의존관계를 설정한다.

### 1.2 JPA 동작방식

**persistence.xml에 설정한 persistence Unit당 하나의 persistence.EntityManager를 생성할 수 있다.**

**즉, 하나의 데이터 베이스마다 하나의 영속성 유닛(Entity Manager Factory)을 설정할 수 있다는 것을 의미한다.** 

**영속성 유닛은 여러 Entity Manager를 생성할 수 있고 Entity Manager 사이에는 스레드 공유하면 문제를 일으킬 수 있다.**

### 1.3 JPA의 영속성 컨텍스트

영속성 컨텍스트란 데이터베이스에 레코드를 저장하거나 레코드를 읽어올 때, 엔티티 매니저가 이를 기록하는 공간이다.

영속성 컨텍스트는 트랜잭션마다 개별적으로 생성되는 것이 기본이다.

#### 1.3.1 엔티티의 생명주기

+ 비영속 : 영속성 컨텍스트와 관련없는 상태
+ 영속 : 영속성 컨텍스트에 저장된 상태(entityManager.persist(obj))
+ 준영속 : 영속성 컨텍스트에 저장되었다가 분리된 상태(em.clear(), em.detach(obj), em.close())
+ 삭제 : 삭제된 상태(em.remove(obj))

#### 1.3.2 영속성 컨텍스트의 특징

+ 영속성 컨텍스트에 영속되는 객체는 반드시 식별자가 있어야 한다.
+ 트랜잭션이 Commit될 때, 영속성 컨텍스트의 내용이 DB에 반영된다. 이를 flush라고 한다.
+ 영속성 컨텍스트는 1차 캐시, 쓰기 지연, 변경 감지, 지연로딩(Lazy Loading)을 제공한다.

영속성 컨텍스트에 존재하지 않는 객체는 Lazy Loading을 할 수 없다.

#### 1.3.3 엔티티 조회

만약 영속성 컨텍스트(1차 캐시)에 존재하면 이를 가져오고, 존재하지 않는 경우에는 DB에서 조회한다. DB에서 조회할 때도 영속성 컨텍스트에 저장되어 남는다.

JPA는 Repeatable-Read 격리수준을 Application level에서 제공한다. JPA에서는 어떻게 아래의 예시에서 repeatable-read를 제공할 수 있을까?

T1 : ------Read(A)-----------------------------Read(A)

T2 : ---------------Update(A)------commit-------------

T1의 영속성 컨텍스트에는 A가 영속되어 있다. 반면, T2의 영속성 컨텍스트에는 A가 영속된 후, update될 때 값이 변경되고 쓰기 지연 SQL 저장소에 update 쿼리가 저장된다.

그러므로, 각 트랜잭션마다 서로 다른 영속성 컨텍스트를 갖고 있으므로 Repeatable-read를 제공한다.


#### 1.3.4 엔티티 저장

엔티티를 저장하면(em.persist(obj), 1차 캐시에 엔티티가 저장되고, SQL 쓰기 지연 저장소에 INSERT Query가 보관된다. 

그리고, em.flush될 때(예를 들어, commit되는 경우), DB에 쓰기지연 SQL 저장소의 Query가 발생해 DB에 반영된다.

#### 1.3.5 엔티티 수정

DB 레코드를 수정할 때는 INSERT Query가 필요하다. 이는 수정 쿼리가 많아지고 SQL 의존적인 개발을 해야 한다는 문제점이 있다.

JPA에서는 영속성 컨텍스트의 변경 감지를 통해 위의 문제를 해결했다.

영속성 컨텍스트에 엔티티가 최초로 저장될 때, 원본의 스냅샷과 함께 저장된다.

그리고 영속성 컨텍스트에 영속된 엔티티가 변경된다면, 엔티티를 변경하고 쓰기 지연 SQL 저장소에 UPDATE 쿼리를 저장한다.

만약 entityManager가 flush 된다면, 스냅샷과 원본 엔티티를 비교하여 변경이 있는 경우에 Update Query를 수행한다.

데이터 베이스는 동일한 쿼리를 보내면 이전에 파싱해둔 것을 사용하므로 모든 필드를 덮어 씌운다고 해서 성능이 크게 나빠지지는 않는다.

### 1.4 flush

영속성 컨텍스트를 DB에 반영하는 것을 flush라고 하며 아래의 경우에 flush가 발생한다. 영속성 컨텍스트 내용을 삭제하는 것은 아니다.

+ 직접 em.flush 호출
+ 트랜잭션 commit(@Transactional 포함)시 flush 호출 : commit 전에 영속성 컨텍스트를 flush해야 쓰기 지연 SQL 저장소의 쿼리가 실행되기 때문이다.
+ JPQL과 같은 객체지향쿼리 실행 시 flush 호출

아래의 예제를 보면 JPQL이 실행될 때, flush가 필요함을 알 수 있다.

```java
em.persist(memberA);
em.persist(memberB);
// 이 시점에 영속성 컨텍스트에 memberA와 memberB가 존재하고 DB에는 없다. 

List<Member> memberList = em.createQuery("select m from Member m", Member.class).getResultList();
// memberA, memberB는 영속성 컨텍스트에 존재하나 List<member>는 없다. 설상가상으로 DB에 정보가 반영되어 있지도 않다.
// memberList에는 아무것도 조회되지 않았다. 그러므로, JPQL 실행 시, flush가 필요하다.
```

물론, flush를 commit할 때만 실행하도록 변경할 수도 있긴 하나 기본은 commit, JPQL 2가지일 때 발생한다.

### 1.5 준영속 상태

영속성 컨텍스트에서 분리한 상태를 준영속이라고 한다.

만약, 준영속 상태인 엔티티를 다시 영속 상태로 변경하려면 entityManager.merge(obj)를 사용하면 된다.

```java
Obj newObj = em.merge(obj);
```

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
**연관관계의 주인은 외래 키를 등록, 삭제, 수정할 수 있다. 그러나, 주인이 아닌 곳은 읽기만 가능하다.**

N:1, 1:N, N:N에서 항상 N에 외래키(1)가 존재한다. 물론, N이 항상 연관관계의 주인인 것은 아니다.

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

일대다 매핑시 일(1)에서 insert 쿼리가 발생한다면, 이와 연관된 다(N)의 Update 쿼리가 발생한다. 가급적 1:N 보다는 N:1 매핑을 사용하자.

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
 **일대일(1:1) 관계는 양쪽 엔티티간 사로 하나의 관계만을 갖는다. 예를 들어, 회원(Member) 엔티티와 팀(Team) 엔티티가 존재할 때, 팀은 반드시 한 회원에 의해서만 관리되고 회원 또한 2개 이상의 팀을 관리할 수 없다면 이는 일대일 관계에 해당된다.**

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

대상 테이블에서 관리하는 전략은 데이터베이스 설계자 입장에서 바람직한 설계이다.

만약 팀은 반드시 한명의 회원에 의해 관리되어야 하나, 한 회원이 여러 팀을 관리할 수 있다고 요구사항이 변경되었다고 가정하자. 

이는 일대일 관계에서 일대다 관계로 변화된 것을 의미한다. 대상 테이블에서 외래키를 관리한 경우, 쉽게 로직을 변경할 수 있지만, 주 테이블에서 관리했다면 그렇지 않다. 
  
### 3.4 다대다(N:N) 매핑

**다대다(N:N) 관계는 일대다, 또는 다대일 관계로 풀어서 매핑해야 한다.**

회원이 상품을 주문할 수 있다는 요구사항을 예로 들자. 회원 엔티티와 상품 엔티티만 존재한다면, 이를 주문할 수 있는 요구사항으로 연결하기 위해서는 다대다(N:N) 매핑이 필요하다. 

그러나, 주문 날짜, 주문 일자와 같은 추가적인 속성이 필요할 수 있으므로 새로운 엔티티를 통해 다대다 관계를 풀어내는 것이 필요하다.

기존의 Member, Product 엔티티가 존재하고 새로운 엔티티 Order을 생성하고자 하면 아래와 같이 쓸 수 있다.

```java
public class OrderId implements Serializable {
  private String member;
  private String team;
  
  @Override
  public boolean equals(object o) { ... }
  }
  
@Entity
@IdClass(OrderId.clasS) 
// 복합 기본 키 생성을 위해서 사용한다.(식별 관계) 물론, Order 엔티티 자체에 대한 새로운 기본 키 생성도 가능하다.(비식별 관계) 
public class Order {
  ...
  @Id
  @ManyToOne
  @JoinColumn(name="MEMBER_ID")
  private Member member;
  
  @Id
  @OneToMany
  @JoinColumn(name="PRODUCT_ID")
  private List<Product> products = new ArrayList<>();
  
  ... 추가 attribute .... 
  }
  
```

**어떤 엔티티에 연관관계 편의 메서드를 정의해야 할까?**

연관관계 편의 메서드란 양방향 매핑(주인의 영속, 대상의 양방향 매핑)을 위해 존재하는 메서드이다. 

연관관계의 주인이 외래키를 관리하므로 이곳에 연관관계 편의 메서드를 작성해야 한다고 착각하면 안된다.

주로 사용되는 엔티티에 연관관계 편의 메서드를 작성하도록 한다.

또한, 엔티티를 생성하는 것은 필요에 따라 static 생성 메소드, 생성자, setter 등을 사용하도록 한다. 

**연관관계 저장 시 주의사항**

연관관계 주인을 선택하는 것을 외래키 관리자를 선택하는 것이다. 그러므로, 연관관계 주인 쪽에서 연관관계를 매핑하면 기능상 문제는 없다.

그러나, 객체 관점에서 양방향 모두 매핑해주는 것이 좋다.

```java
 Team teamA = new Team("teamA");
 Member member1 = new Member("m1");
 Member member2 = new Member("m2");
 
 member1.setTeam(teamA);
 teamA.getMemberList().add(member1); // 없어도 문제없지만, 테스트 코드 작성 시 문제를 일으킬 수 있다.
 
 member2.setTeam(teamA);
 teamA.getMemberList().add(member2);
```

또한, 연관관계가 수정될 때도 이전 연관관계를 완전히 끊는 것에 주의한다.


### 3.5 상속관계 매핑
**관계형 데이터베이스에서 상속관계를 직접적으로 다룰 수 없다. 그러나, 슈퍼타입-서브타입 모델로 상속 관계와 근접하게 표현할 수 있다.**

부모인 Item 엔티티와 자식인 Album, Book 엔티티가 존재한다고 예를 들자. 자식 엔티티는 부모 엔티티의 속성을 모두 상속 받는다. 이를 나타낼 수 있는 전략은 아래와 같다.

+ 조인 전략

Album과 Book 엔티티(클래스)는 Item 엔티티(추상 클래스)를 상속받는다. 이에 따라, 데이터베이스에서 Album 테이블과 Book 테이블이 개별적으로 존재하게 된다.

데이터베이스를 정규화한다는 장점이 있지만 조인으로 인한 쿼리 성능이 저하되는 문제가 있다.

```java
@Entity
@Inheritence(strategy = Joined)
@Discriminator(name="DTYPE")
public abstract class Item {
  @Id @GeneratedValue
  @Column(name="ITEM_ID")
  private Long Id;
  
  ...
  
  }
  
@Entity
@DiscriminatorValue("Book")
@PrimaryKeyJoinColumn(
public class Book extends Item {
  private Type anotherAttribute1;
  private Type anotherAttribute2;
  ...
  }

```

+ 단일 테이블 전략

Album, Book과 같은 자식 엔티티를 개별 테이블로 관리하는 것이 아닌 하나의 테이블로 관리하는 전략이다. 

데이터베이스에 하나의 테이블에 모든 애트리뷰트가 포함되어 있다. 그리고 DTYPE 칼럼으로 구분된다. 만약, Book 엔티티가 저장된다면 Album에 해당되는 애트리뷰트는 Null 처리된다.

조인이 적어 쿼리 시 성능의 이점이 있지만, 많은 Null 값을 저장해야 하므로 저장공간 낭비가 존재한다. 

```java
@Entity
@Inheritence(strategy = SingleTable) // 이 부분만 달라짐.
@Discriminator(name="DTYPE")
public abstract class Item {
  @Id @GeneratedValue
  @Column(name="ITEM_ID")
  private Long Id;
  
  ...
  
  }
  
@Entity
@DiscriminatorValue("Book")
@PrimaryKeyJoinColumn(
public class Book extends Item {
  private Type anotherAttribute1;
  private Type anotherAttribute2;
  ...
  }

```

+ @MappedSuperclass

 공통의 애트리뷰트를 다른 엔티티(테이블)에 전달하는 용도로 만들어진 클래스이다. @MappedSuperClass는 데이터베이스의 테이블로 저장되지 않는다.

```java
  @MappedSuperclass
  public abstract class BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
    ...
    }
    
    @Entity
    @AttributeOverrides(
      @AttributeOverride(name= "id", column = @Column(name = "MEMBER_ID")),
      @AttributeOverride(name= "name", column = @Column(name = "MEMBER_NAME"))
    public class Member extends BaseEntity {
      ...
    }
 ```

@MappedSuperClass 정보를 오버라이딩하려면 @AttributeOverrides, @AttributeOverride를 사용하면 된다.

 
 **@MappedSuperClass와 @Embeddable의 차이점은?**
 
 @MappedSuperClass는 Entity로 클래스를 관리하는 방법이고, @Embeddable은 Value로 클래스를 관리하는 방법이다.
 
 전자는 상속을 통해서 사용되고, 후자는 위임을 통해 사용되는 방식이다.
 
 ### 3.6 복합 키 
 복합 키란 2개 이상의 애트리뷰트로 이루어진 기본 키를 의미한다. @IdClass, 또는 @Embeddable로 구현할 수 있다.
 
+ 복합키 생성 방법
```java
public class MemberId implements Serializable {
  private String id1;
  private String id2;
  
  public MemberId() {}
  public MemberId(String id1, String id2) {
    this.id1 = id1;
    this.id2 = id2;
    }
  @Override
  public boolean equals(Object o) { ... }
  @Override
  public int hashCode() { ... }
  }
  
  // 기본 생성자, eqauls, hashCode 멤버함수는 반드시 정의되어야 한다.
  
@Entity
@IdClass(MemberId.class)
public class Member {
  @Id
  @Column(name="MEMBER_ID1")
  private String id1;
  
  @Id
  @Column(name="MEMBER_ID2")
  private String id2;
  
  ...
  
  }
  
```

  ```java
@Embeddable // <-----
public class MemberId implements Serializable {
  @Column(name="MEMBER_ID1")
  private String id1;
  @Column(name="MEMBER_ID2")
  private String id2;
  
  public MemberId() {}
  public MemberId(String id1, String id2) {
    this.id1 = id1;
    this.id2 = id2;
    }
  @Override
  public boolean equals(Object o) { ... }
  @Override
  public int hashCode() { ... }
  }
  
  // 기본 생성자, eqauls, hashCode 멤버함수는 반드시 정의되어야 한다.
  
@Entity
public class Member {
  @Embedded // <---------
  private MemberId id;
  ...
  
  }
```

@IdClass는 DB지향적인 복합키 생성이다. 왜냐하면, 엔티티에서 PK가 무엇인지 바로 알 수 있기 때문이다.

@Embeddable은 객체지향적인 복합키 생성이다. 왜냐하면, PK가 클래스 객체로 되어 있기 때문이다.

복합 키 생성 시 어떤 방식을 사용해도 문제 없다.

그러나, @EmbeddableId에서 JPQL이 더욱 길어질 수 있다. 왜냐하면, PK로 조인할 때, 경로 표현식을 통해 PK(객체)를 참조해야 하기 때문이다.

+ **왜 equals(), hashCode() 멤버함수를 구현해야 할까?**

기본적으로 equals() 멤버함수는 동일성(==) 비교를 한다. 개발자가 Override하지 않으면 값이 아닌 주소 비교를 하게 되므로 값이 같아도 false를 반환하게 된다.

따라서, **동등성 비교(값이 같은지를 비교)** 를 위해서 equals()와 hashCode() 멤버함수를 생성하는 것이 필요하다.


### 3.7 식별관계 매핑
**식별관계란 부모로부터 상속받은 기본키(외래키)를 자식에서 기본 키로 사용하는 관계를 의미하고, 비식별관계란 부모로 부터 상속받은 기본키를 외래키로만 사용하는 관계를 의미한다. 식별관계 매핑을 통해 상속 관계를 표현할 수 있다.**

복합 키를 갖는 식별관계를 어떻게 매핑할 것인지 알아본다. 부모, 자식, 손자 엔티티가 있다고 하자. 부모의 기본 키는 (부모id), 자식의 기본 키는 (부모 id, 자식 id), 손자의 기본 키는 (부모 id, 자식id, 손자id)라고 하자. 아래와 같이 @IdClass를 통해 표현할 수 있다.

```java
@Entity 
public class Parent {
  @Id @GeneratedValue
  @Column(name="PARENT_ID")
  private Long id;
  ...
  }

@Entity
@IdClass(ChildId.class)
public class Child {
  @Id
  @ManyToOne
  @JoinColumn(name="PARENT_ID")
  private Parent parent;
  
  @Id @Column(name="CHILD_ID")
  private String childId;
  ... 
  }
  
public class ChildId implements Serializable {
  private String parent;
  private String child;
  ...equals(), ... hashCode()
  }

@Entity
@IdClass(GrandChildId.class)
public class GrandChild {
  @Id @ManyToOne
  @JoinColumns({
    @JoinColumn(name="CHILD_ID"),
    @JoinColumn(name="PARENT_ID")
    })
  private Child child;
  
  @Id @Column(name="GRANDCHILD_ID")
  private String id;
  ...
  }

public class GrandChildId implements Serializable {
  private ChildId child;
  private String grandChild;
  ... equals(), ...hashCode()
  }
```

유사한 방법으로 @EmbeddedId(PK)와 @MapsId(FK)를 통해서도 구현이 가능하다. 

```java
@Entity
public class Child {
  @EmbeddedId
  private ChildId childId;

  @MapsId("parentId")
  @ManyToOne
  @JoinColumns(name = "PARENT_ID")
  private Parent parent;
}

@Entity
public class Parent {
  @Id
  @Column(name = "PARENT_ID")
  private Long id;
}
```

그러나, 식별 관계 매핑보다는 비식별 관계 매핑이 더욱 선호된다.
+ 식별 관계 사이에 조인해야 할 기본 키가 불필요하게 많아 질 수 있다.
+ 자식 엔티티가 존재하기 위해서는 반드시 부모 엔티티가 존재해야 한다. 왜냐하면 기본 키에 Null이 존재할 수 없기 때문이다. 그러나, 비즈니스 로직이 변화함에 따라 테이블 구조는 유연하게 변화할 수 있어야 한다.

### 3.8 조인 테이블

엔티티 테이블 간 조인을 위해서는 조인 칼럼을 사용하는 방법도 있지만, 조인 테이블을 사용할 수 있다. 주로 N:N 관계에서 많이 사용된다.

+ 1:1 관계 조인 테이블(ex. 학생과 사물함)
```java
@Entity
public class Member {
  @Id @Column(name = "MEMBER_ID")
  private Long id;

  @OneToOne
  @JoinTable(name = "MEMBER_LOCKER"
        JoinColumns = @JoinColumn(name = "MEMBER_ID"),
        InverseJoinColumns = @JoinColumn(name = "LOCKER_ID")
  )
  private Locker locker;
}
```

+ N:N 관계 조인 테이블(ex. 회원이 여러 팀에 소속될 수 있음)
```java
public class Member {
  @Id @Column(name = "MEMBER_ID")
  private Long id;

  @ManyToMany
  @JoinTable(name = "MEMBER_TEAM",
          JoinColumns = @JoinColumn(name = "MEMBER_ID"),
          InverseJoinColumns = @JoinColumn(name = "TEAM_ID")
  )
  private List<Team> teamList = new ArrayList<>();
}
```


***
## 4. 프록시와 연관관계 관리
**프록시란 문자 그대로 대리인이라는 의미를 갖는다. 예를 들어, 회원 엔티티와 팀 엔티티가 N:1 연관관계에 있다고 하자. JPA에서는 회원 엔티티를 검색하면 팀 엔티티와까지 조회하는 쿼리가 생성된다.(eager loading) 그러나, 사용자는 팀 엔티티에 대한 정보를 얻는 것을 원하지 않을 수 있다.**

**이러한 경우에 팀 엔티티 객체 실체를 영속성 컨텍스트에 불러오지 않고 프록시 객체를 불러온다. 사용자는 프록시 객체를 통해 회원 엔티티와 연관된 팀 엔티티에 대한 정보를 필요할 때만 요청할 수 있다. 프록시를 통해 실제 엔티티에 접근하는 것을 대신한다는 점에서 의미가 통한다.**

```java
void main() {
  EntityManager em = EntityMangerFactory.createEntityManager();
  
  // 1. 엔티티 자체 조회
  Member member1 = em.find(ID, Member.class);
  
  // 2. 엔티티 프록시 조회
  Member member2 = em.getReference(ID, Member.class);
  }
```

### 4.1 지연로딩과 즉시로딩
**즉시로딩이란 엔티티를 조회할 때 연관된 모든 엔티티를 함께 조회하는 것을 의미하고, 지연로딩은 엔티티 조회 후, 연관된 엔티티는 실제 사용할 때만 로딩하는 것을 의미한다.**

```java
  @Entity
  public class Member {
    ...
    @ManyToOne(fetch=LAZY) // 지연 로딩
    ...
    
    @MantyToOne(fetch=EAGER) // 즉시 로딩
    
   }
```

가급적이면, 지연로딩을 사용하는 것이 바람직하다. 왜냐하면, 즉시로딩은 N+1 문제를 일으킬 수 있다.

+ **즉시로딩 시 발생하는 N+1 문제란?**

아래와 같은 JPQL을 실행했다고 가정하면,

```java

List<Member> members = em.createQuery("select m from Member m", member.class).getResultList();

```

기대되는 SQL은 아래와 같다.

```SQL
select *
from Member
```

만약, Member가 즉시로딩이라면, 조회된 Member의 개수만큼 연관 엔티티를 찾기 위해 아래의 쿼리가 발생한다. 

**이처럼, 처음 조회된 수 만큼 새로이 쿼리가 발생하는 것을 N+1 문제라고 한다.**
```SQL
select * 
from ORDERS
WHERE MEMBER_ID = ?
```

물론, 지연 로딩일 때도 N+1 문제가 발생할 수 있지만, 추후 다루도록 한다.
    

### 4.2 영속성 전이: CASCADE
**특정 엔티티를 영속 상태로 만들고자 할 때, 연관 엔티티까지 모두 영속 상태로 만들 때 사용하는 기법**
```java
@Entity
public class Parent {
  ...
  @OneToMany(mappedby="parent", fetch=LAZY, cascade=persist)
  private List<Child> children = new ArrayList<>();
  ...
  }
  
@Entity
public class Child {
  ...
   @ManyToOne(fetch=LAZY)
   @JoinColumn(name="PARENT_ID")
   private Parent parent;
   ...
   }
   
void main() {
  Parent parent = new Parent();
  Child child1 = new Child();
  Child child2 = new Child();
  
  child1.setParent(parent);
  child2.setParent(parent);
  
  parent.getChildren.add(child1);
  parent.getChildren.add(child2);
  
  em.persist(parent); // 자식 엔티티까지 모두 영속된다.
  }
```

cascade 옵션으로는 persist, remove, 그리고 all이 있다.


### 4.3 고아객체
**부모 엔티티와 연관관계가 끊긴 자식 엔티티를 자동으로 삭제하는 기법이다.**

그러므로, 참조되는 곳이 하나일 때만 사용 가능하다. (@OrphanRemoval=true (ex. OneToMany, OneToOne))

***

## 5. 값 타입
엔티티와 값 타입을 혼동해서는 안된다. 식별자를 통해 추적이 필요한 것은 엔티티이다. 

### 5.1 기본 값 타입
기본 값 타입에는 int, double과 값은 기본 타입, Integer와 같은 래퍼 클래스, 그리고 String이 있다. 기본 값 타입은 공유되지 않는다.

### 5.2 임베디드(Embedded) 타입
여러 클래스(엔티티)에서 공유하는 특성을 모아서 만든 값 타입이다. @Embeddable 애노테이션을 사용한다. **임베디드 타입에는 반드시 기본 생성자가 필요하다.**

@Embedded 타입을 잘 활용하면 엔티티 더욱 객체지향적으로 설계가 가능하다. 잘 구현된 ORM 애플리케이션은 테이블보다 엔티티 클래스의 수가 더 많다.

예를 들어, 아래의 Address @Embeddable 타입은 Member 엔티티 뿐만 아니라 다른 엔티티에도 사용될 수 있다. 사용 방법은 아래와 같다.

```java
@Embeddable
public class Address {
  private String street;
  private String city;
  private String state;
  }

@Entity
public class Member {
  ...
  @Embedded
  private Address address;
  /*
    Member 클래스에 street, city, state가 주입된다.
  */
  ...
  }
```

@Embeddable - @Embedded 타입은 연속적으로 연관관계를 맺으며 매핑될 수 있다. (ex. Zipcode Embedded in Address, Address Embedded in Member) 

 + @Embeddable과 @MappedSuperclass의 차이점은?
 
 위임(has)과 상속(is)의 차이이다. 기능 상 차이는 없지만 JPQL 호출 시 위임의 경우에 쿼리가 더 길어질 수 있다.
 
 + 동일한 @Embeddable 타입을 2개 이상 오버라이딩을 통해 주입할 수 있다. 예를 들어, Member에 homeAddress, companyAddress가 embedded되는 경우 
```java
@AttributeOverrides({
  @AttributeOverride(name="...", column=@Column(name="...")), 
  ...
  })
  // 기본 칼럼명(name="...")을 column=@Column(name="...")으로 변경한다는 것을 의미한다.
```

+ @Embeddable 타입과 같은 특성을 가진 객체 타입을 공유하도록 만들면 위험하다.
 
서로 다른 두 객체가 하나의 객체를 참조한다면, 한쪽에서 수정한다면 다른 쪽도 변경되는 문제가 발생한다.

마찬가지로 @Embeddable 타입도 서로 다른 엔티티가 공유하면 동일한 문제가 발생한다.

이러한 문제를 컴파일러 레벨에서 막을 방법은 없다.(MyClass a = b;와 같은 문법을 막을 수 없음)

이를 해결하기 위해서 setter를 생성하지 않아서 객체를 불변상태로 만들면 된다.


### 5.3 값 타입 컬렉션

동일한 타입의 값을 1개 이상 저장하기 위해서는 Java Collection(List, Set), @ElementCollection, @CollectionTable 애노테이션을 사용한다.

값 타입 컬렉션은 엔티티의 키와 값 타입의 속성을 기본 키로 하는 새로운 테이블로 생성된다. 또한, CASCADE과 고아객체 특성을 갖는다.

또한, 값 타입 컬렉션을 수정 또는 삭제하면, 값 타입은 엔티티처럼 식별자가 없기 때문에 컬렉션을 모두 삭제하고 다시 삽입하는 쿼리가 발생한다. 


+ 일대다 매핑과 값 타입 컬렉션의 차이점은? 
 
값 타입 컬렉션을 수정하면, 삽입을 위한 많은 쿼리가 발생한다는 것을 알았다. 그러므로, 저장된 정보가 많은 컬렉션은 일대다 매핑으로 전환할 것도 고려해야 한다.
  

***

## 6. JPQL
**JPQL이란 SQL을 추상화한 객체지향적 쿼리 언어이다. SQL에서 지원하는 것들을 대부분 지원하여 매우 유사하다.**

SELECT, FROM, WHERE, GROUPBY, HAVING, ORDER BY, JOIN, Aggregate 함수 모두 지원한다. UPDATE와 DELETE는 지원하지만 INSERT는 지원하지 않는다.

서브쿼리 또한 지원하지만, FROM 절에서는 불가능하다.

### 6.1 JPQL의 다양한 사용법

```java
  void main() {
    EntityManager em = EntityManagerFactory.createEntityManager();
    
    String query1 = "select m From Member m"
    
    // 1. TypeQuery<> 객체로 반환
    TypeQuery<Member> result1 = em.createQuery(query1, Member.class);
    
    // 2. Query 객체로 반환
    Query result2 = em.createQuery(query1);
    
    // 3. ArrayList<>로 반환
    ArrayList<Member> result3 = em.createQuery(query1, Member.class).getResultList();
    
    // 4. 객체 반환(결과가 반드시 1개여야 한다. 그렇지 않으면 Exception)
    Member member = em.createQuery(query1, Member.class, Member.class).getSingleResult();
     
    // 5. 파라미터 바인딩
    String nameParameter = "kim";
    ArrayList<Member> result4 = em.createQuery("select m from Member m where m.name = :name, Member.class)
                                    .setParameter("name", nameParameter).getResultList();
                                    
    }
```

### 6.2 프로젝션
**SELECT절에서 조회할 대상을 지정하는 것을 의미한다. 여러개 지정도 가능하다.**

ex) Select m.name from Member m

```java
  void main() {
    ...
    String query = "select m.name, m.age from Member m";
    
    // 1. Query 객체로 반환
    Query result1 = em.createQuery(query);
    
    // 2. Obejct[]로 반환
    List<Object[]> result2 = em.createQuery(query).getResultList();
    
    // 3. new 명령어를 통해 새로운 DTO 생성 후 반환
    List<memberDTO> result3 = new ArrayList<>();
    for(Object[] o : result2) {
      result3.add(new memberDTO((String)o[0], (Integer)o[1]);
      }
      
    List<MemberDTO> result4 = em.createQuery("select new mainDir.MEmberDTO(m.name, m.age) from Member m")
                                    .getResultList();
                                    
    }
    
    // 4. 페이징 API
    List<Member> result5 = em.createQuery("select m from Member m ORDER BY m.age DESC", Member.class)
                                .getFirstResult(10).getMaxResult(20).getResultList();
               
      
```

### 6.3 JPQL에서의 조인
**SQL에서 inner, left outer, right outer, full outer 조인이 있다. 이러한 조인들이 JPQL에서는 어떠한지 알아보도록 한다.**

+ 내부 조인
```java
  String query = "select m, t from Member m inner join m.team t on t.name = \"A\" ";
```

위의 JPQL은 아래의 SQL로 변환된다.

```sql
  SELECT m.*, t.*
  FROM MEMBER m inner join Team t on m.TEAM_ID = t.TEAM_ID
  WHERE t.name = "A"
```

외부 조인 또한 위와 비슷한 방식으로 할 수 있다.

### 6.4 경로 표현식
경로 표현식은 상태 필드(속성), 단일 값 연관 필드(N->1), 그리고 컬렉션 값 연관 필드(1->N) 3가지가 있다. 

+ 상태 필드 경로 탐색에 대한 설명은 생략하도록 한다. 단순히, 엔티티의 애트리뷰트르 찾는 것을 담당한다.

+ 단일 값 연관 필드(N:1, 1:1)

단일 값 연관 필드는 묵시적 조인이 발생한다. 계속 탐색할 수 있다. 코드로 확인하면 아래와 같다.

```java
  String sql = "Select o.member from Orders o;
```

위의 JPQL은 아래의 SQL로 변환된다.

```sql
  SELECT m.*
  FROM orders o inner join Members m on o.member_id = m.member_id
```

묵시적 조인이 일어난다는 것을 알 수 있다. 만약, JPQL에서 명시적으로 조인하는 쿼리문을 짜면 아래와 같다.

```java
  String sql = "select m from Member m inner join m.Orders o";
```

묵시적 조인보다는 명시적 조인이 예상하지 못한 쿼리를 막을 수 있다. 그러므로, 단일 값 연관 필드보다는 명시적 조인이 더 바람직하다.


+ 컬렉션 값 연관 필드(1:N, N:N)


컬렉션 값 연관 필드는 경로 탐색이 안된다는 것을 잊으면 안된다. 마찬가지로 묵시적 조인이 일어나지만 더 이상의 경로 탐색은 불가능하다.


### 6.5 페치 조인(Fetch Join)
페치 조인은 SQL에는 없는 개념이다. 페치 조인은 SQL 호출 횟수를 줄여 최적화하기 위해 사용되는 기법이다. 

연관된 엔티티나 컬렉션을 같이 조회하는 기능이다.

예시를 통해 알아보도록 한다.

+ 엔티티 페치 조인

아래와 같은 JPQL에서의 페치 조인이 있다고 하자. 

```java
  void main() {
    String sql = "select m from Member m join fetch m.team";
    }
```
SQL로 변환되면 아래와 같다.
```sql
  SELECT M.*
  FROM Member M inner join Team T on M.TEAM_ID = T.TEAM_ID
```

위와 같은 페치 조인을 통해 조인된 Member와 Team이 영속성 컨텍스트에 존재하게 된다.

+ 컬렉션 페치 조인

아래와 같은 JPQL이 있다고 하자.
```java
  void main() {
    String sql = "select t from Team t join fetch t.members Where t.name='A' ";
   }
```
위는 SQL로 아래와 같이 번역된다.
```sql
  SELECT T.*
  FROM TEAM T inner join Member M on T.TEAM_ID = M.TEAM_ID 
  WHERE T.NAME = 'A'
```

페치 조인 결과, TEAM과 inner join된 Member가 영속성 컨텍스트에 존재하게 된다. 

물론, TEAM과 Member는 (1:N) 관계이므로 Team은 중복되어 영속성 컨텍스트에 존재한다. 이를 막기 위해 DISTINCT 예약어를 사용할 수 있다.

+ **일반 조인과 페치 조인의 차이점은?**

아래의 두 JPQL을 비교해보도록 하자.
```java
  void main() {
    String sql1 = "SELECT m FROM Member M inner join m.team";
    String sql2 = "SELECT m FROM Member M join fetch m.team";
    ...
   }
```

번역되는 SQL 자체만 비교했을 때, 두 JPQL 쿼리는 차이가 없다. 그러나, 쿼리에 따른 영속성 컨텍스트 상태는 다르다.

Member와 Team 모두 지연로딩이라고 가정하자.

전자의 경우, 영속성 컨텍스트에 Member만 로딩된다. Team은 Join을 위해서만 사용된다.

후자의 경우, 영속성 컨텍스트에 Member와 Team 모두 로딩된다.

### 6.6 N+1 문제란?

+ 즉시로딩에서의 N+1 문제: select m.team from Member m과 같은 JPQL을 발생시키고, team이 즉시로딩이라면 조회되는 member 수 만큼 team에 대한 쿼리가 발생한다. 이를 즉시로딩에서의 N+1 문제라고 한다. 이는 지연로딩을 통해 막을 수 있다.

+ 지연로딩에서의 N+1 문제: select m from Member m JPQL 발생 시, 지연로딩이라면 Member에 대한 쿼리만 발생한다. 그러나, 모든 Member에 대해서 아래와 같은 JPQL을 실행했다고 하자.
```java
List<Member> members = memberService.findAll(); // 지연로딩으로 모든 Member 조회 (쿼리 1번)
for(Member member : members) {
  System.out.println(member.getOrders().size()); // 매 member 마다 Order 조회 쿼리 발생(쿼리 N번)  
}
```
그러므로, 지연로딩에서도 N+1 문제가 발생한다.


### 6.7 N+1 문제 해결방법

+ **@XToOne에서의 N+1 문제 해결방법**

 간단하게 Fetch Join을 이용해서 해결할 수 있다.
 
 
+ **@OneToMany에서의 N+1 문제 해결방법**

위와 동일하게 Fetch Join을 이용해 해결할 수 있다. 그러나 데이터가 실제보다 더 뻥튀기 되어 검색되는 결과가 발생한다.

```java
  Member m1 = new Member("kim");
  Member m2 = new Member("lee");
  Team t1 = new Team("korea");
  
  m1.setTeam(t1);
  m2.setTeam(t2);
  teamRepository.save(t1);
  memberRepository.save(m1);
  memberRepository.save(m2);
  
  // ============
  
  List<Team> teamList = em.createQuery("select t from team t join fetch t.members").resultList();
  // (korea - lee), (korea - kim) 두 레코드가 쿼리 결과로 나오므로 teamList에는 {korea, korea}가 바인딩됩니다.
```
물론, JPQL에 distinct 키워드를 방지해서 데이터가 중복해서 나오는 문제를 막을 수 있다. 

그러나, 페이징이 불가능하다. 

그러므로, @BatchSize를 사용해 SQL의 IN 예약어를 활용함으로써 발생하는 쿼리의 수를 최적화할 수 있다.

결론은 @OneToMany에서는 즉시 로딩과 @BatchSize를 통해서 타협점을 찾을 수 있다는 것이다.

***


## 7. 스프링 데이터 JPA

스프링 데이터 JPA는 데이터 접근 계층 클래스에서의 반복되는 CRUD를 해결하는 기술이다. 아래와 같이 인터페이스를 구현해서 완성할 수 있다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> { }
```

### 7.1 쿼리 메소드 기능

#### 7.1.1 메소드 이름으로 쿼리 생성

엔티티 필드 이름을 이용하여 쿼리를 생성할 수 있다.

예를 들어, 회원 이름(필드명 : username)을 기준으로 회원 엔티티를 조회하는 JPA 쿼리는 아래와 같다.

```java
List<Member> findByUsername(String username) {
  return em.createQuery("select m from Member m where m.username = :username", Member.class).getResultList();
}
```

만약 스프링 데이터 JPA를 사용하면 아래처럼 간략하게 나타낼 수 있다. 이를 스프링 데이터 JPA의 메소드 이름을 통한 쿼리 생성 기능이라고 한다.
```java
List<Member> findByUsername(String username);
```

이외에도 다양한 방법을 통해 메소드 이름을 통한 쿼리 생성이 가능하다.

참고 공식 문서 : https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation

```java
List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
```

#### 7.1.2 @Query를 통한 쿼리 직접 정의

메소드 이름을 통한 쿼리 생성 기능은 조건이 많아질수록 메소드 이름이 길어지는 단점이 있다.

그 대신에 @Query를 사용할 수 있다.

```java
@Query(value = "select m from Member m where m.username = :username and m.age > :age)
List<Member> findByUsernameAndAge(@Param("username") String username, @Param("age") int age);
```

메소드 이름을 통한 쿼리 생성, @Query 모두 컴파일 시점에 SQL 문법 오류를 찾을 수 있는 것이 장점이다.

#### 7.1.3 유연한 반환 타입

동일한 JPQL도 객체, 컬렉션, Optional로 다양하게 결과를 return 받을 수 있다.

다만, JPQL 조회 결과가 0건이고 컬렉션 반환 타입인 경우에는 null이 아닌 빈 컬렉션이 반환되므로 주의한다.

#### 7.1.4 순수 JPA 페이징과 정렬

페이징이란 limit(한 페이지에 보여줄 데이터 수)와 offset(원하는 페이지 번호)을 이용하여,

대용량의 데이터를 조각으로 불러오는 기술이다. 게시판 블로그 기능에서 사용된다.

주로 정렬 기능과 함께 사용되며 순수 JPQL로 작성하는 예시는 아래와 같다.

```java
List<Member> findMemberByPage(int age, int offset, int limit) {
  return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
            .setParameter("age", age)
            .setFirstResult(offset).setMaxResults(limit).getResultList();
}
```

#### 7.1.5 스프링 데이터 JPA 페이징과 정렬 

Pageable, Sort 인터페이스를 통해 페이징 및 정렬 기능을 사용할 수 있다. 예시는 아래와 같다. 

```java
Page<Member> findByUsername(String username, Pageable pageable); // count 쿼리 사용
Slice<Member> findByUsername(String username, Pageable pageable); // count 쿼리 사용 안함
```

페이지당 데이터를 3개씩 갖고, 0번째 페이지를 가져오는 코드는 아래와 같다.

```java
PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
Page<Member> members = memberRepository.findByUsername("member", pageRequest);
```

Page<T> 반환은 count 쿼리와 페이지 쿼리가 함께 발생한다.

Slice<T> 반환은 페이지 쿼리만 발생하지만, 내부적으로 limit+1 만큼의 데이터를 반환한다. (모바일 리스트 \<더보기> 기능에 활용) 

추가적으로 map을 통해 page 쿼리 결과를 DTO에 바인딩하고, count 쿼리를 직접 작성할 수도 있다.

```java
Page<Member> members = memberRepository.findByUsername("member", pageRequest);
Page<MemberDto> memberDtos = members.map(m -> new MemberDto()); // DTO 바인딩
```

```java
@Query(value = "select m from Member m", countQuery = "select count(m) from Member m")
Page<Member> findAll(Pageable pageable)
```

### 7.1.6 벌크성 수정 쿼리

순수 JPA를 사용한 벌크성 수정 쿼리, Spring data JPA를 사용한 벌크성 수정 쿼리는 각각 아래와 같다.

```java
public int bulkAgePlus(int age) { // 순수 JPA
  return em.createQuery("update Member m set m.age = m.age+1 where m.age >= :age")
            .setParameter("age", age).executeUpdate();
}

@Modifying(clearingAutomatically = true)
@Query("update Member m set m.age = m.age+1 where m.age >= :age")
int bulkAge(int age);
```

Spring Data JPA에서의 벌크성 수정, 삭제 쿼리는 반드시 @Modifying이 필요하고,

영속성 컨텍스트에 남기지 않고 직접 DB를 조작하므로 dirty check에 유의해야 한다.

(벌크성 쿼리 이후에는 영속성 컨텍스트를 초기화 하는 것이 안전하다.)

### 7.1.7 엔티티 그래프

EntityGraph란 연관된 엔티티를 한번에 조회하는 방법이다.

fetch join을 통해서 Lazy Loading에서 연관된 엔티티를 불러올 수 있다.

즉, EntityGraph는 left fetch join을 내부적으로 사용하므로 fetch join의 간편 버전이라고 봐도 된다.

```java
// Spring data jpa 공통 메소드 오버라이드
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();

// JPQL + Entity Graph
@Query("select m from Member m")
@EntityGraph(attributePaths = {"team"})
List<Member> findAllMemberByEntityGraph();

// spring data jpa 메소드 이름 쿼리 + Entity Graph
@EntityGraph(attributePaths = {"team"})
List<Member> findByUsername(String username);
```

### 7.1.8 JPA Hint & Lock

JPA 구현체에게 제공하는 힌트와 트랜잭션 락 기능을 제공한다.

힌트는 대표적으로 readOnly가 있고, 락은 비관적 락과 낙관적 락을 제공한다.

트랜잭션 락에 대한 자세한 내용은 아래를 참고한다

https://github.com/chrismrkr/WIL/blob/main/CS/database.md#3-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EA%B4%80%EB%A6%AC

https://github.com/chrismrkr/WIL/blob/main/JPA/JPASummary.md#8-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%EA%B3%BC-%EB%9D%BD-2%EC%B0%A8-%EC%BA%90%EC%8B%9C

```java
// readonly는 영속성 컨텍스트를 생성하지 않음
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = true)) 
Member findReadOnlyByUsername(String username);
```

락 기능은 @Lock으로 제공된다. 

## 7.2 확장 기능

### 7.2.1 사용자 정의 레포지토리

JpaRepository는 인터페이스이므로 메소드를 직접 구현하려면 너무 많은 기능을 구현해야 한다.

그래서, 사용자 정의 레포지토리가 필요하며 사용자 정의 메소드를 구현하는 경우는 아래와 같다.

+ JDBC API 사용
+ JPA 직접 사용
+ MyBatis 사용
+ QueryDsl 사용

사용자 정의 메소드를 구현하는 방법은 아래와 같다.

1. 인터페이스에 사용자 정의 메소드를 정의한다.

```java
public interface MemberCustomRepository { ... }
```

2. 정의한 메소드를 구현한 사용자 정의 클래스를 생성한다.

```java
public class MemberCustomRepositoryImpl implements MemberCustomRepository { ... }
```

3. JPARepository에 인터페이스를 상속한다.

```java
public class MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository { ... }
```

**사용자 정의 클래스 이름은 항상 '리포지토리 인터페이스 이름' + 'impl'이 되어야한다.**

추가로 항상 사용자 정의 레포지토리가 필요한 것은 아니다.

사용자 정의 리포지토리 대신에 새로운 레포지토리를 생성하여 이를 Bean으로 등록하여 사용하는 방법도 있다.

### 7.2.2 Auditing

모든 엔티티에 공통 필드를 적용할 때 사용한다.

예를 들어, 엔티티 변경 이력 추적을 위해 등록 시간, 변경 시간을 모든 엔티티 필드에 저장해야 한다고 하자.

#### 1. 순수 JPA에서는 아래와 같이 구현할 수 있다.

```java
@MappedSuperClass
public class BaseEntity {
	@Column(updatable = false)
	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;
	
	@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createdTime = now;
		this.updatedTime = now;
	}
	@PreUpdate
	public void preUpdate() {
		this.updatedTime = LocalDateTime.now();
	}
  // @PostPersist, @PostUpdate도 존재함.
}

@Entity
public class Member extends BaseEntity { // 상속
}
```
#### 2. Spring Data Jpa에서는 아래와 같이 사용한다

```java
@MappedSuperClass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdTime;
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;
}
```

Spring Data Jpa에서는 @CreatedDate(등록일), @LastModifiedDate(수정일) 뿐만 아니라 @CreatedBy(등록자), @LastModifiedBy(수정자)도 제공한다.

```java
@MappedSuperClass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdTime;
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	@CreatedBy @Column(updateable = false)
	private String createdBy;
	@LastModifiedBy
	private String lastModifiedBy;
}
```

**Spring Data Jpa Auditing 기능을 사용하려면 Main에 반드시 @EnableJpaAuditing을 등록해야한다.**

```java
@SpringBootApplication
@EnableJpaAuditing
public class Application {
	public static void main(String[] args) { ...}
}
```

등록자, 수정자 기능도 사용하려면 아래와 같이 Bean으로 등록한다. @SpringBootApplication에도 Bean을 등록이 가능하다.

```java
@EnableJpaAuditing
@SpringBootApplication
public class DatajpaApplication {
	public static void main(String[] args) {
		SpringApplication.run(DatajpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
```

### 7.2.3 Web 확장 도메인 클래스 컨버터

Http Request Parameter로 넘어온 변수를 컨트롤러에서 이용해 엔티티를 바로 불러올 수 있다.

코드는 아래와 같다.

```java
@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberRepository memberRepository;

	@GetMapping("/members1/{id}") // 기존 코드
	public String getMember(@PathVariable("id") Long id) {
		return memberRepository.findById(id).get().getUsername();
	}

	@GetMapping("/members2/{id}") // Web 확장 도메인 클래스 컨버터
	public String getMember2(@PathVariable("id") Member member) {
		return member.getUsername();
	}
}
```
도메인 클래스 컨버터로 조회시 영속성 컨텍스트는 생성되지 않으므로 주의한다.

### 7.2.4 Web 확장 페이징과 정렬

Spring Data JPA 페이징 및 정렬 기능을 Web(컨트롤러)에 확장하여 사용할 수 있다.

```java

@GetMapping("/members"
public String getUsername(Pageable pageable) {

}
```

***

## 8. 트랜잭션과 락, 2차 캐시

JPA에서의 Isolation level은 영속성 컨텍스트의 특징에 의해 repeatable-read이다.

만약 더 높은 수준의 격리수준이 필요하다면, 낙관적 락 또는 비관적 락을 이용해야 한다.

### 8.1 낙관적 락

대부분의 트랜잭션은 충돌이 일어나지 않을 것이라 가정하는 애플리케이션에서 제공하는 락 기법이다.

Version 정보를 이용하여 특정 칼럼에 낙관적 락을 제공한다. 낙관적 락은 두번의 갱신 내역 분실 문제를 해결할 수 있다. 

두번의 갱신 내역 분실은 아래와 같은 시나리오에서 발생한다.

+ 1. 사용자1이 게시판 수정 버튼 클릭
+ 2. 사용자2가 게시판 수정 버튼 클릭
+ 3. 사용자1이 수정 후 수정 완료 버튼 클릭
+ 4. 사용자2가 수정 후 수정 완료 버튼 클릭

이러한 경우에는 사용자1이 입력한 내용이 사라진다.

두번의 갱신 내역 분실 문제는 데이터베이스에서 제공하는 격리 수준으로 해결할 수 없다.

@Version 애노테이션을 통해 위의 두번의 갱신 내역 분실 문제를 해결할 수 있다. 방법은 아래와 같다.

+ 1. 사용자1이 게시판 수정 버튼 클릭
+ 2. 사용자2가 게시판 수정 버튼 클릭
+ 3. 사용자1이 수정 후 수정 완료 버튼 클릭(version1 -> version2)
+ 4. 사용자2가 수정 후 수정 완료 버튼 클릭(version2로 바뀌었으므로 변경 불가)

@Version 애노테이션을 통해서 특정 칼럼에 대해 낙관적 락을 사용할 수 있지만, 옵션을 사용할 수도 있다.

+ NONE: UPDATE를 할 때, version을 체크한다. dirty read, non-repeatable read 문제가 발생한다.

T1: -----Read(A)----------------Read(A)------------

T2:-----------------Update(A)-------------commit--- \<dirty read>

T1: ------Read(A)----------------------------Read(A)

T2: --------------Update(A)----commit-------------- \<non-repeatable read>
                                                              
+ OPTIMISTIC : UPDATE 뿐만 아니라 SELECT를 할 때도 version을 체크한다. dirty-read, non-repeatable read를 방지한다.
+ OPTIMISTIC_FORCE_INCREMENT : 연관관계에 있는 엔티티의 버전 정보를 강제로 증가시킬 때 사용한다. OPTIMISTIC이 확장된 옵션이다.

만약, 게시물(1)과 첨부파일(N)이 서로 연관관계에 있다고 하자. 만약, 첨부 파일이 update되면 해당 첨부파일의 version은 상승한다.

그러나, 논리적으로 version 정보가 변경되어야 함에도 불구하고 게시물의 version은 변경되지 않는다. 
  
**JPA에서는 영속성 컨텍스트를 통한 repeatable read 격리 수준, 필요한 경우 낙관적 락을 사용할 것을 추천한다.**

### 8.2 비관적 락


***

## 9. 영속성 컨텍스트 관리

스프링에서는 트랜잭션과 영속성 컨텍스트의 범위가 같은 것을 기본 전략으로 한다. 특징은 아래와 같다.

1. 트랜잭션 종료는 영속성 컨텍스 종료를 의미하고, 영속성 컨텍스트에 있던 객체들은 준영속 상태가 된다.
2. @Transactional은 AOP로 구현된 기능이고, 메소드가 정상 종료되면 트랜잭션 commit, 영속성 컨텍스트는 종료된다.
3. 만약 메소드 Exception이 발생하면, 트랜잭션은 rollback되고 영속성 컨텍스트는 그대로 유지된다.(flush 호출하지 않음)
4. 동일한 트랜잭션에서는 동일한 영속성 컨텍스트를 공유한다.

트랜잭션 종료로 인해 영속성 컨텍스트가 종료되면, 영속 객체들은 준영속 상태가 된다.

이에 따라, 지연로딩을 할 수 없다는 문제점이 발생한다. 아래의 예제 코드를 보면 알 수 있다.

```java
@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  
  @Transactional
  public Member findByName(String name) {
      return memberRepository.findByName(name);
  }
}

@Controller
@RequiredArgsConstructor
public class MemberController {
  private final MemberService memberService;
  @GetMapping("/member")
  public String getMember(@RequestParam String name) {
      Member m = memberService.findByName(name);
      m.getTeam(); // 에러 발생. 영속성 컨텍스트가 종료되었으므로 지연로딩 불가능
  }
}
```

이를 해결하기 위해서는 몇가지 방법이 존재한다.

### 9.1 글로벌 Fetch 전략

예를 들어, Member 엔티티에 @ManyToOne으로 조인된 Team을 즉시 로딩(fetch=FetchType.Eager)한다. 

이렇게 하면 트랜잭션이 종료되어 Member가 준영속 상태가 되더라도 Team도 존재하게 된다. 

그러나, 사용하지 않을 수 있는 엔티티를 로딩하거나 N+1 문제가 발생할 수 있다.

### 9.2 Fetch Join 활용

지연 로딩이더라도 Fetch Join을 통해 프록시 객체를 로딩하여 원하는 결과를 얻을 수 있다.

그러나, findMember(), findMemberWithTeam()과 같이 repository 메소드가 무분별하게 늘어날 수 있다는 단점이 있다.

또한, 프레젠테이션 계층(Controller)이 리포지토리 계층을 침범했다는 문제점도 있다.

### 9.3 강제 초기화

Member를 로딩할 때, 영속성 컨텍스트가 종료되기 전 member.getTeam()과 같은 메소드로 강제로 불러오는 방법도 있다.

이처럼, 강제로 초기화하는 계층을 FACADE 계층이라고 한다. FACADE가 추가하여,

Controller -> FACADE(@Transactional) -> Service -> Repository 계층 구조로 변경할 수 있다.

### 9.4 OSIV

결국, 모든 문제는 프레젠테이션 계층에서 영속성 컨텍스트가 종료되어 엔티티가 준영속 상태가 되었기 때문에 발생했다. 적절한 타협점이 필요하다.

프레젠테이션 계층까지 영속성 컨텍스트를 살려두는 전략을 OSIV(Open Session in View)라고 한다.

과거 OSIV는 request가 들어올 때 Filter나 Interceptor를 통해 영속성 컨텍스트를 생성하고, 트랜잭션이 종료될 때 영속성 컨텍스트를 종료하는 방식을 사용했다. **이를 요청 당 트랜잭션 OSIV 방식이라고 한다.**

이 방법을 통해 request-response가 끝나기 전에 영속성 컨텍스트가 종료되는 것을 막을 수 있었다.

그러나, 아래와 같이 프레젠테이션 계층에서 엔티티가 변경된다면, 트랜잭션이 끝날 때 변경 감지로 변경사항이 실제 엔티티에 반영되는 문제점이 발생한다.

```java
@Controller
public class MemberController {
    ...
    public String viewMember(Long id) {
        Member m = memberService.getMember(id);
        m.setName("aaa"); // 변경감지로 DB에 반영되어 문제가 된다.
        ...
    }
}
```

이를 막기 위해서 DTO를 사용하는 방법도 있지만, 코드량이 매우 늘어난다는 단점이 있다.

### 9.5 스프링 OSIV

스프링 OSIV는 프레젠테이션-서비스-레포지토리 계층 중, 서비스 계층이 종료되면 트랜잭션은 종료하나 영속성 컨텍스트는 살려두는 전략을 사용한다.

영속성 컨텍스트는 트랜잭션 없어도 조회는 가능하므로 지연로딩이 가능하다.

그리고, request-response가 종료되면 영속성 컨텍스트도 종료된다. 이때, 영속성 컨텍스트는 flush되지 않으므로 DB에 반영되지도 않는다.

JPA 서블릿 필터에 OSIV를 적용하려면 ```OpenEntityInViewFilter```를 사용하고, 인터셉터에 OSIV를 적용하려면 ```OpenEntityManagerInViewInterceptor```를 사용하면 된다.
  
**한가지 주의사항**은 아래와 같이 영속성 컨텍스트가 살아있는 상태에서, 새로운 서비스를 호출하는 경우이다.

```java
class MemberController {
  ...
  public String viewMember(Long id) {
      Member m = service.getMember(id);
      m.setName("XXX");
      
      service.biz(); // <- 이 부분에서 문제가 발생한다.
      ...
  }
}
```

영속성 컨텍스트는 JPQL이 실행되면 자동 flush되어 DB에 반영된다.

이에 따라 기존의 영속성 컨텍스트는 flush되고 변경감지에 의해 DB에 변경 내용이 반영되는 문제가 발생한다.

그러므로, 트랜잭션보다 영속성 컨텍스트의 범위가 넓고 여러 트랜잭션이 공유되는 상황(OSIV)에서는 편리하지만 주의가 필요하다.
