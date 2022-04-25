# 스프링 MVC 1

**스프링 웹 MVC를 공부하는 이유는 WAS(Web Application Server)를 효율적으로 개발하기 위함이다.** 

### 내용 정리용 핵심 질문

+ MVC 패턴의 동작 방식과 각 부분의 역할에 대해서 설명할 수 있다.
+ 스프링 MVC에서 사용되는 Annotation의  역할 대해서 설명할 수 있다.

***
***

## 1. 서블릿

스프링 MVC를 학습하기 전에 서블릿에 대해서 알아보도록 한다. 서블릿은 Http 요청 및 응답을 간단하게 처리할 수 있게 하는 싱글톤 객체이다.

서버로 HTTP 메세지가 도착하면, 헤더 정보를 읽고 메세지를 파싱하는 등 반복되는 작업들을 할 수 밖에 없다.

이러한 반복되는 작업 등이 포함된 HTTP request 및 response를 처리하기 위해 서블릿(Servlet)이 존재한다.


### 1.1 서블릿 동작 메커니즘

서버가 외부로부터 HTTP Reqeust를 특정 URI로 받는다고 하자. URL을 담당하는 서블릿 싱글톤 객체를 서블릿 컨테이너로부터 가져온다.

호출된 서블릿 객체를 통해 HTTP Request를 HttpServletRequest 객체로 받는다.

그리고 서버의 비즈니스 로직을 실행한 후, 다시 HttpServletResponse 객체로 Http response 메세지를 작성 후 응답한다.

그러므로, 서블릿을 제대로 사용하기 위해서는 HTTP의 start-line, header에 무엇이 있는지 잘 아는 것이 중요하다.

**Start-line**

+ Http Method
+ Request URL/URI
+ QueryParameter(QueryString)

**Header**
 
+ Host Ip
+ Port Number
+ Cache-Control
+ Cookie
+ Accepted-Language
+ Content-Type, Content-Length
+ Character-Encoding
+ keep-alive

### 1.2 HTTP Request 데이터 1: GET(쿼리 파라미터 사용)

.../uri?[queryParamter] 형식으로 이루어진 url이다. Http message Body가 없다는 것이 특징이다.

서블릿을 통해 HTTP GET Request를 받아 Response하는 자바 코드는 아래와 같다.

```java
@WebServlet(name="requestParamServlet", urlPatterns="/request-param")
public class RequestParamServlet extends HttpServlet {
  
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
       
       String name = req.getParameter("username"); // 단일 쿼리 파라미터 조회
       String[] names = req.getParameters("username") // 여러 파라미터 조회 (ex. username=kim&username=lee)
       
       req.getParameterNames().asIterator()
            .forEachRemaining(paramName -> sout(req.getParamater(paramName)); // 전체 파라미터 조회
            
       // 이외에도 start-line, header 정보도 확인할 수 있다.
       
       resp.getWriter().write("ok") 
         
    }
}       
```

### 1.3 HTTP Request 데이터 2: POST(HTML FORM)

1.2의 request의 쿼리 파라미터는 messageBody에 저장되고, 메서드는 GET에서 POST로 바뀌었다.

**messageBody에 내용을 포함해 전송할 때는 반드시 Content-Type을 지정하는 것에 유의하도록 한다.**

그러므로, messageBody의 유무는 Content-Type이 null인지 아닌지로 판단할 수 있다.

HTML FORM의 경우의 Content-Type은 application/x-www-form-urlencoded이다.

```java
@WebServlet(name="requestBodyStringServlet", urlPatterns="/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
  
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
       ServletInputStream inputStream = req.getInputStream();
       String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
       
       resp.getWriter().write("ok") 
    }
}       
```

### 1.4 HTTP Request 데이터: POST(Json API Message Body)

Content-Type은 application/json이다. HTTP 통신시 가장 많이 사용되는 방법이다.

```java

@Getter @Setter
public class Person {
  private String name;
  private int age;
}


@WebServlet(name="requestJsonAPIServlet", urlPatterns="/request-body-json")
public class RequestJsonAPIServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
  
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
       ServletInputStream inputStream = req.getInputStream();
       String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
       
       Person person = objectMapper.readValue(messageBody, Person.class);
       
       resp.getWriter().write("ok");
    }
}       
```

### 1.5 HTTP Response 데이터 1: 단순 텍스트, HTML

마찬가지로 response Content-Type을 명시한다.

```java
@WebServlet(name="responseHtmlServlet", urlPatterns="/response-html")
public class ResponseHtmlServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
       resp.setContentType("text/html");
       resp.setCharacterEncoding("utf-8");
       
       Printwriter printWriter = resp.getWriter();
    }
}       
```

### 1.6 HTTP Response 데이터 2: JSON API

```java
@WebServlet(name="responseJsonServlet", urlPatterns="/response-Json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
       resp.setHeader("Content-Type", "application/json");
       resp.setCharacterEncoding("utf-8");
       
       Person person = new Person("kim", 20);
       
       String result = objectMapper.writeValueAsString(person);
       
       resp.getWriter().write(result);
    }
}       
```

***

## 2. 서블릿, JSP의 단점

### 2.1 서블릿의 단점

서블릿을 통해 Http Request(GET, POST HTML Form, POST Json API)와 Http Response(text/plain, text/html, application/json)를 할 수 있었다.

Http Response(text/html)을 통해 웹 페이지 호출, Html Form 데이터 전송도 할 수 있다.

그러나, HTML 작성하는 것이 불편 복잡할 뿐더러 동적 HTML을 만드는 것은 거의 불가능하다. 

이러한 한계를 극복해서 나온 것이 템플릿 엔진이고, 그 중 하나가 JSP이다.

### 2.2 JSP의 단점

HTML 작성에서의 서블릿의 한계를 극복하기 위해 등장한 템플릿 엔진이 JSP이다. 

그러나, JSP에서는 뷰를 생성하는 HTML 코드부터, 요청 응답과 같은 JAVA 로직까지 같이 섞여 있어서 유지보수 및 가독성이 굉장히 떨어진다.

유지보수성과 가독성을 높이기 위해 뷰(HTML)과 컨트롤러(JAVA 코드)를 분리해야 한다. 여기서 등장한 개념이 **MVC 패턴**이다. 

***

## 3. MVC 프레임워크

MVC는 각각 M(Model), V(View), C(Controller)를 의미한다. Controller은 Request 받아 비즈니스 로직을 처리한다. 

Controller를 통해 생성한 정보를 담아두는 곳이 Model이다. View에서는 Model을 받아 웹 페이지를 띄우게 된다.

### 3.1 Controller V1: 프론트 컨트롤러

여러 컨트롤러에서 공통으로 처리해야할 부분이 존재할 수 있다. 그러므로, 각 컨트롤러에서 공통으로 처리할 부분을 개별적으로 처리하는 것은 비효율적이다.

**그러므로, 공통 기능을 처리하기 위한 프론트 컨트롤러가 필요하다.**

프론트 컨트롤러는 아래와 같이 서블릿을 통해 구현할 수 있다.

```java
@WebServlet(name="frontControllerServletV1", urlPatterns="/front/controller/v1/*")
public class FrontControllerV1 extends HttpServlet {
   private map<String, ControllerV1> controllerMap = new HashMap<>();
   
   public FrontControllerV1() {
       controller.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
       controller.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
       controller.put("/front-controller/v1/members", new MemberListControllerV1());
    }
   
   @Override
   public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        
        ControlleV1 controllerV1 = controllerMap.get(requestURI);
        controllerV1.service();
     }
}

public class MemberFormControllerV1 extends ControllerV1 {
  
   @Override
   public void process(HttpRequestServlet request, HttpResponseServlet Response) throws HttpException, IOException {
         String viewPath = "/WEB-INF/views/new-form.jsp";
         RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
         dispatcher.forward(request, response);
     }
}
 
public class MemberSaveControllerV1 extends ControllerV1 {

   private MemberRepository memberRepository = MemberRepository.getInstance();
 
   @Overried
   public void process(HttpRequestServlet request, HttpResponseServlet Response) throws HttpException, IOException {
   
        String username = request.getParameter("name");
        int age = Integer.parseInt(reqeust.getParameter("age"));
        
        memberRepository.save(new Member(username, age));
        
         String viewPath = "/WEB-INF/views/save-result.jsp";
         RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
         dispatcher.forward(request, response);
    }   
}
```

위의 코드를 확인해보면, 뷰(view)로 렌더링하는 코드가 항상 중복되고 있다.

```java
String viewPath = "...";
RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
dispatcher.forward(request, response);
```

### 3.2 Controller V2: 뷰 컨트롤러

뷰(View)로 렌더링하는 부분을 따로 컨트롤러로 생성해 공통으로 처리할 수 있도록 만드는 것이 뷰 컨트롤러이다.

```java
public class MyView {
    private String viewPath;
    
    public MyView(String viewPath) {
       this.viewPath = viewPath;
    }
    
    public void render(HttpServletRequest request, HttpServletResponse response) throws HttpException, IOException {
          RequestDispatcher dispatcher = request.getRequestDispatcher(this.viewPath);
          dispatcher.forward(request, response);
     }
}

public interface ControllerV2 {
   public MyView process(HttpServletRequest request, HttpServletResponse response) throws HttpException, IOException;
}

...

public class MemberSaveControllerV1 extends ControllerV1 {

   private MemberRepository memberRepository = MemberRepository.getInstance();
 
   @Overried
   public MyView process(HttpRequestServlet request, HttpResponseServlet Response) throws HttpException, IOException {
   
        String username = request.getParameter("name");
        int age = Integer.parseInt(reqeust.getParameter("age"));
        
        memberRepository.save(new Member(username, age));
        
         return new MyView("/WEB-INF/views/save-result.jsp");
    }   
}
```

### 3.3 Controller V3: Model 추가

프론트 컨트롤러와 뷰 컨트롤러를 통해 처음에 공통으로 작업해야 할 부분과 뷰로 렌더링해야할 부분을 리팩토링했다. 즉, 컨트롤러(C)와 뷰(V)가 분리되었다.

그러나, HTTP 메세지를 직접적으로 받는 곳은 프론트 컨트롤러이다. 프론트 컨트롤러를 제외한 컨트롤러들에서 HttpServlet을 사용해야할 이유가 있을까? 없다.

여기서 등장한 개념이 Model이다. 흐름은 아래와 같다.

1. 프론트 컨트롤러에서 HTTP 요청을 받고, 요청에 맞는 컨트롤러를 호출한다.

2. URI와 HTTP 헤더 정보, 쿼리파라미터, 메세지바디 내용을 <String, String> 형태로 저장할 수 있는 파라미터 Map을 생성한다.

3. 파라미터 Map을 컨트롤러에 전달한 후 로직을 실행한다. 이 과정에서 **Model**이 생성되어 반환된다.

4. Model에는 뷰 렌더링을 위한 정보가 존재한다. 

5. 그리고 뷰 리졸버를 통해 렌더링한다.

코드로 나타내면 아래와 같다.

```java 

public class FrontControllerV3 extends HttpServlet {
   private Mep<String, ControllerV3> controllerMap = new HashMap<>();
   
   ...
   
   protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, HttpException {
       String requestURI = request.getRequestURI();
       
       ControllerV3 controller = controllerMap.get(requestURI);
       
       Map<String, String> paramMap = getParamMap(requestURI);
       
       // request 객체로 필요한 변수들을 paramMap에 넣는다.
       
       Model model = controller.process(paramMap);
       
       String viewName = model.getViewName();
       
       MyView myView = viewResolver(viewName);
       view.render(model.getModel(), request, response);
    }
}
```

이것이 M(Model), V(View), C(Controller) 아키텍처이다.
       
### 3.4 Controller V4: 수정된 Controller V3

Controller V3에서는 컨트롤러의 로직이 끝나면 항상 Model에 렌더링에 필요한 변수와 논리주소를 반환한다. 

그러나, 매번 컨트롤러로부터 Model을 반환 받지 않고 렌더링할 뷰의 논리 주소만 반환받으면 된다.

Controller V3의 코드를 아래처럼 리팩토링 할 수 있다.

```java 

public class FrontControllerV4 extends HttpServlet {
   private Mep<String, ControllerV4> controllerMap = new HashMap<>();
   
   ...
   
   protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, HttpException {
       String requestURI = request.getRequestURI();
       
       ControllerV4 controller = controllerMap.get(requestURI);
       
       Map<String, String> paramMap = getParamMap(requestURI);
       Map<String, Object> model = new HashMap<>();
       
       // request 객체로 필요한 변수들을 paramMap에 넣는다.
      
       String viewName = controller.process(paramMap, model);
       
       MyView myView = viewResolver(viewName);
       view.render(model.getModel(), request, response);
    }
}
```

### 3.5 Controller V5: 유연한 Front Controller

Controller V4까지 발전시키면서 MVC 아키텍처를 확립할 수 있었다. 

그러나, 프론트 컨트롤러에서는 하나의 종류의 컨트롤러밖에 받을 수 없다는 한계가 존재한다.

실제로 어떤 상황에서는 V3, 또 다른 상황에서는 V4 컨트롤러가 존재할 수 있다. 다양한 종류의 컨트롤러를 쓸 수 있도록 유연하게 리팩토링 하도록 하자.

다형성을 활용한 리팩토링이다. 이 구조가 실제 스프링 MVC와 유사하다.

0. Http Request가 발생한다.

1. Http Request를 처리하기 위한 핸들러(컨트롤러)를 선택한다.

2. 핸들러를 실행할 수 있는 핸들러 어댑터를 선택해 컨트롤러의 로직을 수행한 후, Model을 반환한다.

3. 뷰로 렌더링한다.

```java
@WebServlet(name="frontControllerServletV5", urlPatterns="/front-controller/v5/*")
public class FrontControllerV5 extends HttpServlet {

   private final Map<String, Object> handlerMappingMap = new HashMap<>();
   private final ArrayList<HandlerAdapter> handlerAdapter = new ArrayList<>();
   
   public FrontControllerV5() {
      initHandler(); // 필요한 핸들러(컨트롤러) 저장
      initHandlerAdapter(); // 필요한 핸들러 어댑터 저장
    }
   
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws HttpException, IOException {
      String requestURI = request.getRequestURI();
      
      Object handler = handlerMappingMap.get(requestURI);
      
      HandlerAdapter handlerAdapter = getHandlerAdapter(handler); // 핸들러에 해당되는 어댑터 찾기
      
      Model model = handlerAdpater.handle(request, response, handler); 
      
      View view = viewResolver(model.getViewName());
      view.render(model.getModel(), request, response);
   }
}
```

핸들러 어댑터를 통해 다양한 컨트롤러를 받을 수 있으므로 프론트 컨트롤러를 확장해 다양한 컨트롤러를 사용할 수 있다.

***


## 4. 스프링 MVC의 기본 구조

스프링 MVC 패턴은 앞서 구현한 ControllerV5 패턴과 매우 유사하다.

스프링 MVC에서 사용되는 모듈들은 아래와 같다.

+ DisPatcherServlet: 프론트 컨트롤러와 유사한 기능을 한다. 이를 확장해 다양한 핸들러를 사용할 수 있다.
+ HandlerMapping: Request에 맞는 핸들러(컨트롤러)를 찾는다.
+ HandlerAdapter: 핸들러를 실행할 수 있게 하는 어댑터를 찾는다.
+ ModelView: 실행된 핸들러(컨트롤러)의 결과물(Model, ViewName)을 반환하는 객체이다.
+ ViewResolver: 뷰를 반환하는 객체이다.
+ View: 뷰를 실행한다.

### 그러므로, 제공된 MVC 아키텍처 위에서 개발자는 컨트롤러만 만들면 된다.



스프링 컨테이너는 스프링 빈들을 싱글톤으로 관리하는 인스턴스이다. 그러므로, 컨트롤러 또한 싱글톤 빈으로 등록해서 사용할 수 있다.

컨트롤러를 스프링 빈으로 등록하기 위해 @Controller, @RequestMapping, @PostMapping, @GetMapping 등의 Annotation을 사용한다.

```java
@Controller
@RequestMapping("/members")
public class MemberControllerV2 {

  private MemberRepository memberRepository = new MemberRepository();
  
  @RequestMapping("/new-form")
  public ModelAndView newForm() {
     return new ModelAndView("new-form");
    }
  
  @RequestMapping("/save")
  public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
      String userName = request.getParameter("username");
      int age = Integer.parseInt(request.getParameter("age"));
      
      Member member = new Member(userName, age);
      memberRepository.save(member);
      
      ModelAndView mv = new ModelAndView("save-result");
      mv.addObject(member);
      return member;
    }
}
```

이와 같이 Annoation을 통해 스프링 빈으로 ModelView를 반환하는 컨트롤러(핸들러)를 등록할 수 있다. 

물론, 다양한 핸들러 어댑터가 존재하므로 다른 종류의 컨트롤러(핸들러)를 빈으로 등록할 수 있다.

```java
@Controller
@RequestMapping("/members")
public class MemberControllerV3 {

  private MemberRepository memberRepository = new MemberRepository();
  
  @GetMapping("/new-form")
  public String newForm() {
     return "new-form";
    }
  
  @PostMapping("/save")
  public String save(@RequestParam("username") String name, @RequestParam("age") int age, Model model) {      
      Member member = new Member(userName, age);
      memberRepository.save(member);
      
      model.addObject(member);
      return "/save-result";
    }
}
```

***

## 5. 스프링 MVC의 기능

### 5.1 Logging을 위한 라이브러리: Slf4j

```java
 private Logger logger = LoggerFactory.getLogger(getClass());
 
 logger.info("data = {}", data); // 이런 방식으로 사용하자
 logger.info("data = " + data); // 이렇게 사용하지 말 것, 로그보다 + 연산이 먼저 실행되기 때문이다.
```

### 5.2 RequestMapping 기본

RequestMapping은 Request로 들어오는 Http 메서드, 경로 변수, 파라미터, MessageBody 등을 매핑해 컨트롤러에 전달하기 위한 Annotation이다.

컨트롤러는 @Controller와 @RestController로 구분된다. 전자는 반환 값이 String이면 뷰 이름으로 인식되어 뷰를 찾아 렌더링된다.

그러나, 후자는 반환 값으로 뷰를 찾는 것이 아니고 HTTP 메세지 바디로 바로 입력되어 response된다.

다양한 경우가 존재할 수 있다. 아래 코드를 통해 살펴보도록 하자.

```java
@RestController
@RequestMapping
public class MappingController {
   private Logger log = LoggerFactory.getLogger(getClass());
   
   @GetMapping("/hello-basic") 
   public String helloBasic() {
      return "helloBasic";
   }
   
   /*
     경로변수(PathVairable) 사용
   */
   @GetMapping("/users/{userId}/orders/{orderId}")
   public String userOrderPathVariables(@PathVariable String userId, @PathVariable Long orderId) {
      log.info("userId: {}, orderId: {}", userId, orderId);
      return "ok";
   }
   
   /* 
     특정 헤더 조건 매핑: 특정 헤더(헤더 값)을 갖는 request만 매핑된다.
   */
   @GetMapping(value="mapping-header", headers="mode=debug")
   public String mappingHeader() { ... }
   
   /*
     Content-Type 조건 매핑: 특정 Content-Type만 매핑되도록 함.
   */
   @PostMapping("/save", consumes={"application/json", ...});
   public String save() { ... }
```

### 5.3 Http Request: 헤더 정보 조회

헤더에는 Content-Type, Length, 쿠키 등 다양한 정보들이 들어있다. 이를 확인하는 방법은 아래에 나타난다.

```java
@RestController
@RequestMapping
public class RequestHeaderController {
  
    @GetMapping("/headers")
    public String headers(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod, Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader(value = "host", required = false) String host,
                          @CookieValue(value = "myCookie", required = false) String cookie
                          ) {
                  ...
                  return "ok";
                  }
  }
```

### 5.4 Http Request Parameter: @RequestParam

@RequestParam Annotation을 통해 요청 파라미터들을 쉽게 받을 수 있다.

또한 @ResponseBody Annotation을 통해 뷰 조회를 무시하고 Http MessageBody에 직접 내용을 입력한다.

```java
@ResponseBody
@RequestMapping("/request")
public String requesting(@RequestParam(required=false) String name, @RequestParam(required=faslse) int age) {
    log.info("name={}, age={}", name, age);
    return "true";
  }
```

required 옵션을 통해 해당 파라미터를 선택적, 또는 필수적으로 받을 수 있다.

null 값을 받을 수 없는 기본 타입(int, char)의 경우에는 defalutValue 옵션을 사용하도록 한다. 그렇지 않으면 에러가 발생할 수 있다.

**Map 형태로 RequestParameter를 받을 수도 있다!**

```java
@ResponseBody
@RequestMapping("/...")
public String requesting2(@RequestParam Map<String, object> paramMap) {
     ...
  }
```

### 5.5 Http Request Parameter: @ModelAttribute

실제 개발 상황에서 Request 요청으로부터 파라미터(@RequestParam)를 받아 특정 객체에 바인딩한 후 비즈니스 로직을 처리한다.

매번 @RequestParam을 통해 파라미터를 받은 후 객체에 바인딩하는 번거로운 작업을 줄이기 위해서 @ModelAttribute 사용해 자동화할 수 있다.

@ModelAttribute를 통해 바인딩하면 자동으로 Model에 객체가 포함된다.

```java
@Data
public class Member {
   private String name;
   private int age;
 }

...

@ResponseBody
@RequestMapping("/model-attribute")
public String modelAttribute(@ModelAttribute Member member) {
     log.info("name={}, age={}", member.getName(), member.getAge());
     return "true";
 }
```

클래스에 @Data Annotation 선언 시, @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 자동 적용된다.

@ModelAttribute는 member 객체의 **Setter**를 호출해 적절한 파라미터를 바인딩한다.

### 5.6 Http Request Message: 단순 텍스트

Request Parameter는 @RequestParam, @ModelAttribute Annotation을 사용해 받을 수 있었다.

그러나, Message Body를 통해 전달을 받을 때는 다른 방법을 사용해야한다.

Message Body를 받을 수 있는 아래의 4가지 방법을 살펴보도록 하자.

```java
@ResponseBody
@PostMapping("/...")
public void messageBody1(HttpServletRequest request, HttpServletResponse response) {
    ServletInputStream inputStream = request.getInputStream();
    String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    
    log.info("messageBody={}, messageBody");
    
    response.getWriter.write("true");
 }
 
@ResponseBody
@PostMapping("/...")
public void messageBody2(InputStream inputStream, Writer responseWriter) {
    String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    responseWriter.write("true");
 }
 
@ResponseBody
@PostMapping("/...")
public HttpEntity<String> messageBody3(HttpEntity<String> httpEntity) {
    String messageBody = httpEntity.getBody();
    return new HttpEntity<>("true");
 }
 
@ResponseBody
@PostMapping("/...")
public String messageBody4(@RequestBody String messageBody) {
      log.info("message={}, messageBody);
      return "true";
 }
```

HttpServlet, InputStream, HttpEntity, @RequstBody, 총 4가지 방법으로 messageBody를 받을 수 있었다.

**@RequestBody가 가장 많이 사용되는 방법이다.**

### 5.7 Http Request Message: JSON

Json 형식의 데이터를 조회하는 것이 Http API에서 주로 사용되는 방식이다.

단순 텍스트와 마찬가지로 HttpServletRequest, InputStream, @RequestBody와 ObjectMapper를 사용해 객체에 바인딩해서 사용할 수 있다.

그러나, @ModelAttribute처럼 한번에 Json형식을 객체에 바인딩하는 방법도 존재한다.

```java
@ResponseBody
@PostMapping("/...")
public Member messageBody5(@RequestBody Member member) {
     log.info("name={}, age={}", member.getName(), member.getAge());
     return member;
 }
```

Http 메세지 컨버터를 통해 다양한 형식의 messageBody를 반환하는 @RestController를 생성할 수 있다.


### 5.8 Http Response: 정적 리소스, 뷰 템플릿

컨트롤러의 로직을 실행한 후, 결과적으로 뷰를 호출하는 메커니즘을 다시 생각해보도록 하자.

1. 컨트롤러(핸들러)에 맞는 어댑터를 찾는다.

2. 어댑터를 통해 컨트롤러 로직을 실행한다.

3. 컨트롤러 로직을 마친 후, 필요한 ModelAndView를 반환한다.

4. 해당 ModelAndView를 통해 뷰를 호출하는 뷰 리졸버를 찾아 뷰를 호출한다.

코드를 통해 더 명확하게 파악할 수 있다. model에 필요한 정보를 담아 뷰로 렌더링한다.

```java
@RequestMapping("/controller")
public String responseviewController(Model model) {
     Model.addAttribute("data", "...");
     return "/response/hello";
 }
```

### 5.9 Http Response: Http API

@RestController를 통해 뷰로 렌더링 하지 않고, 메세지를 직접 response할 수 있다.

이 과정은 앞선 내용으로부터 충분히 알 수 있으므로 생략하도록 한다.

추가적으로, ResponseEntity<>를 통해 Http 응답 상태 코드를 설정할 수 있다.


### 5.10 Http 메세지 컨버터

@RestController를 통해 뷰 리졸버를 호출하지 않고 바로 Http Message를 반환할 수 있다. 

이때, 반환 타입이 String, 객체라면 각 text, Json으로 변환된다.

대표적인 Http 메세지 컨버터는 아래와 같다. request, response에서 모두 사용된다. 오버로딩도 가능하다.

+ ByteArrayHttpMessageConverter
+ StringHttpMessageConverter
+ MappingJackson2HttpMessageConverter


### 5.11 핸들러 어댑터 구조

핸들러(컨트롤러)를 실행하기 위해서는 핸들러 어댑터가 필요했다. 그러나, 핸들러(컨트롤러)를 생성하면 이에 맞는 어댑터가 자동으로 매핑되었다.

어떤 메커니즘으로 이것이 가능할까? 답은 @RequestMapping Annotation에 있다.


핸들러 어댑터는 다양한 종류의 핸들러(컨트롤러)를 실행하기 위해서 필요하다.

각 컨트롤러의 종류마다 서로 다른 파라미터를 받을 수 있다. 

HandlerMethodArgumentResolver를 호출해 핸들러(컨트롤러)가 필요로 하는 파라미터 값을 생성해 바인딩한다.

그러므로, 사용자가 직접 ArgumentResolver의 인터페이스를 확장할 수 있다.


***
