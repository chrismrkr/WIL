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
- 시스템 계정과 일반 계정
  - 시스템 계정 : SYSTEM_USER 권한이 있는 계정(관리자 계정)
  - 일반 계정 : 응용프로그램 및 개발자를 위한 계정
- 계정 생성
  - ```CREATE USER '[사용자명]'@'[허용 호스트 IP]' IDENTIFIED WITH '[패스워드]';```
  - 계정 생성 관련 옵션
    - REQUIRE : SSL/TLS 채널 사용여부
    - PASSWORD EXPIRE : 유효기간 설정. 미설정 시 default_password_lifetime 시스템 변수를 따름
    - PASSWORD HISTORY : 한번 사용했던 패스워드는 재사용할 수 없도록 만드는 것과 관련된 옵션
    - PASSWORD REUSE INTERVAL : 한번 사용했던 패스워드 재사용 금지 기간 설정. 미설정 시 password_reuse_interval 시스템 변수를 따름
    - PASSWORD REQUIRE : 패스워드 만료로 인하여 재설정 시 현재 패스워드를 필요로 할지 말지 결정하는 옵션. 미설정 시 password_require_current 시스템 변수를 따름

### 2.3 비밀번호 관리 
- 고수준 비밀번호 : componet_validate_password를 설치하여 패스워드 유효성 관리 가능
- 이중 패스워드 : 햔재 패스워드, 이전 패스워드 중 하나만 일치해도 인증에 성공하는 방식
  - ```ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password' RETAIN CURRENT PASSWORD;```
    - 새로운 패스워드를 설정하고 현재 패스워드는 이전 패스워드로 설정함
  - ```ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password' DISCARD OLD PASSWORD;```
    - 패스워드를 변경하고 현재 패스워드는 삭제함

### 2.4 권한
- ```GRANT``` 커맨드로 권한 부여
- 객체 권한 : 데이터베이스나 테이블을 제어하기 위한 권한(DB, TABLE 지정 필요)
- 글로벌(정적) 권한 : 데이터베이스, 테이블 이외의 객체를 제어하기 위한 권한
- 동적 권한 : MySQL 서버가 시작하면서 동적으로 생성되는 권한
- 권한 부여 방법
  - 객체 권한 : ```GRANT [권한 목록(SELECT, UPDATE, ...)] ON [db].[table] TO '[사용자명]'@'[허용 호스트 ip]';```
  - 정적 권한 : ```GRANT [정적 권한 목록] ON *.* TO '[사용자명]'.'[허용 호스트 ip]';```
  - DB 권한 : ```GRANT [권한 목록] ON [db].* TO '[사용자명]'.'[허용 호스트 ip]';```

### 2.5 역할
- 여러개의 권한이 부여된 접속 불가능한 계정을 만드는 것. 이를 역할이라 함
- 역할과 계정은 MySQL 내부적으로 동일함
- 역할 생성 과정
  - ```CREATE ROLE [역할 이름];```
  - ```GRANT [권한 목록] ON [db].[table] TO [역할 이름];```
  - ```CREATE USER '[사용자명]'@'[허용 호스트 ip]' IDENTIFIED BY '[password]';```
  - ```GRANT [역할 이름 목록] TO '[사용자명]'@'[허용 호스트 ip]';```
- 주의 사항
  - 사용자 로그인 시, 역할이 자동으로 부여되지 않음
  - activate_all_roles_on_login=ON 시스템 변수를 통해 로그인시 자동으로 역할이 부여되도록 함

***

## 3. 아키텍처
MySQL은 MySQL 엔진과 스토리지 엔진으로 구성되고, 스토리지는 InnoDB를 기본으로 채택함

### 3.1 MySQL 엔진
DB 커넥션 핸들링, SQL 파싱, 쿼리 최적화를 담당함
#### 3.1.1 스레딩 구조
- performance_schema.thread 테이블에서 현재 실행 중인 스레드 확인 가능
  - FOREGROUND thread : 클라이언트가 요청하는 쿼리를 처리함
    - 동작 메커니즘 : 스레드 처리 -> 커넥션 종료 -> 스레드 캐시 반납
      - 만약 thread_cache_size 시스템 변수 만큼의 스레드 캐시가 존재하면, 커넥션 종료 후 반납하지 않고 스레드를 종료함
    - 스레드 캐시란? : 유휴 포어 그라운드 스레드를 갖고 있는 캐시
  - BACKGROUND thread : InnoDB 스토리지 엔진에 특정 작업을 처리함
    - 로그 쓰기 스레드
    - InnoDB 버퍼 데이터 풀 -> 디스크 쓰기 스레드
#### 3.1.2 메모리 할당 방식
- 글로벌 메모리 영역
  - 시스템 변수에 설정한 만큼 운영체제에서 할당함
  - 모든 MySQL 스레드끼리 공유함
  - 테이블 캐시, InnoDB 버퍼 풀, InnoDB 해시 인덱스, InnoDB 리두 로그 버퍼 등에 해당됨
- 로컬(세션) 메모리 영역
  - MySQL 클라이언트 - 서버 연결 시 생성되는 스레드가 사용하는 메모리
  - 커넥션을 연결하는 동안 계속 유지함(커넥션 버퍼, 아웃풋 버퍼 메모리)
  - 쿼리를 수행할 때 할당 및 해제함(소트 버퍼, 조인 버퍼 메모리)
#### 3.1.3 플러그인 스토리지 엔진 모델
- 스토리지 엔진, 검색어 파서, 사용자 인증 플러그인 등이 존재함
- ```SHOW ENGINES;```로 지원하는 스토리지 엔진 확인 가능
- ```SHOW PLUGINS;```로 지원하는 플러그인 확인 가능
#### 3.1.4 쿼리 실행 과정
- 쿼리 파싱 -> 전처리 -> 옵티마이저 -> 실행 엔진 -> 핸들러
#### 3.1.5 쿼리 캐시
- MySQL 8.0 이상에서는 삭제됨
- 동일한 쿼리의 결과를 캐시함. 그러나, 데이터 변경으로 인한 캐시 Flush 시 삭제됨
#### 3.1.6 스레드 풀
- MySQL 엔터프라이즈 버전에서만 사용 가능하나 Percona Server에서 스레드 풀 플러그인 라이브러리를 설치해서 사용 가능
- thread_pool_size 시스템 변수로 설정 가능(일반적으로 코어 수와 동일하게 함)
- thread_pool_stall_limit 시스템 변수로 스레드 풀 full 상태를 밀리 초 단위로 체크
- thread_pool_max_threads에 따라 스레드 풀 full 시 추가 스레드 생성 여부 결정
#### 3.1.7 메타 데이터
- 테이블 메타 데이터는 InnoDB 테이블에 저장됨

### 3.2 InnoDB 스토리지 엔진
#### 3.2.1 기본 키에 의한 클러스터링
- InnoDB 모든 테이블은 기본 키가 클러스터링 인덱스이므로 기본 키를 통한 Range Search 성능이 높음
- 반면 MyISAM은 클러스터링 인덱스가 없음
#### 3.2.2 외래키 지원
- 외래키를 생성하면 자동으로 외래 키에 대한 인덱스가 생성됨
- 테이블 변경 시 외래키에 의한 정합성 체크 과정이 자식 테이블로 계속 전파되고, 이에 따라 Lock이 발생하므로 데드락에 주의해야함
- foreign_key_checks=OFF 시스템 변수를 통해 일시적으로 외래키 관계 체크를 멈출 수 있음
#### 3.2.3 MVCC(Multi Version Concurrency Control) 지원
- Lock을 사용하지 않고도 일관된 읽기를 제공하기 위해 레코드 레벨의 트랜잭션을 지원함
- 테이블이 변경되는 프로세스(격리수준 : REPEATABLE READ)
  - DML(UPDATE, INSERT, DELETE) Query 발생
  - InnoDB 버퍼 풀 변경
  - Undo Log 기록
  - Commit -> 버퍼 풀 내용을 디스크에 기록 & Undo Log 삭제
- 위 상황에서 Commit 되지 않은 상태에서 다른 트랜잭션이 해당 데이터를 Read 하려고 하면 Undo Log에 있는 값을 읽게 함
- 이를 통해 Lock 없이 MVCC를 제공함
- 만약 Undo Log가 지워지지 않고 계속 남아있으면 DB 성능에 악영향을 줌
#### 3.2.4 자동 데드락 감지 지원
- 잠금 대기 목록(Wait-for List) 그래프를 통해 데드락을 모니터링함
- 만약 데드락이 감지되는 경우, 언두 로그가 적은 쪽을 롤백함
- MySQL 엔진은 레코드 락을 감지할 수 있으나 테이블 락(IX, IS)은 감지할 수 없음
- innodb_table_locks 시스템 변수를 활성화하여 테이블 락 상태를 확인할 수 있음
- 동시 처리 스레드가 많아지는 상황에서는 데드락 감지 스레드가 느려짐
  - 잠금 대기 목록에 잠금을 걸고, 그 위에 또 잠금을 거는 것이 반복되는 상황
  - innodb_deadlock_detect=OFF 및 innodb_lock_wait_timeout 시스템 변수를 설정하여 대응할 수 있음




































































