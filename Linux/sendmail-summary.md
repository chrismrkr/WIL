# Sendmail
'아이디@이메일서버명' 형식의 메일 주소를 이요하여 이메일을 주고 받을 수 있는 서비스

## 메일 서비스 설치
- ```yum -y install sendmail```
- ```rpm -qc sendmail```

## 메일 서버 관련 설정 파일
- /etc/mail/sendmail.cf : sendmail 기본 설정 파일
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

- /etc/mail/local-host-names : 로컬 도메인으로 처리할 호스트 이름 목록

- ~/*.forward : 개인이 수신한 메일을 다른 메일로 포워딜할 때 설정하는 파일



