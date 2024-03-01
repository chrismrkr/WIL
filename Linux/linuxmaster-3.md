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
  - DNS 서버를 통해 www.google.com에 매치되는 ip를 찾음
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
  -  ```--with-config-file-path=[ini파일_저장경로]``` 옵션을 통해 .ini 파일이 저장될 위치 미리 설정 가능
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
  - ```mysqladmin -uroot -p'[임시 비밀번호]' password [신규 비밀번호]```
- MySQL 접속
  - mysql -uroot -p '[비밀번호]'
- PHP 설치 및 기본 동작 확인
  - ```yum -y install php php-mysql```
  - ```php -r 'echo "hello world";'```
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
  - 주요 속성
    - DN : 조직 내 고유 식별자
    - RDN : 상대 RD
    - CN : 전체 이름(성+이름)
    - SN : 성
#### 1.2.2 NIS(Network Information Service) 사용하기
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
  - **/etc/sysconfig/network**에 설정하면 재부팅 후에도 도메인이 사라지지 않음
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
   - **/etc/yp.conf**에 설정
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
- 삼바의 구성요소
  - smdb : 주요 서비스로 ip를 통해 직접 접속 가능(tcp : 445)
  - nmdb : UDP 포트(137, 138)로 호스트를 브로드 캐스팅 방식으로 검색 후, TCP 139 포트를 이용하여 호스트 이름으로 접속
##### 1.3.1.2 삼바 서비스 설치와 구성
- 삼바 관련 패키지 설치 : ```yum -y install samba samba-common samba-client```
- 삼바 서비스 실행 : ```systemctl start smb.service nmb.service```
  - 부팅 시 자동 실행하도록 설정 : ```systemctl enable smb.service nmb.service```
- 삼바 서비스 설정 : /etc/samba/smb.conf
- 삼바 서비스 사용자 등록 및 패스워드 설정
  - /etc/samba/smbusers에서 리눅스 계정과 삼바 사용자 매핑
  - smbpasswd : 삼바 사용자 계정 활성화 및 패스워드 설정
  - pdbedit : 삼바 사용자 목록 및 세부 내용 확인
  - write list : 쓰기 가능한 사용자 지정
  - valid users : 접근 가능 사용자 지정(기본 값 : 모든 사용자)
##### 1.3.1.3 삼바 서비스 이용하기
- 삼바 관련 패키지 설치 : ```yum -y install samba-common samba-client```
- 삼바 서버 접속
  - smbclient 명령어
    - smbclient -L : 삼바 서버의 공유 디렉토리 정보 표시
    - smbclient -M : 메세지 전송
    - smbclient -U : 사용자 이름 지정
    - smbclient -p : TCP 포트번호 지정
    
#### 1.3.2 NFS(Network File System) 서비스 사용하기
##### 1.3.2.1 NFS 서비스 개요
- TCP/IP를 통해 원격 호스트에 있는 파일 시스템을 로컬 호스트에 마운트해서 사용하는 방식
##### 1.3.2.2 NFS 서비스 설치 및 구성
- NFS 관련 패키지 설치 : ```yum -y install rpcbind nfs-utils```
- NFS 서버 설정
  - /etc/exports 파일에서 NFS 서비스 설정
  - echo "/var/test-nfs 172.30.0.0/24(rw,no_root_squash)" >> /etc/exports
    - [서버 디렉토리] [접속 허가 클라이언트 호스트](옵션)
    - 옵션 종류
      - root-squash : root 접근 권한 거부
      - no-root-squash: root 접근 권한 허용
  - mkdir /var/test-nfs
  - chmod 666 /var/test-nfs
- NFS 관련 데몬 실행 : ```systemctl start rpcbind nfs-server``` ```systemctl enable rpcbind nfs-server```
- NFS 서버 관련 명령어
  - exportfs : export된 디렉토리 정보, 즉 공유 목록 관리
##### 1.3.2.3 NFS 서비스 이용하기
- NFS 서버 접속
- 클라이언트 호스트에 디렉토리 마운트
  - ```mount -t nfs [클라이언트 ip]:[서버 디렉토리 경로] /var/test-local```
  - /etc/fstab에서도 설정 가능
- NFS 클라이언트 관련 명령어
  - showmount : NFS 서버에서 export된 정보 확인
    
#### 1.3.3 FTP(File Transport Protocal)
##### 1.3.3.1 FTP 개요
- FTP 서버와 클라이언트 사이 파일 전송을 위한 프로토콜
- 능동 모드 : 클라이언트가 서버에 포트번호를 알리고 이를 통해 서버가 클라이언트로 접속
- 수동 모드 : 서버가 포트번호를 지정하고, 이를 통해 클라이언트가 서버로 접속
##### 1.3.3.2 FTP 서비스 설치와 구성
- FTP 관련 패키지 설치 : ```yum -y install vsftpd```
- FTP 서버 설정 : ```/etc/vsftpd/vsftpd.conf```
- vsftpd 데몬 실행 : ```systemctl start vsftpd.service```
##### 1.3.3.3 FTP 서비스 이용하기
- FTP 클라이언트 설치 : ```yum -y install ftp```
##### 1.3.3.4 FTP 관련 파일
- /etc/vsftpd/ftpdusers : 서버에 접근할 수 없는 계정 기입

### 1.4 메일 관련 서비스
#### 1.4.1 메일 관련 서비스의 개요
##### 1.4.1.1 메일 서비스의 개념과 구성요소
- SMTP : TCP 25. 메일 전송을 위해 사용하는 프로토콜
- POP3 : TCP 110. 도착한 메일을 수신하는 프로토콜 
- IMAP : TCP 143. 도착한 메일을 수신하는 프로토콜. POP3과 달리 메일을 서버에 남겨 두었다가 삭제할 수 있음
##### 1.4.1.2 메일 서비스 사용하기
- 메일 관련 패키지 설치 : ```yum -y install sendmail```
- sendmail 주요 설정 파일
  - /etc/mail/sendmail.cf
  - /etc/mail/access : 메일 서버에 접속하는 호스트의 접근을 제어하는 설정 파일
    - ex. Connect:127.0.0.1  OK, From:abnormal@google.com   REJECT
    - ```makemap hash /etc/mail/access < /etc/mail/access```
  - /etc/aliases : 메일 별칭(특정 계정)으로 수신한 이메일을 다른 계정으로 전달하는 것을 설정
    - sendmail이 참조하는 파일은 /etc/aliases.db임
    - /etc/aliases 수정 후, newaliases 또는 sendmail bi로 변경
  - 홈 디렉토리의 .forward 파일
    - 외부 메일 서버로 전송하기 위해 사용

### 1.5 DNS 관리 서비스
#### 1.5.1 DNS의 개요
##### 1.5.1.1 DNS의 개념과 구성요소
- 도메인 이름과 ip를 상호 변환하는 서비스
- TCP 53, UDP 53 포트 이용
- Primary Name Server, Slave Name Server, Caching Name Server 3가지 존재
#### 1.5.2 DNS 서비스 사용하기
##### 1.5.2.1 DNS 설치
- 관련 패키지 설치 : ```yum -y install bind```
##### 1.5.2.2 /etc/named.conf 파일 설정
- /*~*/, //, #를 주석 기호로 사용할 수 있음
- DNS 서버 주요 환경 설정 파일
  - options
    - directory : zone 파일 설정(/var/named)
    - forward :
      - only : 도메인 주소에 대한 query를 다른 서버로 넘김
      - first : 다른 서버에서 응답이 없을 경우 자신이 응답
    - allow-query : 질의할 수 있는 호스트 지정
    - allow-transfer : 파일 내용을 복사할 대상 정의
  - logging
  - acl : 여러 호스트들을 하나의 이름으로 지정하여 allow-query, allow-transfer에서 사용하도록 함
  - zone : 도메인을 관리하기 위한 데이터 파일
##### 1.5.2.3 zone 파일 설정
- 도메인, IP, 리소스 간 매핑 정보를 포함한 파일
- zone 파일은 도메인 -> ip 매핑이고, rev 파일은 ip -> 도메인 매핑
- named-checkzone [도메인명] [파일경로] : /var/named/ 이하 zone 파일 문법 점검
- named-checkconf [파일경로] : /etc/named.conf 문법 점검
```sh
zone "[도메인명]" IN {
  type [master|slave|hint]; # hint : 루트 도메인, mater : 1차 네임 서버, slave : 2차 네임 서버
  file "[zone 파일명]";
};
```
- SOA 레코드 : zone 소유자, 메일주소, 유효성 검사 주기 등을 설정함
  - Nameserver
  - Contact_email_address
  - Serial_number
- 주요 레코드 타입
  - A : ipv4 주소
  - CNAME : 도메인 이름 별칭
  - MX : 도메인 이름에 대한 메일 교환 서버
  - NS : 호스트에 대한 공식 네임 서버
  

### 1.6 가상화 관련 서비스
#### 1.6.1 가상화 서비스의 개요
##### 1.6.1.1 가상화 특징
- 하나의 물리적 리소스를 여러 논리적 리소스로 나누거나, 다수의 물리적 자원을 하나의 논리적 자원으로 통합하는 것
- 공유(Sharing) : 다수의 가상 자원들이 하나의 물리적 자원에 연결되거나 가리키는 것
- 프로비저닝(Provisioning) : 사용자의 요구사항에 맞게 리소스를 세밀한 조각으로 나눈 것
- 에뮬레이션(Emulation)
- 단일화(Aggregation) : 물리적 자원을 통합하여 하나의 논리적 자원으로 하는 것
- 절연(insulation)
##### 1.6.1.2 가상화 서비스 방식과 기술
- 하드웨어 레벨의 가상화
  - 전가상화 : 하드웨어를 완전히 가상화하여 다양한 게스트 OS를 수정없이 사용 가능
  - 반가상화 : 하이퍼바이저에 하드웨어 제어를 요청하며 동작
- 호스트 기반 가상화(Virtual Machine)
- Xen : 하이퍼바이저 기반의 가상화 기술로 전가상화 및 반가상화 모두 지원
- KVM : CPU 전가상화를 지원하는 기술로 이더넷, DISK I/O, 그래픽은 반가상화를 지원함
##### 1.6.1.3 가상 머신 관련 명령어
- virt-top : 가상머신 CPU 자원 모니터링
- virsh : 가상머신 셸
- virt-manager


### 1.7 기타 서비스
#### 1.7.1 슈퍼 데몬
##### 1.7.1.1 슈퍼 데몬의 개요
- inetd와 같이 다른 서비스를 실행 관리하는 데몬을 슈퍼 데몬이라 함
- inetd, standalone 방식 사용. Access Control을 위한 TCP Wrapper
- xinted(/etc/xinted)
##### 1.7.1.2 TCP Wrapper
- /etc/hosts.allow, /etc/hosts.deny 파일을 이용하여 데몬 서비스 접근제어
#### 1.7.2 프록시 서비스
##### 1.7.2.1 프록시 개요
##### 1.7.2.2 리눅스 프록시 서버(squid)
- http_access allow : 특정 대역만 사용하도록 허가함
#### 1.7.3 DHCP 서비스
##### 1.7.3.1 DHCP 서비스의 개요
- 호스트가 사용할 ip 주소, 게이트워이 주소, 네임 서버 주소 등을 자동으로 할당하는 서비스
- DHCP 데몬 설정 파일 위치 : /etc/dhcp/dhcpd.conf
  - log-facility :  syslog에서 전달한 로그 facility 지정
#### 1.7.4 VNC 서비스
##### 1.7.4.1 VNC 서비스의 개요
- 해상도 변경 : /etc/sysconfig/vncservers 수정

## 2. 네트워크 보안
### 2.1 네트워크 침해 유형 및 특징
#### 2.1.1 네트워크 침해 유형
##### 2.1.1.1 스니핑
- 네트워크 내에서 전송되는 패킷을 임의로 확인하는 공격 기법
- 대응방법 : SSL 암호화
##### 2.1.1.2 스푸핑
- 패킷 정보를 임의로 변경하는 기법
##### 2.1.1.3 Dos DDos
- Ping of Death : 패킷을 정상적인 크기보다 더 크게하여 전송하여 시스템에 문제를 일으키는 방법
- Smurf Attack : 송신 ip 주소를 공격 서버로 변경하고 브로드캐스트하면 공격 대상 서버가 이를 받게되며 부하가 걸림
- Teardrop Attack : 패킷을 재조립할 때 offset을 임의로 공격
- Land Attack : 발신자 수신자 ip를 모두 공격 대상으로 변경






















  
























































