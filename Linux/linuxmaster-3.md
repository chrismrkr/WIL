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
- cmake 설치 : ```yum install cmake```
- 소스코드 및 boost 라이브러리 다운로드
  - wget 명령어로 boost.tar.gz, mysql-boost-[version].tar.gz 다운로드 후 압축 해제
- 소스코드 빌드 설정 실행
  - cmake -DCMAKE_INSTALL_PREFIX=/usr/local/mysql ... 명령어로 환경설정
  - ```cmake -L``` 설정된 옵션 확인 가능
- 소스코드 컴파일 및 빌드
  - ```make && make install```
- 설치 확인
  - /usr/local/mysql 내용 확인
  - ```mysql --version``` 명령어로 확인
  - ```useradd -d /usr/local/mysql mysql``` 명령어로 계정 생성
  - /var/log/mysqld.log에서 임시 root 계정 비밀번호 확인 가능
- 기본 DB 및 테이블 생성
  - ```mysqld --initialize```    
##### 1.1.3.4 PHP 소스코드 컴파일 및 설치
- 소스코드 다운로드
  - wget php-[version].tar.bz2 다운로드 후 압축 해제
- 소스코드 빌드 설정(cmake)
  - ```./configure --prefix=/usr/local/php --with-apxs2=/usr/local/apache/bin/apxs --with-mysql=/usr/local/mysql --with-config-file-path=/usr/local/apache/conf```
- 소스코드 컴파일 및 설치
  - ```make && make install```
- 설치 확인
  - ```php --version```
- php.ini 파일 복사 후 아파치 웹 서버 재시작
  - cp ./php.init-production /usr/local/apache/conf/php.ini
  - /usr/local/apache/bin/apachectl restart
- 연동 확인
  - echo "<?php phpinfo(); ?> > /usr/local/apache/htdocs/sample.php
##### 1.1.3.5 패키자 관리자를 이용한 APM 설치 및 연동
- 아파치 웹 서버 설치 및 기본 동작 확인
  - ```yum -y install httpd```
  - ```netstat -nlp | grep httpd```
- MySQL 설치와 기본 동작 확인
  - ```yum -y install [url]```
  - ```service mysqld start``` 또는 ```systemctl start mysqld.service```
  - ```service mysqld status``` 또는 ```systemctl status mysqld.service```
- MySQL root 패스워드 변경
  - /var/log/mysqld.log에서 root 계정 임시 비밀번호 확인 가능
  - ```mysqladmin -uroot -p'[임시 비밀번호]' password [신규 비밀번호]
- MySQL 접속
  - mysql -uroot -p'[비밀번호]'
- PHP 설치 및 기본 동작 확인
  - ```yum -y install php php-mysql```
  - ```php -r 'echo "hello world";'
- 아파치 웹 서버 php 설정 변경
  - vi /etc/httpd/conf/httpd.conf에서 변경
- 아파치 웹서버 재시작
  - ```apachectl restart```
- 아파치, MySQL, php 연동 테스트
- 방화벽 설정
###### 1.1.3.6 아파치 웹 서버의 구조와 세부 설정
- **실습 및 기출 문제 위주로 진행하며 정리할 것**
- /etc/httpd/conf에 기본 환경설정 파일인 httpd.conf 파일이 존재함
- httpd.conf 파일에서 Include conf.d/*.conf로 모든 설정 파일을 포함함
- httpd.conf 주요 설정 항목
  - ServerRoot : 아파치 서버의 주요 파일이 저장된 최상위 디렉터리 절대경로 설정
  - Listen : 포트번호 지정
  - LoadModules : DSO(Dynamic Shared Object) 방식으로 로드할 모듈 지정
  - User : 데몬 실행 사용자 권한 지정
  - Group : 데몬 실행 그룹 권한 지정
  - ServerAdmin : 어드민 이메일 설정. 에러 발생 시 해당 메일로 에러 메세지 전달
  - ServerName : 호스트명 입력
  - DocumentDirectory : Docment Root 디렉토리를 지정함
  - Directory : 지정한 디렉토리에 대한 권한, 제어, 옵션 등을 설정함
  - FileMatch : 지정된 패턴에 맞는 파일에 대한 권한, 제어, 옵션 등을 설정함
  - AllOverride
  - LogLevel
    - /etc/rsyslog.conf 내 우선순위와 동일함
  - ifModule : 지정한 모듈에 대한 세부 동작 옵션 설정
    - log_config_module : 로그 레벨
    - mod_userdir.c : 사용자 별 홈 페이지 사용여부 설정(사용자 A는 a 홈페이지, B는 b 홈페이지..)
- httpd-vhosts.conf
  - 하나의 ip 주소로 여러개의 도메인(호스트)를 설정할 수 있음
- default.conf, http-default.conf
  - 기본 설정 값이 들어 있음

### 1.2 인증 관련 서비스
#### 1.2.1 인증 관련 서비스 개요
##### 1.2.1.1 리눅스 인증의 개요
- 리눅스 기본 인증은 /etc/passwd, /etc/shadow에서 관리됨
- 리눅스 시스템 사용자가 많으면 네트워크 인증이 필요함(NIS, LDAP)
##### 1.2.1.2 NIS와 LDAP 서비스의 주요 특징
- NIS(Network Information Service)
  - 시스템에 등록된 사용자 계정 정보를 네트워크를 통해 다른 시스템에 제공
  - 여러 호스트는 동일한 계정 정보를 이용할 수 있음
  - telnet, samba, ssh 등을 통해 사용자 인증 가능
- LDAP(Lightweight Directory Access Protocol)
  
#### 1.2.2 NIS 사용하기
##### 1.2.2.1 NIS 서버 설치 및 구성
- RPC 데몬 구동
  - 서버 및 클라이언트 모두 원격 통신을 위해 RPC 데몬 구동 필요
  - ```systemctl start rpcbind```
  - 호스트 파일에 도메인 등록 : /etc/hosts
- NIS 서버 설치
  - ```yum -y install ypserv```
  - /usr/lib/systemd/system에 NIS 관련 서비스 파일이 설치됨
- NIS 도메인명 등록
  - 명령어 사용 : ```nisdomainname [도메인명]```
  - /etc/sysconfig/network에 설정하면 재부팅 후에도 도메인이 사라지지 않음
- NIS 사용자 계정 생성
  - NIS 클라이언트에서 사용할 계정 생성 : ```useradd nisuser```
- NIS 관련 데몬 실행
  - ypserv
  - yppasswdd
  - ypxfrd
- NIS 정보 갱신 및 적용 : ```make -c /var/yp```
##### 1.2.2.2 NIS 클라이언트 설치와 구성
- RPC 데몬 구동
- NIS 클라이언트 설치 : ```yum -y install ypbind yp-tools```
- NIS 도메인명 설정
- NIS 서비스(서버)와 도메인 정보 설정
   - /etc/yp.conf에 설정
- NIS 클라이언트 데몬(ypbind) 실행
##### 1.2.2.3 NIS 관련 주요 명령어
- nisdomainname : NIS 도메인 이름 설정 또는 도메인 확인
- ypwhich : NIS를 통해 로그인 후, 인증에 사용한 NIS 서버 도메인 확인
- ypcat : NIS 서버 구성파일 확인
- yptest : NIS 클라이언트에서 NIS 동작, 설정, 도메인명, 사용자 계정 정보 등 확인
- yppasswd : 사용자 비밀번호 변경
- ypchsh : 사용자 셸 변경
- ypchfn : 사용자 정보 변경

### 1.3 파일 관련 서비스
#### 1.3.1 삼바(SAMBA) 서비스 사용하기
##### 1.3.1.1 삼바 서비스 개요
- GPL 라이센스인 자유 소프트웨어로 호스트 간 디렉토리, 파일, 프린터 등을 공유하기 위해 사용함
- 



























  

























































