# 자주 사용되는 Annotation 정리

### @Configuration

해당 Annotation이 붙은 클래스를 스프링 빈으로 수동 등록한다.


### @Bean

메소드를 스프링 빈으로 등록한다. 클래스(@Configuration)의 메소드를 Bean을 붙이면 싱글톤으로 사용할 수 있도록 만든다.


### @Autowired

의존관계를 자동으로 주입할 수 있도록 만든다.

**필드 주입 vs Setter 주입 vs 생성자 주입**

스프링에서는 생성자 주입을 권장한다. @RequiredArgsConstructor Annotation과 final 키워드를 통해서도 생성자 주입이 가능하다.

생성자 주입을 권장하는 이유는 다음과 같다.

1. 테스트 코드 작성의 편의성

필드 주입 방식을 사용하면 테스트 코드 작성시 의존관계가 주입되지 않는다. 물론, Setter를 사용할 수 있지만 바람직하지 않다.

2. 불변 객체

생성자 주입을 통해 객체를 주입하고, final 키워드를 사용하면 불변 객체가 된다. 변경 가능성을 배제한다는 장점이 있다.

3. 순환 참조 문제 방지

생성자 주입을 사용하면, 객체가 생성되는 시점에 순환 참조 문제를 바로 확인할 수 있다.

### @ComponentScan

@Component가 붙은 클래스를 스캔에서 스프링 빈으로 등록한다.

@SpringBootApplication Annotation에 @ComponentScan이 포함되므로, @Component만 적용시키면 스프링 빈으로 등록할 수 있다.

### @Component

자동으로 스프링 빈으로 등록하기 위해 사용된다.

**자동 등록 vs 수동 등록**

비즈니스 로직과 관련된 것은 자동 등록, 설정과 관련된 것은 수동 등록을 사용하는 전략을 사용하자.

### @Qualifier

주입하고자 하는 필드에 대한 구현체(class)가 2개 이상 존재할 수 있다. 물론, 필드 이름을 통해 매칭할 수 있다.

또다른 방법으로 @Qualifier를 통해 매칭할 수 있다.

### @Primary

마찬가지로 주입하고자 하는 필드에 대한 구현체(class)가 2개 이상 존재할 수 있다. 만약, 이름 매칭, @Qualifier 매칭 모두 없다면, @Primary로 지정된 것이 우선적으로 주입된다.


### @Controller

View로 매핑하기 위한 컨트롤러를 지정한다.

### @RestController

RestApi 형식으로 반환하기 위한 컨트롤러를 지정한다.

### @GetMapping

특정 URL의 HTTP GET 요청을 처리하는 메소드를 지정한다. 

### @PostMapping

특정 URL의 HTTP POST 요청을 처리하는 메소드를 지정한다. 

### @PutMapping

특정 URL의 HTTP PUT 요청을 처리하는 메소드를 지정한다. 

### @PathVariable

URL에 포함된 Variable을 매개변수로 가져오기 위해 사용한다.

```java
public void func(@PathVariable("id") Long id, ...) { ... }
```

### @RequestParam

HTTP GET 요청에서 쿼리 파라미터를 매개변수로 받기 위해서 사용한다.

ex. URL: .../url?name="hello"&age=10

```java
@GetMapping("/url")
public void func(@RequestParam("name") String name, @RequestParam("age") int age ) { ... }
```

### @ModelAttribute

HTTP GET 요청의 쿼리 파라미터 또는 FORM 형태의 데이터를 처리해 객체로 바인딩하기 위해 사용된다.

객체에 파라미터를 바인딩하기 위해 생성자, 또는 Setter를 사용할 수 있다.

### @RequestBody

HTTP 요청의 Message Body를 처리 객체로 변환하기 위해 사용한다. 

HTTP 메세지 컨버터를 통해 변환한 후, 기본 생성자를 통해 객체를 생성하고 필드명 인식을 위해 Getter 또는 Setter를 사용한다. (Getter 사용하자.)


### @ResponseBody

HTTP BODY 내용을 HTTP 메세지 컨버터를 통해 JSON 형식 등으로 변환해 리턴하도록 만든다.

HTTP 메세지 컨버터는 바이트 스트림, 문자열, Json 등으로 변환해 리턴할 수 있도록 한다.
