# 스프링 MVC 1

**스프링 웹 MVC를 공부하는 이유는 바람직한 WAS(Web Application Server)를 만들기 위함이다.** 


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

### 1.2 HTTP Request 데이터 1: GET(쿼리 파라미터 사용)

.../uri?[queryParamter] 형식으로 이루어진 url이다. Http message Body가 없다는 것이 특징이다.

서블릿을 통해 HTTP GET Request를 받아 Response하는 자바 코드는 아래와 같다.

```java
@WebServlet(name="requestParamServlet", urlPatterns="/request-param")
public class RequestParamServlet extends HttpServlet {
  
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throw ServletException, IOException {
       
       
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

1.2의 request의 쿼리 파라미터가 messageBody로 전달되고, 메서드는 GET에서 POST로 바뀌었다.

**messageBody에 내용을 포함해 전송할 때는 반드시 Content-Type을 지정하는 것에 유의하도록 한다.**

그러므로, messageBody의 유무는 Content-Type이 null인지 아닌지로 판단할 수 있다.

HTML FORM의 경우의 Content-Type은 application/x-www-form-urlencoded이다.

```java
@WebServlet(name="requestBodyStringServlet", urlPatterns="/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
  
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throw ServletException, IOException {
       
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throw ServletException, IOException {
       
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throw ServletException, IOException {
       
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throw ServletException, IOException {
       
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
