# MySQL
Real MySQL 서적 및 MySQL Document를 참고하여 요약함

## 1. 설정

### 1.1 서버 시작 및 종료
- 일반적으로 /etc/my.cnf 환경설정 파일을 읽은 후,
- /usr/lib/systemd/mysqld.service 파일을 통해 MySQL 서버를 실행함
```shell
systemctl start mysqld
```

### 1.2 서버 연결
+ mysql.sock을 이용한 연결
+ tcp/ip를 이용한 연결
+ mysql -uroot -p 명령어를 통한 연결

### 1.3 서버 설정
- my.cnf : MySQL 시스템 변수 저장
  - mysql --help로 서버 시작 시 my.cnf 파일을 읽는 우선순위 확인 가능
- 시스템 변수 : DB 설정과 관련된 변수
  - ```SHOW VARIABLES;``` 명령어로 확인 가능
  - MySQL 메뉴얼에서 자세한 시스템 변수 확인할 것
 
### 1.4 글로벌 변수와 세션 변수
- 글로벌 변수 : 서버 인스턴스 전체에 영향을 미치는 변수(ex. InnoDB 스토리지 엔진 버퍼 크기)
- 세션 변수 : 현재 서버에 연결한 클라이언트 세션에 부여되는 변수(ex. autocommit)
- Both : 서버가 기억을 하고 있다가 클라이언트와의 커넥션 생성 시 시스템 변수를 해당 변수로 변경하기 위해 사용하는 변수

### 1.5 정적 변수와 동적 변수
- 정적 변수 : 서버 기동 중인 상태에서 변경할 수 없는 변수(시스템변수 설정 파일 값)
- 동적 변수 : 서버 기동 중인 상태에서 변경할 수 있는 변수(메모리 적재된 변수)
- SET, GLOBAL, PERSIST 명령어를 적절히 사용하여 동적 및 정적 변수 변경 가능

### 1.6 SET PERSIST
- MySQL 8.0 버전 이상에서 지원
- ```SET PERSIST [글로벌 변수=ㅁㅁ];``` 커맨드 시, 현재 메모리에 적재된 변수를 변경하고 mysqld-auto.cnf에 저장함
- 그리고 MySQL 서버 재시작 시, my.cnf와 mysqld-auto.cnf를 스캔하여 이를 글로벌 변수로 설정함
- 세션 변수에는 ```SET PERSIST```를 적용할 수 없음
- ```SET PERSIST_ONLY [글로벌 변수=ㅁㅁㅁ];``` 명령으로 mysqld-auto.cnf에만 저장할 수 있음
  - 현재 메모리에 적재된 글로벌 변수를 변경할 수 없을 때 사용함
    
***

## 2. 사용자 및 권한

### 2.1 사용자 식별
- MySQL은 호스트명과 ip 모두 계정의 일부임
  - ex1. 'mysql'@'127.0.0.1' : 로컬 호스트에서만 접근 가능한 계정인 mysql
  - ex2. 'mysql'@'%' : 모든 호스트에서 접근 가능한 계정인 mysql

### 2.2 사용자 계정 관리
- 





