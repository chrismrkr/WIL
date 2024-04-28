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
  - configure 명렁어를 통해 makefile 생성
- 아파치 웹 서버 소스코드 컴파일 및 설치
  - ```make && make install```
  - /usr/local/apache에서 설치 상태 확인
  - ```httpd -version```으로 설치 상태 확인
- 데몬 설정 및 도메인 설정
  - /etc/rc.d/rc.local에서 데몬 설정 
  - /usr/local/apache/conf/httpd.conf에서 웹 서버 도메인 및 ip 설정 가능
  - httpd -t 명령어로 컴파일 전 환경설정 파일 문법 오류 체크 가능
  - httpd -f 명령어로 환경설정 파일을 직접 지정하여 실행 가능
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
- 아래 링크 참고
- https://github.com/chrismrkr/WIL/blob/main/Linux/apache-summary.md

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
- 아래 링크 참고
- https://github.com/chrismrkr/WIL/blob/main/Linux/nis-summary.md

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
    - WindowsUsername = LinuxUsername1, LinuxUsername2
  - smbpasswd : 삼바 사용자 계정 활성화 및 패스워드 설정
  - pdbedit : 삼바 사용자 목록 및 세부 내용 확인
  - write list : 쓰기 가능한 사용자 지정
  - valid users : 접근 가능 리눅스 사용자 지정(기본 값 : 모든 사용자)
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
  - ```mount -t nfs [NFS 서버 ip]:[서버 디렉토리 경로] /var/test-local```
  - /etc/fstab에서도 설정 가능
    - ```[NFS 서버 ip]:[서버 디렉토리 경로] [로컬 디렉토리 경로] nfs ... ```
- NFS 클라이언트 관련 명령어
  - showmount : NFS 서버에서 export된 정보 확인
    
#### 1.3.3 FTP(File Transport Protocal)
##### 1.3.3.1 FTP 개요
- FTP 서버와 클라이언트 사이 파일 전송을 위한 프로토콜(TCP 20)
- 능동 모드 : 클라이언트가 서버에 포트번호를 알리고 이를 통해 서버가 클라이언트로 접속
- 수동 모드 : 서버가 포트번호를 지정하고, 이를 통해 클라이언트가 서버로 접속
##### 1.3.3.2 FTP 서비스 설치와 구성
- FTP 관련 패키지 설치 : ```yum -y install vsftpd```
- FTP 서버 설정 : ```/etc/vsftpd/vsftpd.conf```
  - ex. local_enable=YES : 일반 사용자(로컬 사용자) 접근 가능
- vsftpd 데몬 실행 : ```systemctl start vsftpd.service```
- /etc/vsftpd/ftpdusers : 서버에 접근할 수 **없는** 계정 기입
##### 1.3.3.3 FTP 서비스 이용하기
- FTP 클라이언트 설치 : ```yum -y install ftp```



### 1.4 메일 관련 서비스
#### 1.4.1 메일 관련 서비스의 개요
##### 1.4.1.1 메일 서비스의 개념과 구성요소
- SMTP : TCP 25. 메일 전송을 위해 사용하는 프로토콜
  - MTA(Mail/Message Transfer Agent)인 qmail, postfix 사용
- POP3 : TCP 110. 도착한 메일을 수신하는 프로토콜
  - dovecot 프로그램 사용
- IMAP : TCP 143. 도착한 메일을 수신하는 프로토콜. POP3과 달리 메일을 서버에 남겨 두었다가 삭제할 수 있음
  - docvecot 프로그램 사용 
##### 1.4.1.2 메일 서비스 사용하기
- 메일 관련 패키지 설치 : ```yum -y install sendmail```
- sendmail 주요 설정 파일
  - /etc/mail/sendmail.cf
  - /etc/mail/access : 메일 서버에 접속하는 호스트의 접근을 제어하는 설정 파일
    - ex. Connect:127.0.0.1  OK, From:abnormal@google.com   REJECT
    - ```makemap hash /etc/mail/access < /etc/mail/access```
  - /etc/aliases : 메일 별칭(특정 계정)으로 수신한 이메일을 다른 계정(이메일)으로 전달하는 것을 설정
    - sendmail이 참조하는 파일은 /etc/aliases.db임
    - /etc/aliases 수정 후, ```newaliases 또는 sendmail bi```로 변경
  - 홈 디렉토리의 .forward 파일
    - 외부 메일 서버로 전송하기 위해 사용
  - /etc/mail/virtusertable : 가상의 도메인으로 들어오는 메일을 특정 계정으로 전달
    - [가상 이메일 주소] [포워딩 이메일 주소]
    - ```makemap hash /etc/mail/virtusertable < /etc/mail/virtusertable```
  - /etc/mail/local-host-names : 로컬 도메인으로 처리할 호스트 이름 목록

### 1.5 DNS 관리 서비스
#### 1.5.1 DNS의 개요
##### 1.5.1.1 DNS의 개념과 구성요소
- 도메인 이름과 ip를 상호 변환하는 서비스
- TCP 53, UDP 53 포트 이용
- /etc/named.conf, /var/named
- Primary Name Server, Slave Name Server, Caching Name Server 3가지 존재
- BIND 프로그램이 DNS 서버 프로그램으로 가장 널리 쓰이며 ISC에서 배포함
#### 1.5.2 DNS 서비스 사용하기
##### 1.5.2.1 DNS 설치
- 관련 패키지 설치 : ```yum -y install bind```
##### 1.5.2.2 /etc/named.conf 파일 설정
- /*~*/, //, #를 주석 기호로 사용할 수 있음
- DNS 서버 주요 환경 설정 파일
  - options
    - directory : zone 파일 설정(/var/named)
    - forward :
      - only : 도메인 주소에 대한 query를 다른 서버로 넘김. 서버 응답이 없는 경우 버림.
      - first : 다른 서버에서 응답이 없을 경우 자신이 응답
    - forwarders : forward할 서버 지정(ex. forwrders: host1; host2;)
    - allow-query : 질의할 수 있는 호스트 지정
    - allow-transfer : 파일 내용을 복사할 대상 정의
  - logging
  - acl : 여러 호스트들을 하나의 이름으로 지정하여 allow-query, allow-transfer에서 사용하도록 함
    - ex. acl "host-group" {192.168.2.14; 192.168.2/24;};
    - allow-query: host-group;
  - zone : 도메인을 관리하기 위한 데이터 파일
##### 1.5.2.3 zone 파일 설정(/var/named/)
- 도메인, IP, 리소스 간 매핑 정보를 포함한 파일
- zone 파일은 도메인 -> ip 매핑이고, rev 파일은 ip -> 도메인 매핑
- named-checkzone [도메인명] [파일경로] : /var/named/ 이하 zone 파일 문법 점검
- named-checkconf [파일경로] : /etc/named.conf 문법 점검
- ;를 이용하여 주석 가능
```
;@는 /etc/named에 설정된 도메인명임
;SOA는 Start Of Authority의 약자로 DNS 핵심 정보를 지정함
;$TTL은 캐시 보관 시간임

$TTL 1D
@  IN SOA ns.ihd.or.kr. kait.ihd.or.kr (
  20240030306  ;Serial
  7200         ;Refresh
)
IN NS ns.ihd.or.kr.
IN A 192.168.12.22
IN MX 10 ihd.or.kr.
www IN A 192.168.12.22
www1 IN CNAME www
```

아래 방법으로 zone 파일 지정도 가능함

```
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
- 에뮬레이션(Emulation) : 물리적으로 존재하지 않는 장치를 범용적인 모델로 인식하여 사용할 수 있도록 지원하는 것
- 단일화(Aggregation) : 물리적 자원을 통합하여 하나의 논리적 자원으로 하는 것
- 절연(insulation)
##### 1.6.1.2 가상화 서비스 방식과 기술
- 하드웨어 레벨의 가상화
  - 전가상화 : 하드웨어를 완전히 가상화하여 다양한 게스트 OS를 수정없이 사용 가능
  - 반가상화 : 하이퍼바이저에 하드웨어 제어를 요청하며 동작
- 호스트 레벨 가상화(Virtual Machine)
  - Xen : 하이퍼바이저 기반의 가상화 기술로 전가상화 및 반가상화 모두 지원
  - KVM : CPU는 전가상화, 이더넷, DISK I/O, 그래픽카드는 반가상화를 지원
- 가상화 지원 소프트웨어 디스크 이미지 형식
  - VDI, VHD, VMDK
#### 1.6.2 가상화 관련 주요 명령어
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
- inetd 데몬에 의해 관리되는 서비스에 대한 접근 제어를 함
- /etc/hosts.allow, /etc/hosts.deny 파일 이용
#### 1.7.2 프록시 서비스
##### 1.7.2.1 프록시 개요
##### 1.7.2.2 리눅스 프록시 서버(squid)
- TCP 3128을 기본으로 사용함
- /etc/squid/squid.conf 사용
- acl : 별칭 지정
- http_access allow : 특정 대역만 사용하도록 허가함
- http_access deny : 특정 대역에서 접근을 불허함
#### 1.7.3 DHCP 서비스
##### 1.7.3.1 DHCP 서비스의 개요
- 호스트가 사용할 ip 주소, 게이트워이 주소, 네임 서버 주소 등을 자동으로 할당하는 서비스
- DHCP 데몬 설정 파일 위치 : /etc/dhcp/dhcpd.conf
  - log-facility :  syslog에서 전달한 로그 facility 지정
```
subnet 192.168.10.0 netmask 255.255.255.0 {
  range 192.168.10.0 192.168.10.200;
  option domain-name "temp-dhcp.com";
  ...
}
```
#### 1.7.4 VNC 서비스
##### 1.7.4.1 VNC 서비스의 개요
- Virtual Network Computing의 약자로 GUI 방식으로 원격 컴퓨터 접속 및 사용 기능 제공
- 해상도 변경 : /etc/sysconfig/vncservers 수정
##### 1.7.5 NTP 서비스
##### 1.7.5.1 NTP(Network Time Protocol) 개요
- 컴퓨터 간 시간을 동기화하기 위해 사용함
##### 1.7.5.2 NTP 설치와 설정
- /etc/ntp.conf 이용
##### 1.7.5.3 NTP 서비스 명령어
- ntpdate [서버주소] : 원격 서버와 시간을 동기화함
- ntpq -p : 연결된 서버 상태 출력

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

### 2.2 대처 및 대처 방안
#### 2.2.1 보안 솔루션을 이용한 대비 및 대처
##### 2.2.1.1 방화벽(Firewall)
- 허가된 트래픽만 내부로 유입되고 외부로 유출될 수 있도록 통제하는 소프트웨어 또는 하드웨어
##### 2.2.1.2 침입 탐지 시스템(Intrusion Detection System)
- 네트워크 데이터를 수집 및 분석하여 공격을 실시간으로 탐지함
##### 2.2.1.3 침입 방지 시스템(Intrusion Prevention System)

#### 2.2.2 Snort를 이용한 대비 및 대처
##### 2.2.2.1 Snort의 개요
- 탐지 룰(Rule)을 이용하여 네트워크 트래픽을 분석하고 침입을 탐지함
  - Snort Rule은 크게 헤더와 룰 옵션으로 구성됨
##### 2.2.2.2 Snort의 Rule 설정
- ex. alert tcp any any -> 192.168.10.0/24 80(msg:"passwd detected"; content:"passwd"; nocase; sid:100001;)
  - 외부에서 192.158.10/24 대역 80 포트로 들어오는 패킷 중, passwd가 문제된 패킷이 발견되면 alert 메세지를 띄움
- ex. alert ip any any -> any any (msg:"Land Attack"; sameip; sid:100001;)
  - Land Attack 탐지
#### 2.2.3 iptables를 이용한 대비 및 대처
##### 2.2.3.1 iptables 개요
- 패킷 필터링을 수행하는 리눅스 방화벽
- firewalld 대신에 iptables를 사용할 수 있음
```
systemctl stop firewalld
systemctl disable firewalld
systemctl start iptables
systemctl enable iptables
```
##### 2.2.3.2 iptables의 테이블 구조
- filter, nat, mangle(특수 규칙 적용), raw(연결 추적을 위한 세부 기능), security(보안 커널에서 사용하는 세부 규칙) 총 5가지 테이블이 존재하며 각각 chain 지정 및 정책 설정 가능
- filter
  - input : 목적지로 들어오는 패킷 필터링
  - output : 출발지로 나가는 패킷 필터링
  - forward : 라우터로 통과되는 패킷
- nat
  - prerouting : 패킷의 최종 도착지 주소를 변경
  - postrouting : 패킷의 최초 출발지 주소를 변경
  - input, output : 라우팅 주소 변경

##### 2.2.3.3 iptables 사용하기
- ```iptables [-t 테이블이름] [action] [체인 이름] [match 규칙] [-j 타겟]```
- filter 테이블을 사용하면 -t 생략 가능
- 체인 자체 옵션
  - -N : 정책 체인 신규(iptables -N new-filter)
  - -X : 정책이 없는 체인 삭제
  - -L : 지정한 체인 내부 목록 확인
  - -F : 지정한 체인 내부 정책 삭제(flush)
  - -C : 패킷 테스트
  - -P : 지정된 체인에 기본 정책 설정
- 체인 내부 옵션
  - -A : append
  - -I : 지정한 라인에 Insert
  - -D : 내부 정책 제거
  - -R : 정책 수정

- ex1. iptables -A INPUT -s 192.168.10.0/24 ! -p icmp -j ACCEPT
  - 해당 대역에서 들어오면서 icmp 패킷이 아닌 요청을 수용하는 정책을 추가함
- ex2. iptables -P INPUT DROP
  - INPUT 기본 정책을 DROP으로 함
- iptables 저장
  - ```iptables-save > my-firewall.sh```
- iptables 정책 영구 적용
  - ```serivce iptables save```
- iptables 복원
  - ```iptables-restore < my-firewall.sh```
##### 2.2.3.4 iptables를 이용한 NAT 구성































  

























































