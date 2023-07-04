# 스프링 데이터 JPA

스프링 데이터 JPA는 데이터 접근 계층 클래스에서의 반복되는 CRUD를 해결하는 기술이다. 아래와 같이 인터페이스를 구현해서 완성할 수 있다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> { }
```

## 1 쿼리 메소드 기능

### 1.1 메소드 이름으로 쿼리 생성

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

### 1.2 @Query를 통한 쿼리 직접 정의

메소드 이름을 통한 쿼리 생성 기능은 조건이 많아질수록 메소드 이름이 길어지는 단점이 있다.

그 대신에 @Query를 사용하여 쿼리를 직접 정의할 수 있다.

```java
@Query(value = "select m from Member m where m.username = :username and m.age > :age)
List<Member> findByUsernameAndAge(@Param("username") String username, @Param("age") int age);
```

메소드 이름을 통한 쿼리 생성, @Query 모두 컴파일 시점에 SQL 문법 오류를 찾을 수 있는 것이 장점이다.

### 1.3 유연한 반환 타입

동일한 JPQL도 객체, 컬렉션, Optional로 다양하게 결과를 return 받을 수 있다.

다만, JPQL 조회 결과가 0건이고 컬렉션 반환 타입인 경우에는 null이 아닌 빈 컬렉션이 반환되므로 주의한다.

### 1.4 순수 JPA 페이징과 정렬

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

### 1.5 스프링 데이터 JPA 페이징과 정렬 

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

### 1.6 벌크성 수정 쿼리

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

### 1.7 엔티티 그래프

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

### 1.8 JPA Hint & Lock

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

***

## 2. 확장 기능

### 2.1 사용자 정의 레포지토리

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

### 2.2 Auditing

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

**주의사항: Spring Data Jpa Auditing 기능을 사용하려면 Main에 반드시 @EnableJpaAuditing을 등록해야한다.**

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

### 2.3 Web 확장 도메인 클래스 컨버터

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

### 2.4 Web 확장 페이징과 정렬

Spring Data JPA 페이징 및 정렬 기능을 Web(컨트롤러)에 확장하여 사용할 수 있다.

```java
@GetMapping("/members")
public Page<Member> getUsername(Pageable pageable) {
	Page<Member> m = memberRepository.findAll(pageable);
	return m;
}
```

#### 1. Http 요청 파라미터를 통해 Pageable에 파라미터를 전달할 수 있다.

+ page : 가져올 페이지 번호(0부터 시작함)
+ size : 한 페이지당 데이터 수
+ sort : 정렬 조건 정의

예를 들어, GET /members?page=0&size=20&sort=id, desc&sort=username,desc와 같이 작성하여 페이징 기능을 사용할 수 있다.


#### 2. 페이징 기본값 설정도 가능하다.

Application 전역으로 기본값 설정을 원하는 경우, 설정 파일(.yml, .properties)을  아래와 같이 변경한다.

spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.max-page-size=2000

메소드 별로 기본값 설정을 원하는 경우, @PageableDefault 어노테이션을 사용한다.

```java
public String list(@PageableDefault(size = 12, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
...
}
```

페이징 정보가 2개 이상이라면 @Qualifier에 접두사를 추가한다. 

```java
public String list(@Qualifiter("member") Pageable memberPageable, @Qualifier("order") Pageable orderPageable) {
...
}
```

페이징 정보를 전달하기 위해서는 아래와 같이 쿼리 파라미터를 사용한다.

GET /members?member_page=0&order_page=1



#### 3. 페이징 결과를 DTO로 map을 통해 변환할 수 있다.

```java
    public Page<MemberDto> list(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }
```

***

## 3. Spring Data JPA 분석

### 3.1 구현체 분석

Spring Data JPA 구현체와 save 메소드는 아래와 같다.

```java
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID> ... {

	@Transactional
	public <S extends T> S save(S entity) {
		if(entityInformation.isNew(entity) {
			em.persist(entity);
			return entity;
		}
		else {
			return em.merge(entity);
		}
	}
}
```

특징을 정리하면 아래와 같다.

+ 1. @Repository가 적용되었으므로 JPA Exception을 Spring Exception으로 변환함
+ 2. @Transactional이 적용되었으므로 Spring Data JPA를 사용할 때는 해당 어노테이션 생략 가능
+ 3. @Transactional(readOnly = true) 옵션을 사용하면 조회성 쿼리는 flush가 발생하지 않으므로 성능 향상


### 3.2 새로운 엔티티 구별 방법

위의 save 메소드는 신규 엔티티인 경우에는 em.persist를 호출하고, 그렇지 않은 경우는 em.merge를 호출한다.

전자는 flush될 때 update 쿼리를 보내고, 후자는 select + update 쿼리를 보내므로 merge가 성능이 더 나쁘다.

@Id(식별자) 어노테이션을 통해서 엔티티 신규 여부를 확인할 수 있는데, 

식별자(@Id)를 @GeneratedValue를 통한 자동 할당 방식을 사용하는 대신에 직접 할당 방식을 사용한다면,

이미 @Id가 할당되었으므로 신규 생성이더라도 em.merge(select + update)가 발생한다.

그러므로, Id를 직접 할당하는 경우에 Persistable 인터페이스를 구현하는 것이 필요하다. 아래와 같이 CreatedDate를 기준으로 하여 신규 생성 여부를 확인할 수 있다.

```java
@Entity
@EntityListener(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
	@Id
	private String id;

	@CreatedDate
	private LocalDateTime createdTime;

	@Override // Persistable 인터페이스 메서드 구현
	public String getId() {
		return this.id;
	}
	@Override // Persistable 인터페이스 메서드 구현
	public boolean isNew() {
		return this.createdDate == null;
	}
}
```
