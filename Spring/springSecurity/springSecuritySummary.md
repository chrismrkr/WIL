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

***

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

위는 기본적인 보안 기능 구현 방식이다. 이외에도 많은 인증과 인가 API를 제공하고 있다.

***

### 1.3 인증 API: Form 인증 방식

Form 인증 방식이란 HTML의 Input Form을 활용해서 User를 인증하는 방법을 의미한다.

사용자가 적절한 ID와 Password를 입력하면 인증 서버에 Session을 등록하고 Session에 접근할 수 있는 Id를 사용자에게 반환한다.

스프링 시큐리티에서 제공하는 인증 API는 아래와 같다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
           
           
           http .formLogin() /* 인증 정책 */ 
                .loginPage("/loginPage") /* 로그인 Input Form URL */
                .defaultSuccessUrl("/") /* 로그인 성공시 이동할 URL */
                .failureUrl("/login") /* 로그인 실패시 이동할 URL */
                .usernameParameter("userId") /* ID Input Form HTML Form name */
                .passwordParameter("passwd") /* Password Input Form HTML Form name */
                .loginProcessingUrl("/loginProc") /* 로그인 정보를 처리할 컨트롤러 URL */
                .successHandler(new AuthenticationSuccessHandler() { /* 로그인 성공 후 호출할 컨트롤러 */
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        System.out.println("authentication: " + authentication.getName());
                        response.sendRedirect("/");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() { /* 로그인 실패 후 호출할 컨트롤러 */
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        System.out.println("exception: " + exception.getMessage());
                        response.sendRedirect("/login");
                    }
                })
                .permitAll(); /* 모든 유저는 인증 가능하다. */
    }
}
```

***

### 1.4 UserPasswordAuthenticationFilter의 구조

AbstractAuthenticationProcessingFilter.class, UsernamePasswordAuthenticationFilter.class를 분석하면 인증필터 구조를 확인할 수 있다.

개괄적인 방법은 아래와 같다.

+ 1. Authentication 객체를 만들어 임시로 ID, Password를 저장한다.
+ 2. AuthenticationManager를 통해 실제 인증을 수행한다.
+ 3. 인증에 성공하면 Authentication 객체에 Session을 추가한 후, SecurityContext에 저장한다.

위의 두 클래스를 분석하면 더 명확히 알 수 있다.

***

### 1.5 LogoutFilter

Logout API를 이해하기 전에 **쿠키, 세션, 그리고 토큰**에 대해서 정리해보도록 하자.

+ <b>쿠키</b>: 세션에 접근하기 위해 Local에 존재하는 key
+ <b>세션</b>: 서버 메모리에 저장된 정보
+ <b>토큰</b>: 일반적으로 JWT Token을 의미하고, 인증 절차를 통해 받을 수 있다. Http Header에 JWT Token을 추가해 서버에 전송하여 인가를 받을 수 있다. 그러므로, 서버에서는 인가를 위한 별도의 저장장치가 필요하지 않다.

쿠키와 세션을 통한 인가 방식은 세션 하이재킹의 위험성과 서버에 세션을 저장하여 부담이 증가한다는 단점이 있다.

토큰 방식은 MSA 아키텍처에서의 애플리케이션 확장성을 늘릴 수 있지만 매번 Header에 토큰을 추가해야한다는 단점이 있다.

스프링 시큐리티에서의 인가 정책은 기본적으로 쿠키와 세션 방식을 따른다. 

스프링 시큐리티에서 제공하는 Logout API는 세션을 만료한다. 제공하는 API는 아래와 같다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    
           http .logout() // 로그아웃 처리 API 
                .logoutUrl("/logout") // 로그아웃을 처리할 URL
                .logoutSuccessUrl("/login") // 로그아웃 성공시 이동할 URL
                .addLogoutHandler(new LogoutHandler() { // 로그아웃 실행 시 실행할 컨트롤러 
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        request.getSession().invalidate();
                    }
                })
                .logoutSuccessHandler(new LogoutSuccessHandler() { // 로그아웃 성공 후 실행할 컨트롤러
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                });
    
    }
}
```

자세한 것을 알아보기 위해 스프링 시큐리티의 **LogoutFilter.class** 파일을 분석해보도록 하자.

(IntelliJ에서 Shift+Shift 단축키로 찾아볼 수 있다.)

SecurityContext의 Authentication 객체를 제거하는 방식으로 진행된다.

***

### 1.6 Remember-Me 

Remember-Me는 세션이 만료된 이후에도 애플리케이션이 사용자를 기억하는 기능이다. 자동 로그인에 활용된다.

세션이 활성화된 상태에서는 로그인이 필요없고, 세션이 비활성화되면 다시 로그인을 해야한다.

Remember-Me 기능을 사용하면 세션이 만료되더라도 자동 로그인을 할 수 있다. 과정은 아래와 같다.

+ 1. Remember-me 쿠키를 로컬에 저장한다.
+ 2. Remember-me 쿠키를 key하여 접근할 수 있도록 ID와 Password를 서버에 저장한다. 이때, 메모리 또는 DB에 저장할 수 있다.
+ 3. 세션이 만료되었고 Remeber-me 쿠키가 만료되지 않았다면, 자동 로그인을 할 수 있다.

스프링 시큐리티에서 제공하는 API는 아래와 같다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.rememberMe() // rememberMe API
                .rememberMeParameter("remember-me") // rememberMe 쿠키 이름
                .tokenValiditySeconds(3600) // rememberMe 토큰 유효기간(DB 또는 메모리에 remeberMe 토큰이 저장됨을 알 수 있다.)
                .alwaysRemember(true)
                .userDetailsService(userDetailsService); // 사용자 정보를 가져오는 Service 지정
    }
}
```

UserDetailService에 대해서 더 알아보도록 하자.

세션이 만료되었고 RemeberMe 토큰이 유효하다면, 저장소(Repository)에서 사용자 정보를 가져와야 자동 로그인을 할 수 있다.

이를 위해 사용되는 것이 스프링 시큐리티의 UserDetailService 인터페이스의 loadUserByUsername 멤버함수이다.

예를 들어, 회원을 관리하는 서비스(MemberService)에서 UserDetailService 인터페이스의 loadUserByUsername을 implement 및 Override하여 회원 정보를 가져올 수 있도록 만들 수 있다.

이때, 회원 정보는 UserDetails 인터페이스로 반환해야 한다. UserDetails는 스프링 시큐리티에서 제공하는 인터페이스로 회원정보와 권한정보를 갖는다.

대략적인 코드는 아래와 같을 것이다.

```java
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailService {
    private final MemberRepository memberRepository;
    
    /* ... 멤버함수들 있음 ... */
    
    @Override
    public UserDetails loadUserByUsername(Long pk) {
        Member member = memberRepository.findById(pk);
        
        // 빌더 패턴 활용
        return UserDetails.Builder()
                .userName(member.getUserName())
                .bulild();
    }
}
```

***

### 1.7 Remember-Me의 구조

RememberMeAuthenticationFilter.class를 분석하면 그 구조를 알 수 있다.

개괄적인 구조는 아래와 같다.

+ 1. RememberMeAuthenticationFilter에서 SecurityContext의 Authentication 객체가 유효한지 검증한다. 즉, 세션이 유효한지 검증한다.
+ 2. 세션이 유효하지 않다면, RememberMeService에서 RememberMe 토큰이 유효한지 검증한다.
+ 3. RememberMe 토큰이 유효하다면, 해당 토큰과 연동된 회원정보가 유효한지 확인한다.
+ 4. 회원정보도 유효하다면, 앞서 구현한 UserDetailService 인터페이스를 통해 회원정보를 불러온다.

***

### 1.8 AnonymousAuthenticationFilter

익명사용자를 인증처리하는 필터이다. **오로지 인증 사용자와 구별하기 위해 사용한다.**

과정은 아래와 같다. AnonymousAuthenticationFilter.class를 분석하면 확인할 수 있다.

+ 1. 이미 인증된 회원, 즉 Authentication 객체가 존재하는지를 확인한다.
+ 2. 인증된 회원이 아니라면 AnonymousAuthenticationToken을 발급해서 SecurityContext에 저장한다.

***

### 1.9 동시 세션 제어, 세선 고정 보호, 세션 정책

#### 1.9.1 동시 세션 제어

사용자가 두 곳 이상에서 동시에 로그인을 시도할 수 있다. 최대 로그인 허용 개수를 1개라고 가정하자.

2가지 정책을 사용할 수 있다.

<br> 1. 이전 사용자 세션 만료 </br>-> 이전에 로그인한 사람의 세션을 만료하고 새롭개 로그인 하는 곳의 세션을 저장한다.

<br> 1. 현재 사용자 인증 실패 </br>-> 이전에 로그인한 사람의 세션은 유지하고 새롭개 로그인 하는 곳의 인증을 거부한다.








