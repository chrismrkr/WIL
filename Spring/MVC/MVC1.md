## 스프링 MVC 1

**스프링 웹 MVC를 공부하는 이유는 바람직한 WAS(Web Application Server)를 만들기 위함이다.** 


### 1. 서블릿

스프링 MVC를 학습하기 전에 서블릿에 대해서 알아보도록 한다. 서블릿은 Http 요청 및 응답을 간단하게 처리할 수 있게 하는 싱글톤 객체이다.

서버로 HTTP 메세지가 도착하면, 헤더 정보를 읽고 메세지를 파싱하는 등 반복되는 작업들을 할 수 밖에 없다.

이러한 반복되는 작업 등이 포함된 HTTP request 및 response를 처리하기 위해 서블릿(Servlet)이 존재한다.


#### 1.1 서블릿 동작 메커니즘

서버가 외부로부터 HTTP Reqeust를 특정 URI로 받는다고 하자. URL을 담당하는 서블릿 싱글톤 객체를 서블릿 컨테이너로부터 가져온다.

호출된 서블릿 객체를 통해 HTTP Request를 HttpServletRequest 객체로 받는다.

그리고 서버의 비즈니스 로직을 실행한 후, 다시 HttpServletResponse 객체로 Http response 메세지를 작성 후 응답한다.
