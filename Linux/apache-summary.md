# Apache 웹 서버

## Apache 웹 서버 특징
- 멀티 프로세스 모듈(prefork MPM) 방식
- 매 요청이 들어올 때 마다 프로세스를 fork한 후, 이를 통해 요청을 처리함(prefork 방식)
- Apache2.0 이상에서는 매 요청마다 스레드를 생성하여 처리하는 worker 방식을 지원함

## 설치 및 설정 파일 확인
- 설치 : ```yum -y install httpd```
- 설정 파일 확인 : ```rpm -qc httpd```
- 설정 파일 위치 : /etc/httpd/conf/httpd.conf

## httpd 명령어
- ```man httpd``` 로 확인 가능
- ```httpd -t``` : 환경설정 파일 문법적 오류 점검
- ```httpd -f [파일명]``` : httpd.conf 파일이 아닌 [파일명]을 지정하여 아파치 웹 서버 실행

## apachectl 명령어
- ```man apachectl```로 확인 가능
- ```apachectl [start | stop | restart]``` : 시작, 종료, 재시작
- ```apachectl configtest``` : 환경설장 파일 문법적 오류 점검
- ```apachectl graceful``` : 현재 클라이언트 접속 유지하면서 환경설정 최신화


## 환경설정 파일 및 관련 설정
- /etc/httpd/httpd.conf
- ```man httpd.conf```로 메뉴얼 확인 가능
- 설정
  - ServerRoot : 아파치 서버의 주요 파일이 저장된 최상위 디렉토리 절대경로 설정
  - Listen : 요청을 Listen할 포트번호 지정
  - LoadModules : DSO(Dynamic Shared Object) 방식으로 로드할 모듈 지정
  - User : 데몬 실행 사용자 권한 지정
  - Group : 데몬 실행 그룹 권한 지정
  - ServerAdmin : 어드민 이메일 설정. 에러 발생 시 해당 메일로 에러 메세지 전달
  - ServerName : 호스트명 입력
  - DocumentDirectory : Docment Root 디렉토리를 지정함
  - <Directory [디렉토리경로]> ... : 지정한 디렉토리에 대한 권한, 제어, 옵션 등을 설정함
    - Options : 해당 디렉토리 및 하위 디렉토리 제어
    - AllowOverride
    - Allow from, Deny forom
  - <FileMatch [파일명]> : 지정된 패턴에 맞는 파일에 대한 권한, 제어, 옵션 등을 설정함
  - LogLevel
    - /etc/rsyslog.conf 내 우선순위와 동일함
  - ErrorLog : 에러 로그 위치 경로
  - CustomLog [파일 절대 경로] [] : 커스텀 로그 위치 경로
  - ifModule : 지정한 모듈에 대한 세부 동작 옵션 설정
    - log_config_module : 로그 레벨
    - mod_userdir.c : 사용자 별 홈 페이지 사용여부 설정(사용자 A는 a 홈페이지, B는 b 홈페이지..)

## WEB-WAS 연동
- httpd-vhosts.conf 파일에서 WEB-WAS 연동 설정함
- 프록시 방식
  - mod_proxy 모듈 사용
- 직접 연동 방식
  - mod_jk 모듈 사용
- httpd-vhosts.conf 프록시 설정 관련 속성
  - <VirtualHost *:80> : 프록시 서버 ip & 포트 설정
    - ProxyRequests [ON | OFF] : 포워드 프록시 | 리버스 프록시
    - ProxyPreserveHost [ON | OFF] : ON 시 원래 호스트의 요청 헤더를 백엔드 서버로 전달함
    - <Proxy *> ... </Proxy> : 프록시 설정에 대한 보안 규칙 지정
    - ProxyPass [prefix] [전달 url]
    - ProxyPassReverse [prefix] [전달 url]

## 웹 서버 기본 설정
- httpd-default.conf
  - KeepAlive : 클라이언트 및 서버 모두 On이어야 활성화됨
  - MaxKeepAliveRequests, KeepAliveTimeout
  - HostnameLookups

## 웹 서버 프로세스 및 스레드 운용 방식 설정
- httpd-mpm.conf
- prefork : mpm_prefork_module 사용
  - StartServers
  - MinSpareServers
  - MaxSpareServers
  - MaxRequestWorkers
  - MaxConnectionPerChild

- worker : mpm_worker_module 사용
  - StartServers
  - MinSpareThread
  - MaxSpareThread
  - ThreadPerChild
  - MaxRequestWorkers : 동시 접속 클라이언트 최대 값
  - MaxConnectionPerChild

















