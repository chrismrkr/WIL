# Sendmail
'아이디@이메일서버명' 형식의 메일 주소를 이요하여 이메일을 주고 받을 수 있는 서비스

## 메일 서비스 설치
- ```yum -y install sendmail```
- ```rpm -qc sendmail```

## 메일 서버 관련 설정 파일
- /etc/mail/sendmail.cf : sendmail 기본 설정 파일
  - Cw : 수신 호스트 이름을 설정. 보통 도메인 명을 함
  - Fw : 여러 개의 도메인명을 수신 호스트의 이름으로 이용할 경우, 관련 설정 파일 지정(/etc/mail/local-host-names)
  - Dj : 발신 도메인을 강제로 변경함
  - Dn : sendmail이 에러 등의 이유로 회신할 때 사용할 사용자 이름
  - FR-o : Relay를 허용할 도메인 설정(/etc/mail/relay-domains)
- /etc/mail/sendmail.mc : sendmail.cf를 보조하는 파일
  - ```m4 sendmail.mc > sendmail.cf``` 명령어로 m4 유틸리티 생성

- /etc/mail/access : 메일 서버에 접속하는 호스트 접근 제어
  - Connect:127.0.0.1 OK
  - From:abnormal@email.com REJECT
  - To:gildong-mail.com RELAY
  - ```makemap hash /etc/mail/access < /etc/mail/access``` 명령어로 /etc/mail/access.db에 적용

- /etc/aliases : 메일 별칭으로 수신한 이메일을 다른 계정(이메일)로 전달하는 것을 설정
  - /etc/aliases 수정 후, ```newaliases``` 또는 ```sendmail bi``` 명령어로 변경
  - /etc/aliases.db를 참조함

- /etc/mail/virtusertable : 가상의 메일 계정으로 수신한 메일을 특정 호스트 계정으로 전달
  - [가상 이메일 계정] [호스트 계정]
  - ex. virtmailaddress@email.com host1
  - ```makemap hash /etc/mail/virtusertable < /etc/mail/virtusertable``` 명령어로 /etc/mail/virtusertable.db에 적용

- /etc/mail/local-host-names : 메일 수신지(도메인 및 호스트)를 설정함
  - 예를 들어, 위 파일에 tmp.or.kr을 입력하면, 그것이 메일 수신 도메인이 됨
- ~/*.forward : 개인이 수신한 메일을 다른 메일로 포워딜할 때 설정하는 파일



