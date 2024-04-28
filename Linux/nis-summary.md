# NIS(Network Information Service)

서로 다른 리눅스 시스템 사이에 동일한 인증 서비스를 제공하기 위해 사용함

## NIS 서버
- RPC 데몬 구동
  - ```systemctl start rpcbind```, ```systemctl enable rpcbind```
- NIS 서버 패키지 설치
  - ```yum -y install ypserv```
- NIS 도메인명 등록
  - ```nisdomainname [도메인명]```
  - ```echo "NISDOMAIN=[도메인명]``` >> /etc/sysconfig/network
- NIS 계정 생성
  - 아이디 생성 : ```useradd [계정명]```
  - 패스워드 설정 : ```passwd [계정명]```
- NIS 서버 관련 서비스 실행
  - ```systemctl [start | restart | enable] ypserv.service```
  - ```systemctl [start | restart | enable] yppasswd.service```
  - ```systemctl [start | restart | enable] ypxfrd.service```
- NIS 서버 정보 갱신
  - ```make -c /var/yp```

## NIS 클라이언트
- NIS 클라이언트 설치
  - ```yum -y install ypbind yp-tools rpcbind```
- 접속하려는 NIS 서버 도메인 설정
  - /etc/yp.conf
- NIS 클라이언트 실행
  - ```systemctl [start | restart | enable] ypbind.service```

## NIS 관련 주요 명령어
- nisdomainname : NIS 도메인 이름 설정 또는 도메인 확인
- ypwhich : NIS를 통해 로그인 후, 인증에 사용한 NIS 서버 도메인 확인
- ypcat : NIS 클라이언트에서 NIS 서버 구성파일(map) 확인
- yptest : NIS 클라이언트에서 NIS 동작, 설정, 도메인명, 사용자 계정 정보 등 확인
- yppasswd : 사용자 비밀번호 변경
- ypchsh : 사용자 셸 변경
- ypchfn : 사용자 정보 변경
- yppush : 서버에서 클라이언트로 서버 구성 파일(map) 전송 및 업데이트











