# 리눅스 실무의 이해

## 1. 리눅스 개요

### 1.1 운영체제의 개요

#### 1.1.1 운영체제 정의 및 목적, 역할

- 정의 : 하드웨어 조작을 위한 사용자 인터페이스

- 목적 : 처리 능력 향상, 반환 시간 최소화, 신뢰도 향상, 가용성 확장

- 역할 : 하드웨어 리소스 관리

#### 1.1.2 운영체제 구조와 기능

- 구조 : 시스템 기능 호출을 위해 응용 프로그램, shell 등을 사용할 수 있는 하드웨어 추상계층 제공

- 기능 : 프로세스, 메모리, 장치 I/O, 파일시스템, DB, 네트워크 등 자원 및 접근 관리

#### 1.1.3 운영체제 운용 기법 및 발전 순서

- 일괄 처리(Batch System) : 여러 작업을 한번에 묶어서 처리함. 처리 중에는 I/O Blocking 발생하고 현재 이외 다른 작업 수행을 Blocking 발생

- 다중 프로그래밍(Multi Programming System) : I/O Blocking 시 다른 작업을 수행하는 Non-Blocking 기법

- 시분할(Time Quantum System) : 여러 작업을 일정 Time Quantum으로 번갈아가며 수행하는 기법(동시성 확보)

- 다중 처리(Multi Processing System) : 여러 개의 프로세스를 통해 여러 작업을 동시에 수행하는 기법(병렬성 확보)

  - 다중 처리는 비대칭적(주-종 프로세스) 및 대칭적 다중처리로 나뉜다.

- 실시간 처리(Real Time System) : 요구된 시간 안에 작업을 실행하는 기법
  - soft real time, hard real time processing

- 다중 모드(Multi-Mode System) : 위의 방식을 모두 혼용해서 사용하는 기법

- 분산 처리(Distributed Processing System) : 독립적인 시스템 간의 통신을 통해 작업을 분산하여 처리하는 기법

#### 1.1.4 운영체제 사례

- 리눅스는 데스크톱, 서버, IoT에 사용되는 운영체제

- 경량형 운영체제 : Contiki, TinyOS, RIOT

- 모바일 운영체제 : Tizen(삼성전자 & 인텔), raspbian, webOS(LG 개발 IoT용)

### 1.2 리눅스 기초

#### 1.2.1 리눅스 개요

##### 1.2.1.1 정의와 의미

- 정의 : 개인용 데스크톱, 서버, 모바일 기기, 임베디드 기기 등에 사용되는 오픈소스 운영체제

##### 1.2.1.2 리눅스 일반적 특징

- 이식성(Portablility) : 여러 CPU 플랫폼에 적용 가능

- 자유 소프트웨어(free software) : 오프소스 운영체제로 여러 개발자들이 기여 중

- 멀티 유저(multi-user) : 다양한 사용자가 시스템 리소스에 접근 가능

- 멀티 프로그래밍(multi-programming) : 다양한 작업을 동시에 실행 가능

- 계층적 파일 시스템(hierarchical file system) : 루트 디렉토리(/) 하위에 사용자 관련 디렉토리(/usr), 디바이스 관련 디렉토리(/dev) 등이 있음

- 쉘(shell) : 리눅스 명렁어 실행 프로그램. 명령어 해석, 프로그래밍, 환경설정 기능을 제공함

- 보안(security) :
  - 주체(사용자)에 객체(디렉토리 및 파일) 접근 권한을 부여하는 **임의 접근 제어**(접근 통제 모델) 정책 제공
  - 리눅스 자체를 네트워크 노드 및 라우터로 활용할 수 있음. **netfilter, iptables, ebtables, arptables** 제공
  - 주체(사용자)와 객체(디렉토리 및 파일)에 각각 권한 레벨을 부여하여 주체가 객체보다 레벨이 높거나 낯을 때만 접근할 수 있는 **강제 접근 제어** 제공

##### 1.2.1.3 리눅스 기술적 특징

- 모놀리틱 커널(Monolithic kernel) : 단일 커널로 리소스(CPU, 메모리 등) 관리 기능을 제공함
  - 일부 기능 변경 시, 커널 전체를 컴파일 해야하는 단점도 있으나 동적 설정이 가능한 커널 모듈도 존재함

- 장치의 파일화 : 모든 시스템 자원을 파일로 관리함. 디렉토리, 일반파일, 특수파일(장치 파일, 파이프, 소켓)로 나뉨
  - 장치 파일 : 문자 장치 파일 / 블록 장치 파일
  - 파이프 : 프로세스 간 통신을 위해 존재하는 파일
  - 소켓 : 응용 프로그램간 소켓 프로그래밍을 위해 존재하는 파일

- 다양한 파일 시스템 지원
  - 리눅스 자체 파일 시스템 : ext2, 3, 4
  - 윈도우 파일 시스템 : FAT32, NTFS
  - 네트워크 파일 시스템 : SMB, CIFS
  - 복구용 파일 시스템 : 저널링 파일 시스템

- 가상 메모리 : 물리적인 메모리 크기로 인한 한계를 극복하기 위한 기법
  - 자주 사용하는 프로그램을 물리 메모리에 로드하고, 그렇지 않은 것은 디스크에 저장하는 페이징 기법 사용
  
- 스왑(Swap)
  - 메모리에서 자주 사용되지 않은 것을 swap-out할 때 사용되는 디스크 공간(일명 swap space)
  - 휘발성 메모리 데이터를 비휘발성 디스크에 저장함
  - **/etc/sysctl.conf에서 vm.swapiness**에 swap 사용 여부 설정함. 만약 10으로 설정되어 있으면 메모리가 10% 사용될 때 swap을 시작함
  - **free 명령어**로 swap space 및 메모리 사용량 확인 가능
  - swap space 크기는 동적으로 변경이 **불가능**함
 
- 정적 라이브러리와 동적 라이브러리
  - 정적 라이브러리 : 프로세스마다 로드하며, 실행 속도가 빠르고 크기가 크며 배포에 제약이 없음 
  - 동적 라이브러리 : 프로세스끼리 공유하며, 실행 속도가 느리고 크기가 작으며 배포에 제약이 있음
      - /etc/ld.so.conf 파일과 환경변수 LD_LIBRARY_PATH를 확인하여 동적 라이브러리 로드
      - /etc/ld.so.cache 파일을 활용하여 빠른 동적 라이브러리 검색 가능(conf 파일 변경시 ldconfig로 캐시 업로드 필요)

- 파이프 : 프로세스의 표준 출력을 다른 프로세스로 보내기을 위해 사용(|)

- 라다이렉션 : 프로세스 입력 및 출력 (<<, >>)

##### 1.2.1.4 리눅스 장단점

오픈 소스의 장단점과 동일함

#### 1.2.2 리눅스와 GNU 그리고 오픈소스 라이센스

- GNU GPL(General Public License)는 자유 소프트웨어에 배포되는 라이센스이다. GPL 라이센스가 있는 소프트웨어를 사용하는 다른 소프트웨어는 GPL 라이센스 배포가 필요하다.

- 리눅스는 유닉스의 POSIX 표준을 따르나 소스코드는 일체 사용하지 않았다. 리눅스 또한 GNU GPL 라이센스에 등록되어 있다.

- copyleft 라이센스

- GPL 이외의 다양한 오픈소스 라이센스
  - LGPL : LGPL 프로그램을 사용하는 소프트웨어는 LGPL 라이센스가 해당되지 않으나, 소스코드를 수정한 경우에는 LGPL 라이센스에 해당됨
  - BSD : 버클리 대학교 오픈소스 라이센스. 2차 저작물은 재배포 및 소스 공개가 의무가 아님
  - 아파치 라이센스 : 2차 저작물을 상업적 목적으로 사용할 수 있음. 단, 아파치 라이센스를 따른다고 명시해야함
  - MPL 라이센스
  - MIT 라이센스 : 라이센스 및 저작권만 명시하면 무엇이든 사용 가능

#### 1.2.3 리눅스의 역사와 리눅스 배포판

##### 1.2.3.1 리눅스 역사

##### !.2.3.2 리눅스 분류 및 특징

- 슬렉웨어 : 가장 오래된 배포판
- 데비안 : 자발적 커뮤니티에 의한 배포판
  - 우분투
  - raspbian
  - chromeOS
- 레드햇 : 유료서비스를 통한 수익 창출 추구.
  - centOS : 단, centOS는 개인 사용자에게 무료로 배포됨(apt)
  - Fedora

***

## 2. 리눅스 시스템 이해

### 2.1 리눅스와 하드웨어

#### 2.1.1 하드웨어의 이해

##### 2.1.1.1 컴퓨터의 구성요소

- 중앙처리장치(CPU) : 레지스터(연산을 위한 임시 저장소), 연산장치, 제어장치
- 주기억장치(Main Memory) :
  - RAM : 휘발성이나 주소를 따라 시간상 동일하고 빠르게 접근 가능
  - ROM : 비휘발성이며 BIOS(컴퓨터 부팅용)같은 프로그램이 적재됨
    - MASK : 수정 불가
    - PROM : 1회 수정 가능
    - EPROM : 계속해서 수정 가능
- 입출력장치
- 보조기억장치 : 주기억장치 대비 속도가 느림. SSD, DVD 등

##### 2.1.1.2 리눅스 설치를 위한 하드웨어 요구사항

- 중앙처리장치(CPU) : GNU C가 지원하는 CPU 환경이면 리눅스 이식 가능
- 보조기억장치(RAM) : 대부분 모두 지원(32bit, 64bit 운영체제)
- 하드디스크 : 대부분 지원하며 **/dev/sdX** 파일에 매핑됨
- 그래픽카드 : 대부분 지원하며 **lspci** | grep -I vga 명령어로 확인 가능
- 랜카드 및 주변장치 : 대부분 모두 지원

#### 2.1.2 하드웨어 선택

##### 2.1.2.1 RAID 개요

- 복수의 디스크의 고성능 및 신뢰성(무정지)을 위해 중복된 데이터를 나눠서 저장하는 기술이다. 1개의 RAID는 운영체제에서 1개의 디스크로 인식됨
- RAID 0는 고성능을 추구하고, RAID 1은 무정지를 추구함
- 스트라이핑 기술 : 고성능을 위해 연속된 데이터를 여러 디스크에 라운드 로빈으로 저장함
- 미러링 기술 : 무정지를 위해 하나 이상의 장치에 중복하여 데이터를 저장함

##### 2.1.2.2 RAID 종류

- RAID 0 : 스트라이핑 기술을 적용하여 처리속도가 빠름. 그러나, 에러 발생 시 복구가 불가능함.
- RAID 1 : 미러링 기술을 적용하여 읽기속도가 빠름. 그러나, 중복 저장 때문에 처리 속도가 중복된 정도에 따라 줄어듦.
- RAID 2 : 비트 레벨로 스트라이핑 기술을 적용함. 오류 정정 부호를 저장하여 복구도 가능함. 그러나, 추가 비트연산이 필요하므로 입출력 성능이 떨어짐
- RAID 3 : 바이트 레벨로 스트라이핑 기술을 적용함. 패리티 디스크를 통해 오류 체크 및 복구 가능. 쓰기 및 읽기 성능 모두 우수함
- RAID 4 : 블록 레벨로 스트라이핑 기술을 적용함.
- RAID 5 : 블록 레벨로 스트라이핑 기술을 적용하나, 패리티 디스크가 따로 존재하지 않음(가장 많이 사용)
- RAID 0+1 : RAID 0 구성 세트에 RAID 1을 적용  
- RAID 1+0 : RAID 1 구성 세트에 RAID 0을 적용

##### 2.1.2.3 디스크 인터페이스
- IDE : 병렬 인터페이스. PATA라고도 불림
- SATA : 직렬 인터페이스
- SCSI : 병렬 인터페이스
- SAS : 병렬 인터페이스


##### 2.1.2.4 LVM

- 디스크와 파일 시스템의 중간 장치로, 파일을 디스크에 저장할 때 LVM이 매핑한 Logical Volume에 저장하도록 함.
- LVM 구성도
  - VG(Volume Group) : 여러 PV를 갖는 그룹. PV와 VG는 N:1 관계
  - PV(Physical Volume) : 기존 물리적 디스크를 LVM에서 사용할 수 있도록 논리적으로 분할한 개념
  - PE(Physical Extent) : 일정한 크기의 블록(4MB). PE와 PV 는 N:1 관계
  - LV(Logical Volume) : VG에서 필요한 만큼 할당하여 만들어지는 공간(ex. /usr, /home)
  - LE(Logical Extent) : 일정 크기의 블록(4MB). LE와 LV는 N:1 관계. LE와 PE는 1:1 관계

 
### 2.2 리눅스 구조

#### 2.2.1 부트 매니저

- 보조기억장치에 존재하는 운영체제를 주기억장치에 로드하기 위한 프로그램
- 대표 부트 매니저 : LILO(가장 오래됨), GRUB(대화형 인터페이스), GRUB2

#### 2.2.2 주요 디렉토리 구조

- 리눅스 주요 디렉토리
  - /bin :   사용자 주요 명령어 (cd, ls)
  - /boot :   부팅에 필요한 파일 위치
  - /dev :   디바이스 파일이 위치
  - /etc :   시스템 환경변수 및 시스템 초기화(부팅) 시 사용하는 스크립트
  - /home :   로그인 사용자 파일
  - /lib :   시스템 라이브러리
  - /media :   CD-ROM 등 이동식 디스크
  - /mnt :   디바이스 마운트 시 사용하는 임시 디렉토리
  - /opt :   애플리케이션 소프트웨어 패키지
  - /proc :   메모리에 존재하는 프로세스들이 파일로 저장됨. 프로세스 상태 정보, 하드웨어 정보 등 존재
  - /root :   루트 사용자 디렉토리
  - /sbin :   시스템 주요 명령어
  - /var :   로그, 스풀 등 저장(ex. /var/log)
  - /sys :   장치 정보 제공
  - /run :   프로세스 런타임 데이터 저장

#### 2.2.3 부팅과 셧다운

##### 2.2.3.1 부팅 개요
- 부팅 : BIOS는 MBR(Master Boot Record. 하드디스크 0번 섹터)로부터 부트 로더를 실행함

##### 2.2.3.2 부팅 절차
- 부팅 절차 :
  - 시스템 시작 및 하드웨어 초기화
  - 1단계 부트로더 : MBR(0번 섹터. 512 bytes)에 존재. 공간이 부족하므로 파일 시스템을 해석할 수 있는 기능이 없으므로 1.5단계 부트로더로 점프할 기능만 제공
  - 1.5단계 부트로더 : 파일 시스템 드라이버 포함. 2단계 부트로더로 점프
  - 2단계 부트로더 : 파일 시스템에 존재. 부트 매니저에 따라 운영체제를 선택하고, 운영체제를 위한 커널을 메모리에 로드하고 커널에 제어권 전달
  - 커널 : /sbin/init 또는 심볼릭 링크 /usr/lib/systemd/systemd 실행하여 init 프로세스 실행
  - INIT 프로세스 : /etc/inittab 파일을 읽어서 적절한 시스템 스크립트(/etc/rcㅁ.d)실행
    - run level : 0-종료, 1-단일 사용자(복구용), 2-다중 사용자, 3-네트워크 기능 포함한 다중 사용자, 5-X 윈도우 다중 사용자, 6-시스템 재부팅
  - systemd : .target 파일을 실행하고. run level은 INIT 프로세스와 유사함
  - 사용자 프롬프트

##### 2.2.3.3 로그인, 로그아웃, 시스템 종료

- 로그인 공지 기능
  - /etc/issue : 로컬에서 로그인 시도 시, 로그인 전 해당 메세지 출력
  - /etc/issue.net : 원격으로 로그인 시도 시, 로그인 전 해당 메세지 출력
  - /etc/motd : 로그인 성공 후, 해당 메세지 출력

- 로그아웃 : logout 명령어 또는 ctrl + d로 가능
  - /etc/profile에서 TMOUT을 설정하여 자동 로그아웃 가능
  - source /etc/profile 커맨드 시 즉시 실행 가능

- 종료 : shutdown -h [+minutes] 으로 종료 가능
  - shutdown -r [+minutes]으로 재부팅 가능


##### 2.3.3.4 GRUB Legacy 패스워드 설정 및 복구

부트로더 실행시 사용하는 패스워드와 관련된 내용

- 패스워드 설정 : /boot/grub/grub.conf

```shell
  # grub-crypt 명령어로 encoded password 생성 후 아래 추가
  hiddenmenu
  password --encrypted [SHA-256, 512 encoded password]


  또는

  # md5-crypt 명령어로 encoded password 생성 후 아래 추가
  hiddenmenu
  password [my password]
```

- 패스워드 복구
  - root 계정 접근 가능 : 위 설정에서 password 삭제
  - root 계정 접근 불가능 : 부팅 메뉴에서 Rescued Install System -> /mnt/sysimgage/boot/grub/grub.conf 수정


##### 2.3.3.5 GRUB2 Legacy 패스워드 설정 및 복구

GRUB Legacy와 유사함... 추후 확인

#### 2.3.4 systemd

- 부팅시 사용하던 init을 대체하기 위한 시스템 서비스







































































