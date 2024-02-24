# 리눅스 시스템 관리

## 1. 일반 운영 관리


### 1.1 사용자 관리
#### 1.1.1 사용자 분류
- 루트 계정, 시스템 계정, 사용자 계정
#### 1.1.2 루트 계정 관리
##### 1.1.2.1 루트 계정 설명
- UID는 0이고 어떤 명령어든 제한없이 사용 가능
##### 1.1.2.2 루트 계정 권한 획득 방법
- root 패스워드 설정
```sh
sudo passwd root
```
- root 사용자로 전환
```sh
su
su - # root 사용자 환경변수도 로드됨
```
- root 사용자로 임시 전환 : sudo 명령어를 통해 임시적으로 root 권한으로 전환하여 명령어를 실행할 수 있음
- /etc/passwd 변경 : UID를 0로 변경하면 root 사용자가 됨
##### 1.1.2.3 루트 계정 관리방안
- SSH, PAM로 접근할 수 없도록 해야함
- /etc/passwd 파일에 UID가 0인 계정은 하나일 것
- root 계정에 TMOUT을 통해 일정 시간 후에 로그아웃 되도록 할 것
- root 계정 로그인은 되도록 지양하고 sudo 명령어를 사용할 것 
##### 1.1.2.4 루트 패스워드 분실 대응 방법 
- 생략
#### 1.1.3 시스템 계정 관리
##### 1.1.3.1 시스템 계정 설명
- 메일 관리, SSH 연결 등 시스템의 특성 서비스에 권한을 갖는 계정
- UID는 1~499 사이에 있고 /etc/passwd에서 확인할 수 있음
##### 1.1.3.2 시스템 계정 관리 방안
- 서비스별로 권한을 분리하여 시스템 계정을 생성해야함
#### 1.1.4 사용자 계정 관리
##### 1.1.4.1 사용자 계정 설명
- 시스템 파일과 디렉토리에 제한적으로 접근하게 함
- 사용자 계정은 그룹에 속할 수 있고, 그룹 별로 리소스 권한 관리가 가능함
##### 1.1.4.2 사용자 계정 생성
- ```useradd``` 명령어로 생성 가능
- 홈 디렉토리, UID, 그룹, 만료일 등 설정이 가능함
##### 1.1.4.3 패스워드 설정
- ```passwd [옵션] [사용자 계정명]``` 명령어로 패스워드 설정 가능
- /etc/shadow 2번째 필드에 패스워드 설정됨
##### 1.1.4.4 사용자 계정 전환
- ```su [사용자 계정명]```으로 사용자 전환 가능
##### 1.1.4.5 사용자 설정 변경
- ```usermod [옵션] [계정명]``` 명령어로 설정 변경
- useradd 명령어로 할 수 있는 대부분을 변경할 수 있음
##### 1.1.4.6 사용자 계정 삭제
- ```userdel [옵션] [계정명]``` 명렁어로 계정 삭제
##### 1.1.4.7 사용자 계정 비밀번호 관리
- ```chage [옵션] [계정명]``` 명령어로 가능
#### 1.1.5 그룹 계정 관리
##### 1.1.5.1 그룹의 개요
- 사용자를 그룹에 묶어서 관리하기 위한 개념. 모든 사용자는 그룹에 속하며 여러 그룹에 포함될 수 있음
- 즉, 사용자는 GID와 UID를 갖음
- 레드헷 계열에서는 사용자 이름과 동일한 그룹을 초기해 생성함
##### 1.1.5.2 그룹 생성
- ```groupadd [option] [group-name]```으로 생성 가능
##### 1.1.5.3 그룹 정보 변경
- ```groupmod [option] [group-name]```으로 정보 변경 가능
##### 1.1.5.4 그룹 삭제
- ```groupdel [option] [group-name]```으로 삭제 가능
##### 1.1.5.5 그룹 패스워드 변경 및 사용자 추가
- ```gpasswd [옵션] [group-name]```으로 변경 가능
- /etc/group 또는 /etc/gshadow에서 확인 가능
##### 1.1.5.6 그룹 참여
- newgrp : 현재 로그인한 사용자의 그룹을 변경함
#### 1.1.6 사용자 환경설정 파일
- /etc/passwd, /etc/shadow, /etc/group, /etc/gshadow
- /etc/default/useradd : useradd 명령어 기본 설정
- /etc/login.defs : 패스워드 관련 설정
- /etc/skel : 사용자 생성 시, 해당 디렉토리 내용이 사용자 홈 디렉토리로 복사됨
##### 1.1.6.1 /etc/passwd
- 사용자 계정 UUID, GID, 홈 디렉토리, 셸 정보를 포함한 파일
- 모든 사용자가 읽을 수 있고, 루트 사용자가 이를 수정할 수 있음
- 비밀번호 수정은 passwd 명령어로 가능함
- 형태 : 사용자명:비밀번호:UID:GID:설명:홈디렉토리:셸
##### 1.1.6.2 /etc/shadow
- 사용자 패스워드 정보가 들어있는 파일
- 형태 : 사용자명:비밀번호:최초생성일:최소사용일:최대사용일:만료경고일:유예기간:만료일
##### 1.1.6.3 /etc/default/useradd
- useradd 명령어로 사용자 생성 시 기본 설정 값이 저장된 환경설정 파일
- useradd -D 명령어로도 확인할 수 있음
##### 1.1.6.4 /etc/login.defs
- 패스워드 최대 사용일, 최소 사용일, 만료 경고일 등을 설정할 수 있음
##### 1.1.6.5 /etc/group
- 그룹에 속한 사용자를 관리하는 파일
- /etc/passwd에 기재된 GID는 사용자의 주 그룹이고, /etc/group에 기재된 GID는 보조 그룹임
- 형태 : 그룹명:패스워드:GID:사용자명1,2, ...
##### 1.1.6.6 /etc/gshadow
- 그룹 패스워드 정보를 포함한 파일
- 형태 : 그룹명:패스워드:그룹관리자:사용자1,2, ...
#### 1.1.7 사용자 및 그룹 정보 관련 명령어
- users : 호스트에 접속한 모든 사용자 출력
- who : 호스트에 접속한 모든 사용자 정보 출력
- whoami : 현재 로그인한 사용자의 이름 출력
- w : 현재 로그인한 사용자가 어떤 시스템에서 로그인 했는지, 어떤 프로세스를 실행 중인지 출력
- logname : 현재 로그인한 사용자의 로그인 이름
- id [option] [username] : 사용자의 정보와 그룹 정보 출력
- groups [username] : 사용자 그룹 정보 출력
- lslogins : 시스템 전체 사용자 **ID** 출력
- pwconv(/etc/passwd로부터 /etc/shadow 생성), pwunconv(/etc/shadow를 /etc/passwd로 되돌림), grpconv, grpunconv
- pwck : /etc/passwd에 잘못된게 있는지 검증
- gwck : /etc/group에 잘못된게 있는지 검증



### 1.2 파일 시스템 관리
#### 1.2.1 파일 및 디렉토리 관리
##### 1.2.1.1 소유권과 허가권
- ```ls -l``` 명령어로 확인 가능
- owner, group, other 사용자 권한 및 사용자, 그룹 소유권을 확인할 수 있음
- 권한 변경 명령어 : chmod
  - 8진수를 활용한 방법
    - ```chmod -R 775 dir_recursive```
    - ```chmod 775 file.txt```
  - 기호를 활용한 방법
    - ```chmod u+x file.txt```
    - ```chmod u-x file.txt```
    - ```chmod a+r file.txt```
    - ```chmod g+x file.txt```
    - ```chmod o-w file.txt```
- 소유권 변경 명령어 : chown
```chown [-R] [username][:groupname] file.txt```
- 그룹 소유권 변경 명령어 : chgrp
```chgrp [-R] [groupname] file.txt```
- 기본 허가권 변경 명령어 : umask
  - 파일 기본 권한(666), 디렉토리 기본 권한(777)
  - 예를 들어, umask가 022라면, 파일은 644, 디렉토리는 755 권한으로 생성됨
##### 1.2.1.2 특수 권한
- Set-UID 권한 비트
  - 파일을 실행했을 때, 사용자 권한이 아닌 소유자 권한으로 실행. 주로 실행 파일에서 사용됨
  - 예를 들어, 패스워드 변경 파일인 /usr/bin/passwd의 실행 권한은 s로 되어 있음
  - 해당 파일 실행 시, /etc/shadow에 임시로 root 권한으로 접근할 수 있게함
  - Set-UID 권한 비트 설정 방법
    - ```chmod u+s file.sh```
    - ```chmod 4755 file.sh```
- Set-GID 권한 비트
  - 파일을 실행했을 때, 현재 사용자의 그룹 권한이 아닌 소유자의 그룹 권한으로 실행
  - Set-GID 권한 비트 설정 방법
    - ```chmod g+s file.sh```
    - ```chmod 2744 file.sh```
- 스티키 비트
  - 디렉토리에만 적용 가능. 접근은 가능하나 삭제를 못하게 만들 때 사용함
  - ```chmod 777 dir_all```
  - ```chmod o+t dir_all```
  - ```chmod 1777 dir_all```
  - dir_all은 모두 접근 가능하지만, 소유자만 삭제할 수 있음
##### 1.2.1.3 파일 링크
- 이미 존재하는 파일에 대한 포인터(ln 명령어로 설정 가능)
- 하드 링크 : 아이노드에 대한 포인터
  - 링크 대상 파일이 삭제되더라도 파일을 조회할 수 있음
  - 동일 파일 시스템 내에서만 가능하고 파일에 대해서만 하드 링크 가능
- 심볼릭 링크 : 파일 및 디렉토리 경로에 대한 포인터
  - 링크 대상 파일이 삭제되면 파일을 조회할 수 없음
  - 파일과 디렉토리 모두 심볼릭 링크 가능함
  - 라이브러리 업그레이드에 주로 사용됨
##### 1.2.1.4 디렉토리 관리 명령어
- pwd : 현재 파일 시스템의 디렉토리 절대 경로 출력
- cd : 디렉토리 변경
- mkdir : 디렉토리 생성
- rmdir : 디렉토리 삭제
##### 1.2.1.5 파일 관리 명령어
- ls : 현재 디렉토리 내 파일 조회
- cp : 파일 복사
- rm : 파일 삭제
- mv : 파일 경로 이동 또는 이름 변경
- touch : 파일 마지막 접근 시간 변경 또는 빈 파일 생성
- file : 파일 유형 확인
- find : 지정한 조건에 맞는 파일 및 디렉토리 검색(find ./ -user ubuntu)
##### 1.2.1.6 텍스트 관련 명령어
- cat : 텍스트 내용 표준 출력
- head : 파일의 앞 부분 출력 (head -n 3 file.txt)
- tail : 파일의 뒷 부분 출력 (tail -f file.log | grep 192.168.0.1)
- more : 텍스트를 한 화면찍 보여주는 명령어 (cat /etc/passwd | more)
- less : 텍스트를 한 화면씩 보여주는 명령어 (less text.log)
- grep : 지정한 패턴과 일치하는 문자열을 보여주는 명령어
- wc : 지정한 파일에 대해 단어, 개행, 문자 개수 세는 명령어
- sort : 한줄씩 읽어서 정렬
- cut : 텍스트 파일을 잘라서 출력함
- split : 파일을 고정 크기의 여러 파일로 분할
#### 1.2.2 파일 시스템 관리 및 복구
- 개요 : fdisk 명령어로 파티션을 생성하고, mkfs 명령어로 원하는 파일 시스템으로 포맷하고, mount 명령어로 시스템상 마운트함
- /etc/fstab 파일에 마운트가 유지될 수 있도록 파일 시스템을 등록함
- fdisk :
  - ```fdisk -l```
  - ``` sudo fdisk [디스크명]```
- mkfs : ```mkfs -t [파일시스템 유형] [파티션명]```
- mount
- unmount
- eject
- df, du : 파일 및 디렉토리 크기 확인
#### 1.2.3 스왑(Swap)
##### 1.2.3.1 스왑 개요
- 물리 메모리 공간이 부족할 때 디스크 공간을 활용하는 기법
- 스왑 파일 또는 파티션 형태로 존재함
##### 1.2.3.2 스왑 파일 생성
- dd 명령어로 스왑 파일 생성 : ```dd if=/dev/zero of=/swapfile bs=1024 count=1048576
- 권한 설정 : ```chmod 600 /swapfile```
- 파일 초기화(스왑 파일 초기화) : ```mkswap /swapfile```
- 파일 시스템에 인식(스왑 파일 활성화) : ```swapon /swapfile```
- /etc/fstab에 영구 등록
##### 1.2.3.3 스왑 파일 삭제
- 스왑 파일 해제 : ```sudo swapoff -v /swapfile```
- /etc/fstab에서 삭제
- 스왑 파일 자체 삭제
##### 1.2.3.4 스왑 파티션 생성
- fdisk 로 파티션 생성
- 파티션 타입을 스왑으로 변경
- 스왑 파티션 초기화
- 스왑 파티션 활성화
- /etc/fstab에 스왑 파티션 영구 설정
#### 1.2.4 디스크 쿼터(Disk Quota)
##### 1.2.4.1 디스크 쿼터 개요 및 설정 방법
- 사용자 및 그룹마다 디스크 사용량을 할당하는 방법
- /etc/fstab에 설정
##### 1.2.4.2 디스크 쿼터 관련 명령어
- quotacheck : 파일 시스템 사용량 체크



### 1.3 프로세스 관리
#### 1.3.1 프로세스 관련 명령어
##### 1.3.1.1 프로세스 정보
- ps : 실행 중인 프로세스 정보 display
- pstree : 트리 형태로 프로세스 정보 display
- top : 프로세스 정보 실시간 display
##### 1.3.1.2 프로세스 종료
- kill : 특정 프로세스(pid)에 signal 전송
- killall [이름]: 이름에 부합하는 프로세스에 signal 전송
- pkill [패턴(정규표현식)]: 이름이 패턴에 부합되는 프로세스에 signal 전송
##### 1.3.1.3 프로세스 전환
- jobs : 현재 실행 중인 jobs 출력
- fg : 백그라운드 프로세스를 포어 그라운드로 전환
- bg : 포어 그라운드를 백그라운드 프로세스로 전환
##### 1.3.1.4 프로세스 우선순위
- nice [options] [command arg] : 프로세스 우선순위. -20 ~ 19 사이이고 숫자가 클수록 우선순위가 낮아짐
  - ```nice --10 bash``` : bash의 우선순위를 10 만큼 내림
- renice [-n] priority [option] : 현재 실행 중인 프로세스 운선순위 변경
  - nice와 달리 우선순위를 증감시키는 것이 아닌 특정 값으로 직접 변경함
- nohub [command]: 로그아웃 또는 연결 세션 종료 시그널이 발생하더라도 프로세스를 종료하지 않음
  - ex. nohub tail -f server.log | grep 192.168.0.0 >> greplog.out
##### 1.3.1.5 프로세스 검색
- pgrep [options] [pattern] : 프로세스 이름 일부 또는 전체가 매칭되는 것을 찾음
#### 1.3.2 프로세스 관련 파일
##### 1.3.2.1 /proc 디렉토리
- 가상의 파일 시스템으로 파일 접근 명령어(ls, cd) 및 시스템 조회 가능
- /proc/[PID] 에 프로세스 정보 존재
#### 1.3.3 작업 예약
##### 1.3.3.1 at
- 지정된 시간에 작업을 실행
- ```at [options] time```을 입력하면 at 전용 프롬프트 시작
- 원하는 명령어 및 스크립트 입력
- ctrl + d 입력
- /var/spool/at 에서 관리됨. 실행 결과는 /usr/sbin/sendmail을 통해 메일로 전송
- atq : 예약된 at 작업을 확인할 수 있음
- atrm : 예약된 작업을 삭제할 수 있음
##### 1.3.3.2 cron
- 배치성 작업
- 크론 데몬(cron.service)가 사용자 정의 크론(/var/spool/cron/crontabs), /etc/crontab, /etc/crond를 감시하여 배치를 실행함
- 크론 설정 파일
 - 크론 설정 파일에 아래와 같이 크론 작업을 정의할 수 있음
 - *(min) *(hour) *(day of month) *(month) *(day of week) [command]
 - ex. 30 22 * * SAT,SUN /usr/bin/backup.sh : 매주 주말 22시 30분에 해당 셸을 실행함
- crontab 명령어
  - ```crontab -e -u [계정명]``` : 해당 명령어로 크론 설정 파일에 접근하여 작업을 추가 및 수정 할 수 있음



### 1.4 설치 및 관리
#### 1.4.1 패키지를 통한 소프트웨어 설치
##### 1.4.1.1 설치 개요
- 패키지 도구 : 시스템에 소프트웨어를 편리하게 설치하기 위한 도구
- rpm, yum, apt-get, apt
##### 1.4.1.2 저수준 패키지 도구
- rpm 패키지 명령어로 설치. 먼저 설치가 필요한 라이브러리가 없으면 에러가 발생할 수 있음
- 패키지 설치 : ``` rpm -i [package-name]```
- 패키지 업그레이드 : ```rpm -U [package-name]```
- 패키지 확인 : ```rpm -q [package-name]```, ```rpm -qa```
- 특정 파일을 설치한 패키지 확인 : ```rpm -qf [file-name]```
##### 1.4.1.3 고수준 패키지 도구
- 패키지 검색 : ```yum(apt) search [package-name]```
- 패키지 설치 : ```yum(apt-get) install(update) [package-name]```
- 패키지 제거 : ```yum(apt-get) erase(remove) [package-name]```
  - 환경설정까지 제거하려면 purge 옵션 사용
- 패키지 정보 출력 : ```yum(apt) info(show) [package-name]```
#### 1.4.2 레드햇 패키지 관리와 데비안 패키지 관리
##### 1.4.2.1 레드햇 패키지 관리(RPM)
- 설치 및 업데이트 : rpm -i, rpm -U
- 제거 : rpm -e
- 확인 : rpm -q
##### 1.4.2.2 YUM
- 명령어 : yum [options] [command] [package-name]
- 환경설정 : /etc/yum.conf
- 예제. http 패키지 설치
  - yum install httpd
##### 1.4.2.3 데비안 패키지 관리
- 명령어 : apt-get [options] [command] [package-name]
#### 1.4.3 소스 코드 컴파일을 통한 소프트웨어 설치
##### 1.4.3.1 소스 코드 컴파일을 위한 소프트웨어 설치 개요
- tar로 아카이브한 후, gzip 등으로 압축된 패키지를 설치함
##### 1.4.3.2 빌드도구
- cmake : makefile을 생성함
##### 1.4.3.3 컴파일러
- gcc [options] 파일명
  - gcc myfile.c -o myfile.out
  - gcc -c myfile.c
##### 1.4.3.4 아카이브
- tar
  - 백업 및 배포 목적으로 많은 파일을 아카이브 파일로 만드는 유틸리티
  - tar [options] [filename]
  - tar 파일 생성 : tar -cvf archive.tar file1.java, file2.java
  - tar 파일 해체 : tar -xvf archive.tar
  - tar 파일 목록 확인 : tar -t archive.tar
  - 압축된 tar 파일 해제 : -zxvf(gzip), -jxvf(bzip2), -Jxvf(xz)
##### 1.4.3.5 압축
- compress, uncompress
  - compress [options] filename
  - uncompress [options] filename
- gzip, gunzip
  - gizp [options. -c(압축) -d(해제)] filename
- 이외 : bzip2(bunzip2), xz(unxz), zip(unzip)



## 2. 장치 관리

### 2.1 장치의 설치 및 관리
#### 2.1.1 리눅스 커널
- 설명 및 명령어 위주로 살펴볼 것
##### 2.1.1.1 커널의 개요
- 리눅스 커널은 GPL 라이센스로 무료 공개 되었고, 운영체제가 가져야 할 모든 특징을 갖음
- ```uname -r``` 명령어로 커널 버전 확인 가능
##### 2.1.1.2 커널 컴파일 순서
- 소스 다운로드 및 압축 해제
- yum, apt-get 등으로 컴파일에 필요한 도구 설치
- 이전 커널 환경설정 제거 : ```make mrproper```
- 커널 환경설정 ```make menuconfig``` 명령어로 실행하여 컴파일 옵션 변경 후 save 시 .config 파일이 변경됨
  - 이전 환경 설정을 복사해둔 후 변경하는 것이 좋음 : ```cp /boot/config-$(uname -r)```
- 컴파일 진행
  - ```make bzImage``` : 커널 이미지 생성
  - ```make modules``` : 커널에서 사용할 모듈 컴파일
  - ```sudo make install``` : 모듈을 /lib/modules/kernel-version 이하에 설치
  - ```sudo make modules_install``` : 커널 이미지 복사 및 커널 시스템 설치
  - ```sudo reboot``` : 시스템 재기동
#### 2.1.2 모듈
- 설명 및 명령어 위주로 살펴볼 것
##### 2.1.2.1 모듈의 개요
- 정의 : 커널 기능 확장을 위해 메모리에 동적으로 로드하는 오브젝트
- 모듈을 통해 커널 기능을 동적으로 로드하여 효율적임
##### 2.1.2.2 모듈 관련 명령어
- lsmod : 현재 로드된 모듈 리스트 출력
- lnsmod : 모듈을 로드함. ```/lib/modules/$(uname -r)```에서 파일을 찾아 로드 가능
- rmmod : 모듈 언로드
- modprobe : 모듈 의존성을 고려하여 로드 및 언로드(-r 옵션)
- modinfo : 지정한 모듈 정보 출력

### 2.2 주변장치 관리
#### 2.2.1 디스크 확장
- **주요 명령어 : fdisk**
##### 2.2.1.1 개요
- 용량이 부족할 때 확장함
- 하드디스크 부착 > 확장 피티션 생성 > 논리 파티션 생성 > 파티션 포맷 > 마운트 > 확인 > /etc/fstab 설정 순서로 진행됨
##### 2.2.1.2 하드디스크 부착
- fdisk -l 명령어로 부착 여부 확인
##### 2.2.1.3 확장 파티션 생성
- sudo fdisk [새로운 하드디스크 Physical Volume]로 콘솔 실행하여 생성
##### 2.2.1.4 논리 파티션 생성
- sudo fdisk [새로운 하드디스크 Physical Volume]로 콘솔 실행하여 생성
- 하나의 파티션을 논리적으로 여러개로 나눔. 실린더의 개수를 조절하여 나눌 수 있음
##### 2.2.1.5 파티션 포맷
- 특정 파일 시스템으로 파티션을 포맷함
- ex. ```sudo mkfs.ext4 /dev/sdb5```
##### 2.2.1.6 마운트
- ```sudo mount -t ext4 /dev/sdb5 /home/ubuntu```
##### 2.2.1.7 확인
- ```df```
##### 2.2.1.8 파티션 설정 저장
- /etc/fstab 저장
#### 2.2.2 프린터
- 프린트 관련 명령어 위주로 살펴볼 것
##### 2.2.2.1 리눅스 프린팅 시스템의 개요
##### 2.2.2.2 CUPS(Common Unix Printing System)
##### 2.2.2.3 프린터 추가
- http://localhost:631
- http://127.0.0.1:631
- 병렬 포트 연결 : /dev/lp0에서 접근 가능
- USB 포트 연결 : /dev/usb/lp0에서 접근 가능
##### 2.2.2.4 프린트 출력
- BSD 명령어 : lpr [options] [filename] : 데이터를 LPD(Linux Printing Daemon)에 전달하여 출력
  - ```cat file.txt > /dev/lp0```
  - ```cat file.txt > lpr```
  - ```lpr file.txt```
  - ```lpr -# 3 file```
- SystemV 명령어 : lp [options] [filename] : 문서를 출력함
  - ```lp -n 3 file.doc```
##### 2.2.2.5 프린트 취소
- BSD : ```lprm [options] [filename]```
- System V : ```cancel [options] [filename]```
##### 2.2.2.6 프린트 작업 및 큐 관리
- lpc : BSD 계열 명령어 프린트 대기열 상태 제어
- lpq : BSD 계열 명령어
- lpstat : System V 계열 명령어
#### 2.2.3 사운드 카드
- 사운드 카드에 대한 설명 위주로 살펴볼 것
##### 2.2.3.1 리눅스 사운드 카드 개요
- OSS(Open Sound System), ALSA(Advanced Linux Sound Architecture)
##### 2.2.3.2 오픈 사운드 시스템(OSS)
- 정의 : 유닉스 호환 운영체제를 위한 사운드 생성 및 캡처 인터페이스이며 인터페이스 구현을 위한 디바이스 드라이버를 의미
- 표준 사운드 응용프로그램을 구현할 수 있는 시스템을 제공
- POSIX 표준 API 존재
- 라이선스 문제 때문에 ALSA로 대체됨
##### 2.2.3.3 고급 리눅스 사운드 아키텍처(ALSA)
- 리눅스에 포함된 사운드 카드 소프트웨어
- GPL, LGPL 라이선스
- alsactl : 다수의 사운드 카드 제어
- alsamixer : alsa 그래픽 믹서 프로그램 호출
- cdparanoia : CD 재생정보 및 음악파일 추출
#### 2.2.4 스캐너
- 개념 이해 위주로 살펴볼 것
##### 2.2.4.1 SANE(Scanner Access Now Easy)
- 스캐너에 대한 표준 액세스를 제공하는 API
- 예를 들어, 시스템 A에서 시스템 B의 SANE API를 통해서 스캐너를 제어할 수 있음
- GPL 오픈소스 라이브러리 이며 리눅스 뿐만 아니라 여러 운영체제에서 지원함
##### 2.2.4.2 XSANE(X based interface for the SANE)
- SANE의 그래픽 기반 응용 프로그램


## 3. 시스템 보안 및 관리
### 3.1 시스템 분석
#### 3.1.1 시스템 로그 개요 및 분석
##### 3.1.1.1 시스템 로그의 개요
- 시스템 로그에는 실행 애플리케이션, 이벤트 정보 등이 시간 순서로 저장됨
- 시스템 로그는 /var/log 디렉토리에서 확인할 수 있음
- 시스템 리부팅 시, 로그 파일을 제거됨
##### 3.1.1.2 주요 시스템 로그 파일
- /var/log/messages(syslog) : 전체 시스템의 모든 동작 사항과 정보 메세지가 로깅됨
- /var/log/httpd : 웹 서버 아파치의 httpd 데몬이 기록하는 로그 파일
- /var/log/xferlog : ftp 접속 연관 로그 파일
- /var/log/secure : 시스템 로그인 행위가 기록된 로그 파일
- /var/log/lastlog : 사용자의 마지막 로그인 기록을 담고, 바이너리 형식이므로 lastlog 명령어로 확인 가능
- /var/log/wtmp : 각 사용자의 모든 로그인 및 로그아웃 기록을 담고, 바이너리 형식이므로 last 명령어로 확인 가능
- /var/log/btmp : 모든 로그인 실패 기록을 담고, 바이너리 형식이므로 lastb 명령어로 확인 가능
- /var/log/utmp : 사용자의 현재 로그인 상태를 담은 로그
##### 3.1.1.3 시스템 로그 파일 명령어
- dmseg : 커널 링 버퍼 출력 및 제어 명령어(옵션 : -c, -T, -level)
- lastlog : /var/log/lastlog 파일의 로그를 확인하는 명령어(옵션 : -b, -t, -u)
- last : /var/log/wtmp 파일 로그를 확인하는 명령어(옵션 : reboot, -x, -f)
- lastb : /var/log/btmp 파일 로그를 확인하는 명령어
#### 3.1.2 시스템 로그 관리
##### 3.1.2.1 시스템 로그 관리 개요
- syslog
  - 1980년대 에릭 알만이 개발함
  - /etc/syslog.conf 기반 /var/log 디렉토리에 로그 생성
- rsyslog
  - syslog보다 더 인기있음
  - 2004년 레이너 게르하드를 주축으로 오픈소스로 제작되었고 ip 통신을 통한 로그 기능 구현 목적
  - /etc/rsyslog.conf에 데몬 환경설정이 저장됨. 부팅 시 데몬이 실행되어 로그를 수집함
##### 3.1.2.2 rsyslog를 통한 로그 관리
- rsyslog 관련 파일
  - /etc/rc.d/init.d/rsyslog : rsyslogd 데몬을 실행하는 스크립트로 start, stop, restart 명령어 실행 가능
  - /etc/rsyslog.conf : rsyslogd 데몬 환경설정 파일
- /etc/rsyslog.conf 파일구조 **(중요)**
  - Global directives, Templates, Output channels, Rules로 이루어짐. Rules가 가장 중요
  - Rules : selector + action
    - selector
      - facility : 로깅할 프로그램을 지정함
      - priority : 로그 메세지 수준 정의. 지정 수준보다 높은 로그만 기록됨
    - aciton : 네트워크를 통해 로그 메세지를 전달하는 등의 행위를 정의함
  - 예시 : [facility,].[priority];[facility,].[priority]   [log 파일 경로]
    - *.=crit;user.none  /var/log/critical : 모든 facility의 로그 중 crit 레벨인 것과 user 서비스의 로그는 /var/log/critical에 기록함
    - *.alert  *
    - cron.*  root,francis
    - authpriv.*   /dev/tty1
    - mail.*;mail.!=debug  /var/log/mail-messages
    - auth,authpriv.alert  @192.168.0.1
##### 3.1.2.3 로테이션을 통한 로그 용량 관리
- logrotate : 로그 파일 시스템이 꽉 차는 것을 막고 디스크 공간을 효율적으로 사용하는 유틸리티
##### 3.1.2.4 journalctl을 통한 로그 관리
- systemd에서 제공하는 커널 및 저널 로그  ㅘㄴ리

### 3.2 시스템 보안 및 관리
#### 3.2.1 시스템 보안 관리
##### 3.2.1.1 리눅스 보안 소개
##### 3.2.1.2 물리적 보안
##### 3.2.1.3 시스템 보안
- sudo 사용은 안전하나 root 사용자 로그인 제한을 막으려면 /etc/passwd 파일의 루트 사용자 셸 설정은 /sbin/nologin으로 변경 필요
##### 3.2.1.4 서비스 및 운영 보안
##### 3.2.1.5 파일 시스템 보안
- chmod, chown, set-UID, sticky-bit와는 별개로 파일 속성 설정을 통한 보안
- chattr : 파일 속성 수정
  - chattr +i [file] : 파일을 삭제하거나 이름을 변경할 수 없음
  - chattr +a [file] : 파일에 오직 추가만 가능. 덮어쓰기 및 삭제 불가능
- getfacl : 파일의 ACL 확인
- setfacl : ACL 설정
  - ex. setfacl [-options] [u:][사용자명][:권한] [filename]
  - ex. setfacl -m d:u:francis:rw /fransis
  - ex. getfacl file1 | setfacl --set-file=- file2
##### 3.2.1.6 네트워크 보안
#### 3.2.2 SELinux(Security-Enhanced Linux)
##### 3.2.1.1 SELinux 개요
- root 권한을 획득하면 시스템 전체를 제어할 수 있다는 점이 위협이 될 수 있음
- root 권한이더라도 미리 지정한 권한으로 리소스 접근을 제한하는 방법임
##### 3.2.1.2 SELinux 설정 및 해제
#### 3.2.3 시스템 보안 유틸리티
##### 3.2.3.1 SSH
##### 3.2.3.2 PAM(Pluggable Authentication Module)
- 리눅스 시스템에서 애플리케이션을 사용하고자 할 때 동적으로 인증할 수 있는 라이브러리
- /etc/pam.conf, /etc/pam.d/ 에서 설정함
- 환경설정 파일의 control-flag : 인증 성공, 실패에 따른 행위를 설정함
  - requisite : 반드시 성공. 실패 시 실패 반환
  - required : 반드시 성공. 단, 동일한 module-type을 모두 체크 후 실패 반환
  - sufficient : 이전 모듈 체크가 성공하면 나머지는 체크하지 않음
##### 3.2.3.3 sudo
- /etc/sudoers에 등록된 사용자만 sudo 명령 가능. 해당 파일은 visudo로 사용 권장
- /etc/sudoers
  - user hostname=(runas-user:runas-group) commands
  - ex. francis ALL=(ALL:ALL) ALL
  - ex. chris myhost=(webuser:webusers) ALL
#### 3.2.4 주요 보안 도구
##### 3.2.4.1 nmap
##### 3.2.4.2 tcpdump
##### 3.2.4.3 tripwire
##### 3.2.4.4 Nessus
##### 3.2.4.5 GnuPG
##### 3.2.4.6 John the Ripper

### 3.3 시스템 백업
#### 3.3.2 파일 백업
##### 3.3.2.1 tar
- 전체 백업
  - tar -cvfp [backup.tar] /file
  - tar -xvf [backup.tar]
- 증분 백업
  - level 0 : 최초 파일 백업
    - tar -g snapshot -cvfp [backup-lv0.tar] /file
  - level 1 : 변경된 내용만 백업
    - tar -g snapshot -cvfp [backup-lv1.tar] /file
  - 해제
    - tar -xvf backup-lv0.tar -C
    - tar -xvf backup-lv1.tar -C
##### 3.3.2.2 cpio

#### 3.3.3 파일 시스템 및 디스크 백업
##### 3.3.3.1 dump
- 디스크 파티션 단위로 백업할 때 사용 
##### 3.3.3.2 dd
- 파티션이나 디스크 단위로 백업할 때 사용
- 백업 시 블록 단위로 지정 가능
##### 3.3.3.3 restore
- dump로 생성한 백업 파일 복원
#### 3.3.4 네트워크 백업
##### 3.3.4.1 rsync
- 네트워크로 연결된 원격 컴퓨터의 파일들을 동기화하는 도구
- root 권한 불필요
- 로컬 시스템 백업 시에는 별다른 설정 필요 없음







































































































































































































