# SAMBA
윈도우 운영체제에서 리눅스 파일 시스템을 원격으로 사용하기 위한 서비스

## SAMBA 서버
### 패키지 설치 및 서비스 실행
- ```yum -y install samba samba-common```
- ```systemctl [start | restart | enable] smb.service```
- ```systemctl [start | restart | enable] nmb.service```
### 환경 설정
- ```rpm -qc samba```로 확인
- /etc/samba/smb.conf에서 설정
- Global Setting
  - [global]로 시작함
  - work group : 공유 그룹 명
  - server string : 서버에 대한 설명
  - netbios name : 윈도우에서 이름으로 검색하기 위한 키워드
  - hosts allow
  - hosts deny
  - log file
  - max log size
- Share Setting
  - 공유 폴더 주요 설정
  - [디렉토리 이름]으로 시작함
  - path
  - comment
  - read only
  - writable
  - write list
  - valid users
  - public
  - browseable
  - create mask
  - follow symlinks
  - printable
### 계정 등록 및 패스워드 설정
- 계정 등록 : /etc/samba/smbusers
  - [윈도우계정명] = [리눅스 계정 명]
  - ex. root = administrator admin smbrootuser
  - ex. nobody = guest pcguest smbguest
  - smbpasswd 명령어로 계정 생성 및 패스워드 설정 가능
- 패스워드 설정 : pdbedit
  - ```man pdbedit```에서 확인 가능

## SAMBA 클라이언트
- ```yum -y install samba-common samba-client```로 설치
- ```man smbclient``` 명령어로 명령어 확인 가능












