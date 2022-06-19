# 자주 사용되는 JPA, Lombok, Validation Annotation 정리

### @Entity

해당 클래스를 DB 엔티티로 등록한다.

### @Getter

클래스의 필드에 대한 Get 메소드를 생성한다.

### @Setter

클래스의 필드에 대한 Set 메소드를 생성한다.

### @RequiredArgsConstructor

final 필드로 생성자를 만든다. 스프링 빈 의존관계 주입도 가능하다.

### @AllArgsConstructor

모든 필드로 생성자를 만든다.

### @NoArgsConstructor

기본 생성자를 만든다.

### @Data

@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor를 모두 포함한 애노테이션

### @Id

기본키로 설정할 필드

### @MapsId

외래키를 기본키로 설정함

### @Column

DB에 매핑되는 칼럼명을 지정한다.

### @ManyToOne, @OneToOne, @OneToMany

엔티티간 연관관계를 매핑하기 위해 사용한다.

### @JoinColumn

관계집합을 한쪽 엔티티로 합한다.

### @Transactional(중요)

트랜잭션이란 일련의 read, write 작업이다.

**@Transactional은 트랜잭션의 원자성(A), 일관성(C), 지속성(D)을 보장하기 위해 사용한다.**

(No Force -> Redo -> Atomicity, Steal -> Undo -> Durability)

그러나, 격리수준(Isolation)은 따로 지정해야 한다.

가장 확실한 격리수준을 확보하기 위한 방법은 직렬화 스케줄링 방식이다. (Shared Lock, Exclusive Lock) <비관적 락>

물론, DeadLock 한계점을 해결하기 위해서 타임아웃을 지정해야 한다.

그러나, 이는 동시성 처리 성능이 낮으므로 일반적으로 데이터베이스는 Read Committed 방식을 적용한다.

Read Committed에서 Non-Repeatable Read를 방지하기 위해 **낙관적 락**을 활용한다.



