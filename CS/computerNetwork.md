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

요청-응답 시간을 줄이기 위해서 사용한다.

server에서 http response header에 cache-control을 지정하여 클라이언트에 응답하고, 프록시 서버에도 이를 저장한다.

**cache-control**

+ max-age : n초 동안 캐시 데이터는 반드시 최신이라는 것을 의미함(Expires도 가능하다.)

age 헤더를 통해 현재 몇초가 지났는지 확인할 수 있다.

캐시의 유효기간이 지난 후 클라이언트가 동일한 요청을 보내면, 캐시가 유효한지 재검증 조건부 요청을 한다.

재검증에 사용되는 헤더로는 If-None-match와 If-Modified-since가 있다.

전자는 캐시된 데이터의 ETag와 서버 데이터의 ETag가 동일한지 비교하는 헤더이다.

후자는 서버 데이터가 If-Modified-since 이후에 변경되었는지를 확인하는 헤더이다.

캐시가 유효하다면 304를 반환하고, 그렇지 않으면 새로운 데이터를 내려준다.

+ no-cache : max-age = 0 과 동일한 의미
+ no-store : 캐시를 사용하지 않겠다는 것을 의미함.

#### 1.1.5 HTTP 버전 비교

HTTP 1.0과 HTTP 1.1의 차이는 persistent connection에 있다.

HTTP 1.0은 TCP 연결 후, HTTP 통신이 끝나면 즉시 TCP 연결을 해제한다.

그러나, HTTP 1.1은 HTTP 통신이 끝난 후, keep-alive 헤더의 시간만큼 TCP 연결을 유지한 후에 해제한다.


HTTP 1.1과 HTTP2.0의 차이는 데이터 전송 방식에 있다.

HTTP 1.1은 FCFS로 데이터를 전송한다. 그러므로, 여러개의 데이터를 동시에 전송할 때는 새로운 TCP 연결이 필요했다.

그러나, TCP 연결을 무한정 할 수 없다는 단점도 있었다.

HTTP 2.0 에서는 TCP 연결을 하나로 하여 Frame 단위로 쪼개어 순서에 상관없이 전송한다.

이에 따라, 클라이언트에게 필요한 데이터를 먼저 pre-fetch하여 Head of line Blocking 문제를 어느정도 해결했다.


### 1.2 SMTP(Simple Mail Transfer Protocol)

이메일 송수신을 위한 프로토콜이다.

사용자가 메일 서버에게 TCP 연결 후 메세지를 전달한 후, 메일 전달 프로세스를 진행한다.

**전달 프로세스**

xxx@gmail.com -> yyy@naver.com 으로 메일을 전달한다고 가정한다.

1. gmail 메일 서버는 naver.com 메일 서버를 찾기 위해 DNS 접속하여 IP 검색
2. 전송한 후, 도착한 곳이 naver.com 메일 서버가 맞는지 확인한다. 그렇지 않은 경우 다시 DNS 접속하여 IP 검색
3. naver.com 메일 서버에 도착할 때 까지 반복한다.

HTTP는 client push, SMTP는 client pull이라는 점에 차이가 있다.

### 1.3 DNS(Domain Name Server)

ip의 별칭, 즉 ip-name, name-ip Map을 갖고 있는 서버를 의미한다.

예를 들어, www.naver.com ip를 찾는 과정은 아래와 같다.



***

## 2. 전송 계층(L4. Transport Layer)



## 3. 네트워크 계층(L3. Network Layer)


## 4. 데이터링크 계층(L2. Datalink Layer)


## 5. 물리 계층(L1. Physical Layer)
