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

보안 기능은 인증과 인가로 나누어진다.

스프링 시큐리티의 HttpSecurity 클래스에서는 인증 및 인가 API를 제공한다.

WebSecurityConfigureAdapter 클래스를 상속한 후, HttpSecurity를 이용해서 기본적인 보안 기능을 제공하는 객체를 생성할 수 있다.

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

Spring Security 5.7.0+ 버전 이후로는 WebSecurityConfigurerAdapter가 Deprecated되어 사용하지 않는다.

대신에 보안 설정 클래스를 @Bean으로 등록하는 방식을 사용할 수 있다.

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated(); 
        http.formLogin();
    }
}
```

***

### 1.3 인증 API: Form 인증 방식

Form 인증 방식이란 HTML의 Input Form을 활용해서 User를 인증하는 방법을 의미한다.

사용자가 적절한 ID와 Password를 입력하면 인증 서버에 Session을 등록하고 Session에 접근할 수 있는 Id 쿠키를 사용자에게 반환한다.

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
                .usernameParameter("userId") /* ID Input Form(HTML Form name 속성으로 참조) */
                .passwordParameter("passwd") /* Password Input Form(HTML Form name 속성으로 참조) */
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

AbstractAuthenticationProcessingFilter.class, UsernamePasswordAuthenticationFilter.class를 분석하면 인증필터 구조를 알 수 있다.

예를 들어, POST /login HTTP 메소드로 로그인 요청을 보낼 수 있다.

로그인 요청을 보냈을 때, 일어나는 과정은 아래와 같다.

+ 1. AuthenticationToken 객체를 만들어 임시로 ID, Password를 저장한다.
+ 2. AuthenticationManager를 호출한다. authenticationManager는 AuthenticationFilter에 등록되어 있다.

만약, UsernamePasswordAuthenticationFilter 이외에 새로운 Filter를 생성하고자 한다면, AuthenticationManager도 새롭게 생성해서 등록해야 한다.

+ 3. AuthenticationManager -> Provider를 거쳐서 인증에 성공하면, Authentication 객체에 Session을 추가한 후, SecurityContext(ThreadLocal)에 저장한다.

만약, 인증에 실패하면 AuthenticationFailureHandler.onAuthenticationFailure() 메소드를 통해 처리된다.

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
                .logoutUrl("/logout") // 로그아웃을 처리할 URL : HTTP POST /logout
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

SecurityContext에 저장된 AuthenticationToken를 제거하여 로그아웃한다.

***

### 1.6 Remember-Me 

Remember-Me는 세션이 만료된 이후에도 애플리케이션이 사용자를 기억하는 기능이다. 자동 로그인에 활용된다.

세션이 활성화된 상태에서는 로그인이 필요없고, 세션이 비활성화되면 다시 로그인을 해야한다.

Remember-Me 기능을 사용하면 세션이 만료되더라도 자동 로그인을 할 수 있다. 과정은 아래와 같다.

+ 1. Remember-me 쿠키를 로컬에 저장한다.
+ 2. Remember-me 쿠키를 key하여 접근할 수 있도록 ID와 Password를 서버에 저장한다. 이때, 메모리 또는 DB에 저장할 수 있다.
+ 3. 세션이 만료되었고 Remember-me 쿠키가 만료되지 않았다면, 자동 로그인을 할 수 있다.

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
                .rememberMeParameter("remember-me") // rememberMe 쿠키 key 이름
                .tokenValiditySeconds(3600) // rememberMe 토큰 유효기간(DB 또는 메모리에 rememberMe 토큰이 저장됨을 알 수 있다.)
                .alwaysRemember(true)
                .userDetailsService(userDetailsService); // 사용자 정보를 가져오는 Service 지정
    }
}
```

UserDetailService에 대해서 더 알아보도록 하자.

세션이 만료되었고 RemeberMe 토큰이 유효하다면, 저장소(Repository)에서 사용자 정보를 가져와야 자동 로그인을 할 수 있다.

이를 위해 스프링 시큐리티에서는 UserDetailService 인터페이스의 loadUserByUsername 멤버함수를 제공한다.

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

### 1.9 동시 세션 제어, 세선 고정 보호

#### 1.9.1 동시 세션 제어

사용자가 두 곳 이상에서 동시에 로그인을 시도할 수 있다. 최대 로그인 허용 개수를 1개라고 가정하자.

2가지 정책을 사용할 수 있다.

<br><b> 1. 이전 사용자 세션 만료 </b></br>-> 이전에 로그인한 사람의 세션을 만료하고 새롭개 로그인 하는 곳의 세션을 저장한다.
<br><b> 2. 현재 사용자 인증 실패 </b></br>-> 이전에 로그인한 사람의 세션은 유지하고 새롭개 로그인 하는 곳의 인증을 거부한다.

이와 관련된 스프링 시큐리티 API는 아래와 같다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
          http  .sessionManagement() // 세션 관리 정책
                .maximumSessions(1) // 최대 허용 세션 개수: 1개
                .maxSessionsPreventsLogin(false) // 동시 로그인 차단 방법(false: 기존 세션 만료, true: 기존 세션 유지)
                .invalidSessionUrl("...") // 세션이 유효하지 않을 때 이동할 페이지
                .expiredUrl("...") // 세션이 만료된 경우 이동할 페이지;

    }
}
```

#### 1.9.2 세션 고정 보호

세션을 이용한 로그인 방식은 공격자가 JSessionId를 탈취하면 공격당할 수 있다.

이에 대응하기 위한 스프링 시큐리티 API는 아래와 같다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionFixation().changeSessionId();
    }
}
```

changeSessionId는 인증할 때 마다 SessionId를 변경한다. 서버의 Session 자체를 변경하는 것은 아니다.

none 옵션은 아무것도 바꾸지 않는다. 
migrateSession은 기존 세션의 속성을 받아 Session 자체를 변화시킨다. 
newSession은 기존 속성을 받지 않고 Session 자체를 변화시킨다.

***

### 1.10 SessionManagementFilter, ConcurrentSessionFilter

세션 관리, 동시 세션 제어, 세션 고정 보호, 세션 생성 정책 등에 대해서 알아보았다.

이 모든 것들은 SessionManagementFilter.class에서 관리된다. 이 클래스를 분석하면 그 흐름을 알 수 있다.

세션을 이용한 인증 방식의 전체적인 Filter의 흐름은 아래와 같다.

UserPasswordAuthenticationFilter -> ConcurrentSessionFilter -> SessionManagementFilter

ConcurrentSessionFilter는 인증 실패 전략일 때 사용되는 필터이다.

이 흐름을 분석하면 스프링 시큐리티에서 제공하는 기본 인증 API는 마무리된다. 

***

### 1.11 인가 API - 권한 설정 및 표현식 

인증이 완료되면 사용자에게 권한을 부여한다. 권한 설정 방식은 URL, Method 방식 2가지가 있다.

2가지 방법은 선언적 방식과 동적 방식을 모두 지원한다. 

아래의 코드는 선언적 URL 방식의 인가 처리 예시이다. 

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("{noop}1111").roles("USER");
        auth.inMemoryAuthentication().withUser("sys").password("{noop}1111").roles("SYS", "USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}1111").roles("ADMIN", "SYS", "USER");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http    .authorizeRequests()
                .antMatchers("/user").hasRole("USER")
                .antMatchers("/admin/pay").hasRole("ADMIN")
                .antMatchers("/admin/**").access("hasRole('ADMIN') or hasRole('SYS')")
                .anyRequest()
                .authenticated();
    }
}
```

+ antMatchers: 권한을 설정할 HTTP API URL. **구체적인 경로가 먼저 오고, 그것보다 큰 범위가 뒤에 나오도록 해야한다.** antMatcher를 순서대로 체크하기 때문이다.
+ hasRole(role): 해당 role이 있으면 접근 가능

자세한 인가 처리에 대한 내용은 이후에 더 설명하도록 한다.

***

### 1.12 ExceptionTranslationFilter, RequestCacheAwareFilter

인증 및 인가에 실패할 때 발생하는 Filter의 흐름이 있다.

AbstractSecurityInterceptor.class, ExceptionTranslationFilter.class를 분석하면 구조를 이해할 수 있다.

인증 예외가 발생하면 AuthenticationEntryPoint.commence()를 실행하고, 인가 예외가 발생하면 AccessDeniedHandler.handle()을 실행한다.

익명 사용자에 의해 발생하는 인가 예외는 인증 예외를 발생시킨다.

그러므로, 위의 두 클래스만 생성해주면 된다. 스프링 시큐리티에서는 이를 API로 제공한다. 

방법은 아래와 같다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() { 
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        response.sendRedirect("/denied");
                    }
                });
    }
}
```
***
## 2. 주요 아키텍쳐 이해

### 2.1 FilterChainProxy

프로그램이 실행될 때, FilterChainProxy에 앞서 생성한 SecurityFilterChain Bean들이 저장된다.

이에 따라, 프로그램에 HTTP 요청이 들어왔을 때, FilterChainProxy를 통해 적절한 SecurityFilterChain을 거치게 된다. SecurityFilterChain은 여러개 등록할 수 있다.

WebSecurityConfigurerAdapter가 Deprecated 되었으므로 FilterChainProxy에 직접 SecurityFilterChain을 등록하는 방식을 사용한다.

### 2.2 Authentication 

사용자의 인증 정보를 저장하는 객체이다. 

Authentication, CredentialContainer -> AbstractAuthenticationToken 의 상속 구조를 갖는다.

스프링에서 기본적으로 제공하는 formLogin() 인증은 AbstractAuthenticationToken을 상속하여 구현한 UsernamePasswordAuthenticationToken을 사용한다.

AbstractAuthenticationToken을 상속하여 직접 Token 클래스를 구현할 수 있다.

인증 토큰은 principal(ID 정보), credentials(password), authorities(권한 정보), details(부가 정보), authenticated(인증 여부)를 필드 속성으로 갖는다.

UsernamePasswordAuthenticationToken을 통한 인증 과정은 아래와 같다.

+ 1. UsernamePasswordAuthenticationFilter : POST /login을 통해 전달된 \<form>의 name 속성을 참조하여 ID, Password를 가져온다.
+ 2. Authentication 객체 생성 : ID와 Password를 저장한 임시 인증 객체를 만든다.
+ 3. AuthenticationManager : authenticationManager에서 제공하는 provider를 통해 인증처리 한다.
+ 4. Authentication 객체 완성 : 인증에 성공하면, ID, Password 및 권한정보를 추가해 토큰을 완성한다.
+ 5. SecurityContext 저장 : 인증 객체를 서버에 저장한다. 사용자는 ID를 통해 접근할 수 있다.

### 2.3 SecurityContextHolder, SecurityContext

SecurityContext는 Authentication 객체 저장소이고, ThreadLocal에 저장된다. ThreadLocal이란 스레드마다 할당된 저장공간이다. 인증이 완료되면 HTTP Session에 인증 객체를 저장하고, 

SecurityContextHolder는 SecurityContext들을 관리하는 클래스이다. 아래의 코드를 통해 SecurityContext의 인증객체를 가져올 수 있다.

```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
```

### 2.4 Authentication Flow

회원(User)의 인증은 provider에서 담당했다. provider는 UserDetailsService를 필드로 갖는다.

UserDetailsService는 회원 정보를 검증하고, 일치하면 권한 정보를 추가한 authentication을 SecurityContext에 저장하여 반환하는 메소드 제공한다.

```java
@Service("UserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    // 이 클래스 자체를 UserService 안에서 오버라이딩 하는 방법도 있다.
    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username);

        if(account == null) {
            throw new UsernameNotFoundException("UsernameNotFoundException");
        }
        List<GrantedAuthority> roles = new ArrayList<>();

        List<AccountRole> accountRoleList = account.getAccountRoleList();
        for(AccountRole accountRole: accountRoleList) {
            roles.add(new SimpleGrantedAuthority(accountRole.getRole().getRoleName()));
        }

        AccountContext accountContext = new AccountContext(account, roles);
        return accountContext;
    }
}

public class AccountContext extends User {
    private final Account account;
    public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }
}
```

그러므로, provider는 항상 userDetailsService를 필드로 갖고 있어야 한다. userDetailsService의 흐름을 파악하면 Authentication Flow를 더 상세히 알 수 있다. 

### 2.5 AuthenticationManager

인증 요청의 종류에 따라 적절한 provider를 호출하는 객체이다. 

예를 들어, Form 인증, RememberMe 인증, 그리고 Oauth 인증이 있을 수 있다.

AuthenticationManager는 인증 요청 종류에 따라 적절한 provider를 불러온다. 

```java
public class ProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {
    ...
    private List<AuthenticationProvider> providers;
    private AuthenticationManager parent;
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> toTest = authentication.getClass();
        
        Iterator var9 = this.getProviders().iterator();
        while(var9.hasNext()) {
            AuthenticationProvider provider = (AuthenticationProvider)var9.next();
            if (provider.supports(toTest)) { // provider와 authentication이 서로 매칭됨.
              ...
              try {
                    result = provider.authenticate(authentication);
               }
            }
       }
 }  
```

부모 authenticationManager를 설정하여 provider 탐색을 계속할 수도 있다.

### 2.6 AuthenticationProvider

AuthenticationProvider는 인터페이스이고, 이를 통해 실질적인 인증을 담당하는 클래스를 구현한다.

+ authentication(Authentication authentication) : 인증 처리 메서드. 앞서 섫명한 UserDetailsService를 이용함 
+ supports(Class\<?> authentication) : provider가 지원하는 Authentication 클래스를 지정함.

```java
public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication) throws AuthenticationException;
    boolean supports(Class<?> authentication);
}

@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();

        AccountContext accountContext = (AccountContext)userDetailsService.loadUserByUsername(username);
        if(!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
            throw new BadCredentialsException("BadCredentialException");
        }

        /*
        FormWebAuthenticationDetails formWebAuthenticationDetails = (FormWebAuthenticationDetails)authentication.getDetails();
        String secretKey = formWebAuthenticationDetails.getSecretKey();

        if(secretKey != null || "secret".equals(secretKey)) {
            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");
        }
         */

        AjaxAuthenticationToken authenticationToken = new AjaxAuthenticationToken(accountContext.getAccount(),
                null, accountContext.getAuthorities());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AjaxAuthenticationToken.class);
    }
}
```

### 2.7 FilterSecurityInterceptor, Authorization

FilterSecurityInterceptor는 HTTP GET /** 등으로 리소스에 접근할 때 거치게되는 Filter 클래스이다.

SecurityFilterChain의 마지막에 위치하고, 요청의 승인/거부를 최종적으로 결정한다.

AbstractSecurityInteceptor -> FilterSecurityInterceptor 순서로 상속하여 커스터마이징할 수 있다.

FilterSecurityInterceptor를 상속하여 PermitAllFilter를 구현한 것을 예제로 살펴보자.

```java
@Slf4j
public class PermitAllFilter extends FilterSecurityInterceptor {
    List<RequestMatcher> permitAllRequestMatcher = new ArrayList<>();
    public PermitAllFilter(String... permitAllResources) {
        for(String resource : permitAllResources) {
            this.permitAllRequestMatcher.add(new AntPathRequestMatcher(resource));
        }
    }

    @Override
    protected InterceptorStatusToken beforeInvocation(Object object) {
        boolean permitAll = false;
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for(RequestMatcher requestMatcher : permitAllRequestMatcher) {
            if(requestMatcher.matches(request)) {
                permitAll = true;
                break;
            }
        }
        if(permitAll) {
            return null;
        }
        return super.beforeInvocation(object);
    }
}
```

PermitAllFilter.beforeInvocation()을 시작으로 인가 처리를 시작한다.

만약, requestMatcher(ex. GET /login)가 permitAllRequestMatcher 리스트에 존재한다면 별도의 인가 처리를 거치지 않고 자원 접근을 허용한다.(return null)

permitAll Request가 아니라면 AbstractSecurityInteceptor.beforeInvocation(object)를 실행한다.

```java
public abstract class AbstractSecurityInterceptor implements InitializingBean, ApplicationEventPublisherAware, MessageSourceAware {
    protected InterceptorStatusToken beforeInvocation(Object object) {
        Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource().getAttributes(object);
        
        Authentication authenticated = this.authenticateIfRequired();
        
        this.attemptAuthorization(object, attributes, authenticated);
    }
}
```
SecurityMetadataSoruce로부터 인가 정보(Collection\<ConfigAttribute>)를 가져온다.

SecurityMetadataSource에는 ```LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap``` 형태로 인가 정보가 저장되어 있다.

```this.obtainSecurityMetadataSource().getAttributes(object)```를 이용해 인증 객체의 권한과 일치하는 정보를 가져온다.

예를 들어, HTTP 요청은 GET /home이고 인가 정보에는 { GET /home -> ROLE_USER,  GET /user -> ROLE_USER, GET /manager -> ROLE_MANAGER }가 있다면, 위 메소드를 통해 {GET /home -> ROLE_USER}를 인가정보로 가져온다.

SecurityMetadataSource에 대해서는 이후에 더 자세히 다루도록 한다.


```Authentication authenticated = this.authenticateIfRequired();```에서는 ThreadLocal에 저장된 인증 객체를 가져온다.

만약, 리소스 접근 사용자가 인증된 상태가 아니라면, authenticationManager를 통해 인증 과정을 거친다.


앞서 불러온 리소스 요청 정보, 인가 정보, 인증 정보를 이용해 ```this.attemptAuthorization(object, attributes, authenticated);```를 실행한다. 실질적인 인가 처리를 실행한다.

```java
private void attemptAuthorization(Object object, Collection<ConfigAttribute> attributes, Authentication authenticated) {
        try {
            this.accessDecisionManager.decide(authenticated, object, attributes);
        } catch (AccessDeniedException var5) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(...));
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug(...));
            }

            this.publishEvent(new AuthorizationFailureEvent(object, attributes, authenticated, var5));
            throw var5;
        }
    }
```

accessDecisionManager.decide()를 통해 **request 요청하는 사용자가 권한이 있는지**를 확인한다.

권한이 있다면 인가 처리를 완료하고 return 한다.

만약, 위 과정에서 하나라도 실패한다면, Exception을 발생시키고 이를 ExceptionTranslationFilter에서 try-catch를 통해 잡아낸다. AccessDeniedHandler가 호출되기도 한다.

accessDecisionManager에 대해서는 추후 더 자세히 살펴보자.

FilterSecurityInterceptor에서는 AuthenticationManager, SecurityMetadataSource, 그리고 AccessDecisionManager가 사용되었다. 그러므로, 이를 SecurityConfig 클래스에서 지정해야 한다.

```java
    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManager());
        return permitAllFilter;
    }
```

### 2.8 AccessDecisionManager, AccessDecisionVoter

AccessDecisionManager는 인가 허용/거부를 결정하는 객체이고, AccessDeicisionVoter는 허용/거부의 구체적인 방식을 나타내는 객체이다.

AccessDecisionManager의 상속 구조는 AccessDecisionManager -> AbstractAccessDecisionManager -> AffirmativeBased이다.

AbstractAccessDecisionManager는 ```List<AccessDecisionVoter<?>> decisionVoters```를 갖는다.

AccessDecisionManager는 인터페이스이고 대표적인 구현체는 AffirmativeBased이고, AffirmativeBased는 decisionVoters 중 1개만 통과해도 인가를 허용하도록 하는 구현체이다.

```java
public class AffirmativeBased extends AbstractAccessDecisionManager {
    public AffirmativeBased(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException {
        int deny = 0;
        Iterator var5 = this.getDecisionVoters().iterator();
        while(var5.hasNext()) {
            AccessDecisionVoter voter = (AccessDecisionVoter)var5.next();
            int result = voter.vote(authentication, object, configAttributes);
            switch (result) {
                case -1:
                    ++deny;
                    break;
                case 1:
                    return;
            }
        }

        if (deny > 0) {
            throw new AccessDeniedException(this.messages.getMessage());
        } else {
            this.checkAllowIfAllAbstainDecisions();
        }
    }
}
```

AccessDecisionVoter 중 가장 많이 사용되는 것은 RoleVoter이다. 코드를 통해 구체적으로 살펴보자.

주석과 코드를 통해 파악할 수 있다.

```java
public class RoleVoter implements AccessDecisionVoter<Object> {
    private String rolePrefix = "ROLE_";
    
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
          int result = 0;
          Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
          
          Iterator var6 = attributes.iterator(); // object(request)에 접근할 수 있는 권한 리스트를 순회
          while(true) {
                ConfigAttribute attribute;
                do {
                    if (!var6.hasNext()) {
                        return result;
                    }
                    attribute = (ConfigAttribute)var6.next();
                } while(!this.supports(attribute));
                

                result = -1;
                Iterator var8 = authorities.iterator();

                while(var8.hasNext()) {
                    GrantedAuthority authority = (GrantedAuthority)var8.next();
                    if (attribute.getAttribute().equals(authority.getAuthority())) {
 // authentication 객체가 가진 권한 리스트 중 하나와 object(request)에 접근할 수 있는 권한 중 일치하는 것을 찾음.
                        return 1;
                    }
                }
            }     
    }
}
```

FilterSecurityInterceptor에서는 AuthenticationManager, SecurityMetadataSource, 그리고 AccessDecisionManager가 사용되었다. 

마찬가지로 AccessDecisionManager는 RoleVoter 리스트가 사용되었으므로 이를 SecurityConfig 클래스에서 설정한다.

```java
    private List<AccessDecisionVoter<?>> getAccessDecisionVoter() {
        List<AccessDecisionVoter<? extends Object> > accessDecisionVoters = new ArrayList<>();
        accessDecisionVoters.add(roleVoter()); // 자원 권한 관련 voter
        return accessDecisionVoters;
    }
```

이외에도 다양한 AccessDecisionManager와 Voter가 존재하나 추후 더 살펴보도록 한다.

***

## 3. 스프링 시큐리티 실전 및 심화 

### 3.1 시큐리티 모듈 커스터마이징

AuthenticationProvider, AuthenticationSuccessHandler, AuthenticationFailureHandler, accessDeniedHandler 등의 시큐리티 관련 모듈을 커스터마이징은 해당되는 인터페이스를 구현하면 된다.

인터페이스 구현은 2장에서 몇가지 했으므로 예제 코드는 생략한다. 그리고, httpSecurity에 아래와 같이 설정하면 된다.

```java
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http
                .authorizeRequests()
                .anyRequest()
                .authenticated()

        .and()
                .authenticationProvider(authenticationProvider())
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(authenticationDetailsSource)
                .defaultSuccessUrl("/", true)
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll();

        http
                .exceptionHandling()
                .accessDeniedPage("/accessDenied")
                .accessDeniedHandler(accessDeniedHandler());

        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
        return http.build();
}       
```

### 3.2 Ajax 인증 예제

스프링 시큐리티에서는 Form 인증 방식을 기본적으로 제공한다.

인증 프로세스를 새롭게 Ajax 방식으로 구현하였다. 아래 URL을 참고한다.

https://github.com/chrismrkr/toyproject-spring_security_ajaxAuthentication

### 3.3 DB 연동 URL 인가 방식

URL 인가 방식은 선언적 방식과 동적 방식 2가지가 존재한다.

선언적 방식은 대표적으로 antMatchers(...).hasRole(...) 표현식을 이용한다.

예를 들어, ```http.antMatchers("/api/messages").hasRole("MANAGER")```로 설정하면 ROLE_MANAGER 권한을 가진 사용자만 해당 리소스에 접근할 수 있다.

반면에, 선언전 방식 대신에 DB와 연동하는 것을 동적 방식이라고 한다. 

DB에는 권한-리소스 key-value 형태로 저장되어있다. 이를 활용해 SecurityMetadataSource를 생성한다.

자세한 내용은 아래의 URL을 참고한다.

https://github.com/chrismrkr/toyproject-spring_security_DBAuthorization

SecurityMetadataSource를 이전과 다르게 구현하면 된다. SecurityMetadataSource 인터페이스는 아래와 같다.

```java
public interface SecurityMetadataSource extends AopInfrastructureBean {
    Collection<ConfigAttribute> getAttributes(Object request) throws IllegalArgumentException;
    Collection<ConfigAttribute> getAllConfigAttributes();
    boolean supports(Class<?> clazz);
}
```

여기에 requestMap(Security DB에 저장된 내용), securityResourceService(Security DB Dao)를 추가하여 구현체를 만든다.

```java
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();
    private SecurityResourceService securityResourceService;

    Collection<ConfigAttribute> getAttributes(Object request) throws IllegalArgumentException;
    Collection<ConfigAttribute> getAllConfigAttributes();
    boolean supports(Class<?> clazz);
}
```

SecurityMetadataSoruce를 등록하면, FilterSecurityInterceptor에서 인가를 시도할 때 사용할 수 있게 된다.

### 3.4 AOP 인가 방식

URL 단위가 아닌 서비스 단위로 인가처리를 수행한다.

 




