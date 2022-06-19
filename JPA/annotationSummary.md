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

### @Id

기본키로 설명할 필드

### @MapsId

외래키를 기본키로 설정함

