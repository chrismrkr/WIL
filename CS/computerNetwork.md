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

1. 클라이언트는 local DNS Server에 www.naver.com에 해당되는 ip를 요청한다.
2. local DNS Server는 root Server에 .com 도메인 서버가 존재하는지 질의한다.(Top Level Domain)
3. root Server는 .com 도메인 name Server(TLD Server)를 안내한다.
4. local DNS Server는 TLD Server에 naver.com이 있는지 질의한다.(second level Domain)
5. TLD Server는 nave.com을 아는 Authoritive Server를 안내한다.
6. local DNS Server는 Authoritive Server에 www.naver.com에 해당하는 ip를 질의한다. (Sub domain)
7. ip를 받은 후, 클라이언트에 전달한다.

root Server로 부터 TLD Server를 알아내는데 많은 시간이 걸리므로 이를 local DNS Server에 캐싱하기도 한다.

DNS는 UDP를 이용한다. 매번 연결을 유지할 필요가 없기 때문이다.

***

## 2. 전송 계층(L4. Transport Layer)

프로세스간 통신을 책임지는 계층이다.

### 2.1 UDP

L3 계층의 packet에 src port, dest port를 추가한 segment를 전달한다. 

신뢰성 서비스를 제공하지 않는다. 순서제어, 흐름 제어, 혼잡 제어를 하지 않는다.

### 2.2 TCP

신뢰성(순서 제어, 흐름 제어, 혼잡 제어)을 제공하는 프로세스간 통신 프로토콜이다.

UDP와 달리 연결지향형 프로토콜이다.

#### 2.2.1 순서 제어 방법

슬라이딩 윈도우와 response ACK를 적절히 활용하여 순서를 제어할 수 있다.

예를 들어, 윈도우 크기를 3라고 한다. (ㅇ: 패킷, ■ : 전송 중인 패킷)

1. 시작 : ■ ■ ■ ㅇㅇㅇㅇㅇ 

=> 윈도우 크기만큼 전송하고, timer를 실행한다. timer는 패킷을 마지막으로 보낼 때 시작한다.

**2. 수신자** 

2-1. 순서대로 받고 있는 경우

수신 현황 : ■ ■ ■ ...

=> 그 이후에도 계속 순서대로 패킷이 도착할 수 있으므로 0.5초 대기한다.

2-2. 순서에 어긋나게 도착한 경우

수신 현황 : ■ X ■ ....

=> 연속하여 받은 곳 까지 즉각 ACK 하여 송신자가 그 지점부터 다시 전송할 수 있도록 한다.

2-3. Gap이 채워지는 경우

연속되는 만큼 윈도우를 이동하며 ACK한다.

**3. 송신자**

3-1. 순서대로 ACK를 받는 경우

윈도우를 이동하며 패킷을 전송하고 타이머를 실행한다.

3-2. timeout 발생

윈도우만큼 재전송 한다. 아래의 경우를 보면 이해할 수 있다.

전송 현황 : ■ ■ ■

수신 현황 : ■ X ■

이렇게 되면 수신자는 첫번째 패킷에 대해 ACK를 보낸다. 그러면 송신자는 윈도우를 한 칸 움직이고 전송한다.

전송 현황 : ■ ■ ■ ■

만약 2번째 패킷이 유실된 것이라면, 결국 ACK를 받지 못하므로 timeout이 발생하게 된다.

이에 따라, 송신자는 중복된 ACK를 받을 수도 있다.

만약 중복된 ACK를 3번 이상 받으면, 3번 이상 보내도 받지 못했다는 것이므로 유실로 판정한다.

#### 2.2.2 흐름 제어 방법

수신자가 ACK와 함께 추가적으로 받을 수 있는 byte stream 최대 크기를 송신자에게 전달한다.

수신자의 버퍼 크기 제한으로 인해 발생할 수 있는 문제를 제어한다.

#### 2.2.3 연결 지향 방법

3-way-handshake로 한다. SYNC - ACK - ACK+패킷전송 

2-way-handshake로는 연결 지향을 만족할 수 없다.

송신자 : sync1-----timeout-----------------sync2-----TCP1연결--------TCP2연결------

수신자 : -------------------ack1(TCP1 연결)---------ack2(TCP2 연결)----------------

의도하지 않게 2개의 TCP Connection이 생성될 수 있다.

TCP 연결 종료는 4-way-handshake로 이루어진다.

#### 2.2.4 혼잡 제어 방법

혼잡 제어란 라우터에서 발생하는 큐잉에 의한 지연을 제어하는 것을 의미한다.

대표적으로 AIMD가 있다.

패킷이 정상적으로 도착해 ACK를 받으면 윈도우의 크기를 1씩 증가시키는 방식이다.

timeout이 발생하면 윈도우 크기를 1로 줄이고,

데이터 유실이 발생하면 윈도우 크기를 절반으로 줄인다.

***

## 3. 네트워크 계층(L3. Network Layer)

컴퓨터간 통신을 위한 책임지는 계층이다.

L3 계층에서는 순서 및 흐름을 제어하지 않는다.

+ 포워딩 : 어느 output으로 보낼지를 결정한다.
+ 라우팅 : 패킷을 보낼 전체 경로를 결정한다.

### 3.1 Control Plane : 라우팅 경로를 어떻게 설정할 것인가?

SDN(Software Defined Network) 소프트웨어로 부터 테이블을 생성해 라우터에 전달하고, 라우터는 테이블을 이용해 경로를 설정한다.

### 3.1.1 Link State

다익스트라 알고리즘을 통해 라우팅 경로를 생성하는 방식이다. 모든 라우터가 경로 정보를 공유해야 한다.

경로 정보가 바뀌면 계속해서 라우팅 경로가 변경된다는 단점이 있다. 

### 3.1.2 Distance Vector

벨만 포드 알고리즘을 이용한다. D(x, y) = min{C(x, v) + D(v, y)}

이웃으로 부터 정보를 전달받고, 경로를 위의 식을 통해 재계산 한다. 그리고, 다시 이웃에게 이것을 알린다.

Link State와 달리 중앙화된 방식은 아니다.

### 3.2 Data Plane : 어느 output으로 포워딩할 것인가?



## 4. 데이터링크 계층(L2. Datalink Layer)

## 5. 네트워크 보안

### 5.1 보안의 3대 요소

+ 기밀성 : 허가되지 않는 사람은 접근하지 못한다.
+ 무결성 : 인가되지 않은 사람에게 데이터가 수정되지 않는다.
+ 가용성 : 특정 시간 동안만 데이터에 접근할 수 있다.


### 5.2 네트워크 보안 중요 개념

+ 대칭키 암호화 : 상호 간에 동일한 키를 공유함
+ 비대칭키 암호화 : 공개키를 통해 암호화하고 비공개키를 통해 복호화함.
+ DES : 대칭키를 통해 암호화 및 복호화함. 
+ CSRF

CSRF는 쿠키가 서브 도메인에서는 공유될 수 있다는 특징을 이용해서 생겨난 공격이다.

공격 방법은 아래와 같다. 서버는 쿠키-세션 기반으로 인증된다는 것을 가정한다.

1. 사용자가 로그인한다. 인증 완료 후, 웹 사이트에 저장한다.
2. 악의적인 사용자가 웹 사이트 내에 악성 링크를 숨긴다.(게시글 등)
3. 사용자가 악성 링크를 클릭하면 자동으로 쿠키(SessionId)와 악성 Action을 서버로 요청한다.
4. 서버는 인증된 사용자로부터의 요청이므로 이를 받아들인다.

방어방법 : referer(host에 할당되는 unique token) 사용. 스프링 시큐리티에서는 기본적으로 CSRF 공격을 방어할 수 있음.


+ XSS

+ SQL Injection


### 5.3 OAuth

페이스북에서 네이버의 캘린더 정보를 가져오겠다 라는 요구사항을 들어주기 위한 프로토콜이다.

#### 5.3.1 핵심 개념

Resource Owner : 개인정보를 소유하는 사람

Resource Server : 개인 정보를 소유하는 서버

Authorization Server : 권한을 제공하는 서버

client : 신규 애플리케이션 서버

#### 5.3.2 동작 과정

-> 사용자 : 신규 client 접근 시도

-> client : client ID와 redirect url 제공, 이를 통해 authroziation server에 로그인 페이지를 요청

-> authorization server : 로그인 페이지 제공

-> 사용자 : ID / PW 입력

-> authorization server : 인증번호(authorization code) 발급하여 사용자에게 제공

-> 사용자 : client에게 authorization code를 전달하여 access token을 받아올 것을 요청

-> client : authorization server에게 authorization code를 제공하여 access token 발급 요청

-> authorization server : access token 발급하여 client에 제공

-> client : 로그인 완료 후 access token을 사용자에게 전달

-> 사용자 : access token으로 서비스 요청 가능.


























....


















