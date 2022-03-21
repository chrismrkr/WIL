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

**Request Start-line**

+ Http Method
+ Request URL/URI
+ QueryParameter(QueryString)

**Request Header**
 
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


