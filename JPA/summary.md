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

 공통의 애트리뷰트를 다른 엔티티(테이블)에 주입(상속)할 수 있도록 만들어진 클래스이다. 실제 데이터베이스의 테이블로 저장되는 클래스는 아니다.

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
 
 ### 3.6 복합 키 
 **복합 키란 2개 이상의 애트리뷰트로 이루어진 기본 키를 의미한다.**
 
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

**물론, @EmbeddedId를 통해 더 객체지향적인 방법으로 설계가 가능하다.**

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

복합 키 생성 시 어떤 방식을 사용해도 문제 없다. 그러나, @EmbeddableId에서 JPQL이 더욱 길어질 수 있다. 

+ **왜 equals(), hashCode() 멤버함수를 구현해야 할까?**

기본적으로 equals() 멤버함수는 **동일성(==) 비교**를 한다. 즉, 개발자가 Override하지 않으면 값이 아닌 주소 비교를 하게 되므로, 값이 같아도 false를 반환하게 된다.

따라서, **동등성 비교(값이 같은지를 비교)** 를 위해서 equals()와 hashCode() 멤버함수를 생성하는 것이 필요하다.


### 3.7 식별관계 매핑
**식별관계란 부모로부터 상속받은 기본키(외래키)를 자식에서 기본 키로 사용하는 관계를 의미하고, 비식별관계란 부모로 부터 상속받은 기본키를 외래키로만 사용하는 관계를 의미한다. 식별관계 매핑을 통해 상속 관계를 표현할 수 있다.**

복합 키를 갖는 식별관계를 어떻게 매핑할 것인지 알아본다. 부모, 자식, 손자 엔티티가 있다고 하자. 부모의 기본 키는 부모id, 자식의 기본 키는 (부모 id, 자식 id), 손자의 기본 키는 (부모 id, 자식id, 손자id)라고 하자. 아래와 같이 @IdClass를 통해 표현할 수 있다.

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

유사한 방법으로 @EmbeddedId를 통해서도 구현이 가능하다. 그러나, 식별 관계 매핑보다는 비식별 관계 매핑이 더욱 선호된다.
+ 식별 관계 사이에 조인해야 할 기본 키가 불필요하게 많아 질 수 있다.
+ 자식 엔티티가 존재하기 위해서는 반드시 부모 엔티티가 존재해야 한다. 왜냐하면 기본 키에 Null이 존재할 수 없기 때문이다. 그러나, 비즈니스 로직이 변화함에 따라 테이블 구조는 유연하게 변화할 수 있어야 한다.


***
## 4. 프록시와 연관관계 관리
**프록시란 문자 그대로 대리인이라는 의미를 갖는다. 예를 들어, 회원 엔티티와 팀 엔티티가 서로 연관되어 있다고 하자. EntityManger를 통해 회원 엔티티를 검색하면 회원 엔티티와 연관된 팀 엔티티와 같은 것들이 모두 조회하는 쿼리가 생성된다. 그러나, 실제로 사용자는 회원의 팀에 대한 정보를 원하지 않을 수 있다. 엔티티 객체 자체를 영속성 컨텍스트에 불러오는 것 대신에 프록시 객체를 불러온다. 사용자는 프록시 객체를 통해 엔티티에 대한 정보를 요청할 수 있고, 요청이 있을 때 실제로 쿼리가 발생하여 불필요한 것을 줄일 수 있다. 프록시가 엔티티에 접근하는 것을 대신한다는 점에서 의미가 통한다.**

```java
void main() {
  EntityManager em = EntityMangerFactory.createEntityManager();
  
  // 1. 엔티티 자체 조회
  Member member1 = em.find(ID, Member.class);
  
  // 2. 엔티티 프록시 조회
  Member member2 = em.getReference(ID, Member.class);
```
