
## HTTP 기본지식



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

### 3. HTTP 개념 및 특징

**HTTP란 응용(L5) 계층의 프로토콜이다. 서버간 데이터를 전송할 때 보통 사용된다.**

+ stateless: 
