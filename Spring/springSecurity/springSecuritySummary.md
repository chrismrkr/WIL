# Spring Security

Spring Security는 웹 서비스에 대한 인증과 인가 기능을 제공하는 Spring 계열의 프레임워크이다. 


## 1. 기본 API 및 Filter의 이해

Spring Security를 사용하기 위해서는 Spring boot의 Dependency에 아래를 추가해야 한다.

Maven Project일 때, pom.xml에 아래를 추가하도록 한다. 물론, start.spring.io에서도 추가할 수 있다.

```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
```

### 1.1 Filter의 이해

스프링 시큐리티의 기본 API를 이해하기 전에 Filter에 대해서 이해해야 한다.

MVC 디자인 패턴이란 프론트 컨트롤러로 Http Request를 보내면, 이에 맞는 핸들러 어댑터를 불러온 후, 핸들러 어댑터를 통해 Request에 대한 컨트롤러(핸들러)를 실행한 후, Model에 필요한 파라미터들을 저장한 후, View로 반환하는 것을 의미한다.

Filter란 프론트 컨트롤러가 Http Request를 받기 전에 선제적으로 HTTP 요청을 걸러주는 기능이다.

Filter 인터페이스를 구현하는 방식은 아래와 같다.

```java
public class FilterImpl implements Filter {
  @Override
  public void init(...) { ... }
  @Override 
  public void destroy() { ... }
  
  /* 초기화, 종료 메서드는 기본적으로 구현되어있지만, 추가로 커스터마이징 할 수 있다. */
  
 @Override 
 public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
      HttpRequestServlet httpRequest = (HttpServletRequest) request;
      String requestURI = httpRequest.getURI();
      
      String uuid = UUID.randomUUID().toString();
      try {
          log.info("REQUEST [{}][{}]", uuid, requestURI);
          chain.doFilter(request, response);
      } catch (Exception e) {
          throw e;
      } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
      }    
   }
}
```

Filter를 Chain으로 연결해서 사용하는 클래스를 아래와 같이 스프링 빈으로 등록해서 싱글톤으로 사용할 수 있다.

```java
@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new FilterImpl());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }
 }
```

### 1.2 사용자 정의 보안 기능 구현

보안 기능은 크게 인증과 인가로 나누어진다.

스프링 시큐리티의 HttpSecurity 클래스에서 인증과 인가와 관련된 API를 제공한다.

HttpSecurity를 이용해서 WebSecurityConfigureAdapter 클래스를 구현해 기본적인 보안 기능을 제공하는 객체를 생성할 수 있다.

WebSecurityConfigureAdapter를 상속해서 사용자가 정의한 보안 기능을 제공하는 클래스를 구현할 수 있다. 

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated(); /* 인가 정책: 인증이 된 사용자만이 Request 가능 */
        http.formLogin(); /* 인증 정책: Form 인증*/
    }
}
```


### 1.3 인증 API: Form 인증 방식


