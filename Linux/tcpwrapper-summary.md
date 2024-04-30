# TCP Wrapper
inetd 데몬에 의해 관리되는 서비스에 대한 접근 제어를 담당하는 서비스

- 접근 허가 관련 설정 파일 : /etc/hosts.allow
- 접근 거부 관련 설정 파일 : /etc/hosts.deny
- /etc/hosts.allow -> /etc/hosts.deny 순서로 읽힘
- 설정 규칙
  - 형식 : ```[데몬 목록] : [클라이언트 목록] : [옵션]```
  - ex : ```ssh : 192.168.1.0/24 : allow```
  - ex : ```smtpd : ALL : allow```
