# 네트워크 및 서비스 활용

## 1. 네트워크 서비스
### 1.1 웹 관련 서비스
#### 1.1.1 웹의 개념과 구성요소
##### 1.1.1.1 웹의 개요
- HTML과 HTTP를 이용하여 구현
##### 1.1.1.2 웹의 구성요소
- HTML
- 웹 서버 : Apache, Nginx 등
- 웹 브라우저
- HTTP 프로토콜
#### 1.1.2 웹의 동작 원리와 HTTP 프로토콜
##### 1.1.2.1 웹의 동작 원리
- 클라이언트 -> 방화벽 -> WEB 서버 -> WAS -> infrastructure
- www.google.com를 브라우저에 입력했을 때 웹 페이지가 표시되는 과정
  - DNS 서버를 통해 www.google.com에 매치되는 ipfmf ckwdma
  - TCP 3-way 방식으로 웹 서버와 연결(80, 443 포트)
  - 요청에 대한 응답을 WEB & WAS로 부터 내려받음
  - 웹 브라우저에 페이지를 렌더링함
  - HTTP 버전에 따라 TCP Connection을 해제할지 말지 결정(4-Way handshake)
##### 1.1.2.2 HTTP 프로토콜
- HTTP 요청 메소드
  - HEAD, GET, POST, PUT, PATCH, DELETE
- HTTP 응답 상태 코드
  - 2XX(성공), 3XX(리다이렉트 또는 캐시), 4XX(클라이언트 에러), 5XX(서버 에러)
    - 401 : 권한 부족(인증 실패)
    - 403 : 권한 없음(인증은 완료된 상태지만 인가 실패)
    - 404 : 리소스 없음
#### 1.1.3 웹 관련 서비스의 설치와 설정
##### 1.1.3.1 웹 서비스 구성을 위한 설치 목록과 고려사항
- 아파치 웹 서버
  - 1.xx 버전 : 프로세스당 하나의 스레드를 갖고 요청을 처리하는 방식(prefork 방식)
  - 2.xx 버전 : 프로세스당 여러 스레드를 갖고, 스레드가 요청을 처리하는 방식(worker 방식)
    - 모듈을 요청마다 동적으로 적재하는 방식을 사용함
  - httpd -v 또는 rpm -qa | grep httpd 명령어로 아파치 설치 확인 가능
- PHP : 웹 서비스 로직을 구현하고 페이지를 동적으로 생성하도록 고안된 언어
- MySQL : 관계형 데이터베이스
- 아파치 및 PHP 소스코드 컴파일 및 설치 
  - configure -> make -> make install
- MySQL 소스코드 컴파일 및 설치
  - cmake -> make install
##### 1.1.3.2 아파치 웹 서버 소스코드 컴파일 및 설치
- APR(Apache Portability Runtime) 설치
  - wget으로 apr-[verison].tar.gz 파일을 다운로드 후 압축 해제
  - 압축 해제된 디렉토리에서 ```./configure --prefix=/usr/local/apr```로 설정 작업
  - ```make && make install```
- APR util 설치
  - wget으로 apr-util.tar.gz 파일 다운로드 후 압축 해제
  - 압축 해제된 디렉토리에서 ```./configure --with-apr=/usr/local/apr```로 설정 작업
  - ```make && make install```
- 아파치 웹 서버 소스코드 다운로드
  - wget으로 httpd-[version].tar.bz2 파일 다운로드 후 압축 해제
  - 압축 해제된 디렉토리에서 ```./configure --prefix=/usr/local/apache --enable-mods-shared=all```로 설정 작업
  - ```./configure --help```로 옵션 확인 가능
  - makefile 생성
- 아파치 웹 서버 소스코드 컴파일 및 설치
  - ```make && make install```
  - /usr/local/apache에서 설치 상태 확인
  - ```httpd -version```으로 설치 상태 확인
- 데몬 설정 및 도메인 설정
  - /etc/rc.d/rc.local에서 데몬 설정
  - /usr/local/apache/conf/httpd.conf에서 웹 서버 도메인 및 ip 설정 가능
##### 1.1.3.3 MySQL 소스코드 컴파일 및 설치


##### 1.1.3.4 PHP 소스코드 컴파일 및 설치









































