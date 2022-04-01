
## HTTP 기본지식: Spring MVC를 이해하기 위한 선수지식


### 1. 인터넷 네트워크
+ **IP**: 도착 ip와 데이터를 담아 패킷 단위로 전송한다.
+  **TCP**: 연결지향, 데이터 손실 없음, 순서 보장
+  **UDP**: ip + 단순 port번호, 속도가 빠름
+  **DNS**: ip와 특정 도메인(string)으로 매칭해 변환할 수 있도록 한다.

### 2. URL 구성
**"https://www.google.com:888/find?q=hello " 와 같은 URL이 있다고 하자.**
+ 프로토콜: https
+ 호스트명: www.google.com
+ 포트번호: 888
+ Path: find
+ 쿼리 파라미터: q=hello

***

### 3. HTTP 개념 및 특징

**HTTP란 응용(L5) 계층의 프로토콜이다. 서버 간 데이터를 통신할 때 사용된다.**

+ stateless(무상태): 응답 서버가 Response 중간에 바뀌어도된다. TCP 프로토콜을 사용하기 때문이다.

무상태 특성 때문에 여러 서버의 증설이 가능하다.

그러나, 로그인 같은 경우는 상태 저장이 필요하다. 이때는 브라우저의 쿠키와 서버 세션을 사용하도록 하자.

+ 비연결성: 통신을 할 때만 연결하면 된다. 항상 연결하고 있을 필요는 없다.

***

### 4. HTTP 메세지 구조

+ startLine: 로그로 남는 부분
 
Request: HTTP method, Path, (query Parameter), HTTP version 포함

Response: HTTP version, status code 포함

+ Header: 내용 타입(text/plain, json, media, ...), 내용 길이(Content-Length, 1byte 단위)

Header 정보는 사용자가 직접 추가할 수 있다.

+ messageBody: 바이트코드, application/json, text/html 등 다양한 형식으로 request, response 가능

***

### 5. HTTP 메서드

#### 5.1 바람직한 HTTP API?

URL은 항상 리소스 중심으로 만든다. 행위 중심으로 만들지 않는다.

회원을 전체 조회, 조회, 등록, 수정, 삭제하는 HTTP API를 설계해보도록 하자.

#### 5.2 GET

GET ip(DNS)/members/{queryParameter} 형식으로 보낸다. 물론, queryParameter는 생략할 수 있다.

message body는 사용하지 않는다. 데이터를 조회할 때 사용되는 메서드이다.

#### 5.3 POST

POST ip(DNS)/members 형식으로 보낸다. message Body를 통해 서버로 요청 데이터를 전달한다.

리소스를 저장하거나, 기타 요청 데이터(message body, queryParameter)를 처리하는 프로세스를 진행할 때 사용한다.

**이때, 클라이언트는 URI를 알 수 없다.**

그러므로, 서버가 리소스를 관리하는 경우 사용하도록 한다.(ex. members)

#### 5.4 PUT

POST 요청의 리소스 저장과 유사하다. 해당 리소스가 있다면 덮어쓰고 없으면 생성한다.

**그러나, 클라이언트는 URI를 알고 직접 지정한다.**

그러므로, 클라이언트가 리소스를 관리하는경우 사용하도록 한다.(ex. files)

#### 5.5 PATCH

PUT 요청은 리소스가 존재할 때 덮어쓰므로 부분 수정이 불가능하다. PATCH를 통해 리소스 부분 수정이 가능하다.

마찬가지로 클라이언트가 URI를 알고 있다.

#### 5.6 DELETE

리소스를 삭제할 때 사용한다. 마찬가지로 클라이언트가 URI를 알고 있다.


**가급적 POST와 GET을 사용하도록 하자.**

***

### 6. 클라이언트에서 서버로의 데이터 전송

#### 6.1 정적 데이터 조회

정적 데이터 조회 시 GET 메서드 이용한다. ip:portnumber/path

#### 6.2 동적 데이터 조회

동적 데이터 조회 시 GET 메서드를 이용한다. 동적 데이터 조회를 위해 쿼리 파라미터를 사용한다.

ip:portnumber/path/{queryParameter}

#### 6.3 HTML 데이터 전송

Content-Type:application/x-www-form-urlencoded

저장 또는 프로세스 처리를 할 때는 POST 요청을 사용한다. 파라미터들이 message Body에 형식에 맞추어 저장된다.

조회를 할때는 GET 요청을 사용한다. 파라미터들이 url의 쿼리 파라미터로 저장된다.

Content-Type:multipart/form-data를 통해 바이너리 데이터도 전송 가능하다.

**HTML Form은 GET, POST 메서드만 가능하다는 것을 유의하자.**

만약, GET POST 메서드로만 해결하기 어려우면 컨트롤 URI를 만들도록 한다.

#### 6.4 HTTP API 데이터 전송

Json 형식의 데이터를 message Body에 담아 POST, PUT, PATCH 메서드로 전송한다.

이때, URI는 리소스 기반으로 작성하는 것이 바람직하다.
 
***

### 7. 상태코드

+ 1xx: request 처리 중
+ 2xx: request 정상 처리
+ 3xx: re-direct, 작업을 끝내기 위해 추가 작업이 필요함을 의미함.

Location 헤더가 있다면, 그 위치로 자동 이동하도록 함

예를 들어, 주문 요청 후 새로고침을 누르면 중복 주문이 된다. 이를 막기 위해 re-direct가 필요하다.

+ 4xx: 클라이언트 오류
+ 5xx: 서버 오류

***

### 8. HTTP 헤더

+ Content-Type
+ Content-Encoding: message body가 인코딩되었다면 인코딩 방식을 나타냄
+ Content-Language
+ Content-Length
+ Transfer-endcoding: chucke -> 분할 전송

+ **쿠키**

HTTP는 무상태 프로토콜이다. 그러나, 로그인 기능 등에서는 상태를 기억해야한다.

이러한 한계를 위해 등장한 개념이 쿠키이다. 서버가 쿠키를 함께 response하면 클라이언트는 받은 쿠키를 저장한다.

그 이후 클라이언트의 모든 request에 쿠키가 자동으로 포함된다.

헤더에 Set-Cookie: ... 를 추가해 사용한다.

+ **캐시**

클라이언트가 서버로 큰 용량의 데이터를 여러번 받아올 때 사용할 수 있다.

response 헤더에 cache-control: max-age=60(클라이언트가 해당 메세지를 60초간 캐시로 저장)를 붙여 사용 가능하다.

Last-Modified: date ETag 를 통해서도 더 효율적인 캐싱 가능하다.



