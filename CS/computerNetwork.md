# 컴퓨터 네트워크

## 1. 응용 계층(L5. Application Layer)

애플리케이션간 통신을 책임지는 계층이다.

애플리케이션간 통신을 위한 대표적인 프로토콜 몇가지가 존재한다.

### 1.1 HTTP 

HTTP는 클라이언트-서버간 통신을 위한 프로토콜이다. 무상태 프로토콜이다.

80번 포트를 통해 클라이언트-서버 간 TCP Connection 생성 후, 통신을 시작한다.

#### 1.1.1 HTTP 메세지 구조

+ start-line : 메소드, url, query Parameter, http 버전
+ header : request parameter, content-type, content-length, cookie
+ body : http message body(application/json, text 등)

#### 1.1.2 HTTP Method

+ GET : 조회 용도로 사용함. messageBody를 사용하면 convention에 어긋남
+ POST : 등록 용도로 사용함. 
+ PUT : 데이터 덮어쓰기 용도로 사용함. messageBody, PathVariable 사용
+ PATCH : 데이터 부분 수정 용도로 사용함. messageBody, PathVariable 사용
+ DELETE : 데이터 삭제 용도로 사용함. messageBody를 사용하지 않음

#### 1.1.3 HTTP 쿠키

HTTP는 무상태 프로토콜이므로 이전에 통신했던 맥락을 기억하지 않는다. 

그러므로, 데이터 비일관성의 문제 자체가 차단된다는 장점이 있지만 맥락을 기억해야 하는 경우도 있다.

이때 쿠키를 사용할 수 있다.

서버에서 쿠키를 생성해 클라이언트에 전달하고, 클라이언트가 서버와 다시 HTTP 통신을 할 때, Header에 쿠키를 실어서 사용한다.

쿠키는 대표적으로 쇼핑카트, 세션-쿠키 인증 방식에 사용된다.

#### 1.1.4 웹 캐시







## 2. 전송 계층(L4. Transport Layer)


## 3. 네트워크 계층(L3. Network Layer)


## 4. 데이터링크 계층(L2. Datalink Layer)


## 5. 물리 계층(L1. Physical Layer)
