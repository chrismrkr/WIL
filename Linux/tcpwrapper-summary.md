# TCP Wrapper
inetd 데몬에 의해 관리되는 서비스에 대한 접근 제어를 담당하는 서비스
- 설치
  - ```yum -y install tcp_wrappers```

- 접근 허가 관련 설정 파일 : /etc/hosts.allow
- 접근 거부 관련 설정 파일 : /etc/hosts.deny
- /etc/hosts.allow -> /etc/hosts.deny 순서로 읽힘
- 설정 규칙
  - 형식 : ```[데몬 목록] : [클라이언트 목록] : [옵션]```
  - ex
    - ssh는 192.168.1.0 대역에서 접속한 모든 호스트를 허가 : ```sshd : 192.168.1. : allow```
    - smtp는 모든 호스르를 허가 : ```smtpd : ALL : allow```
    - telnet은 192.168.5.13 호스트만 허가 : ```in.telnetd : 192.168.5.13 : allow```
    - 모든 서비스에 로컬호스트와 ihd.or.kr 도메인을 사용하는 호스트는 허가 : ```ALL : localhost, .ihd.or.kr : allow``` 
