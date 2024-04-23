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
#### 3.2.5 자동화된 장애 복구 지원
- 데이터 파일은 일반적으로 깨지지 않으므로 MySQL 시작 시 완료되지 않은 작업을 자동으로 반영함
- 그러나, InnoDB 스토리지 엔진이 자동 복구를 하지 못하는 경우, innodb_force_recovery 시스템 변수를 사용해야함
#### 3.2.6 InnoDB 버퍼 풀
- DML에 의해 발생하는 디스크 I/O 작업 횟수를 줄일 수 있음
- 버퍼 풀 크기 설정
  - 서버 전체 물리 메모리의 약 50% 정도로 설정하고, 상황을 모니터링 하며 늘리는 것이 좋음
  - innodb_buffer_pool_size 시스템 변수로 버퍼 풀 메모리 전체 크기 설정 가능
  - innodb_buffer_pool_instances 시스템 변수로 버퍼 풀 인스턴스 개수 설정 가능
- 버퍼 풀 구조
  - Free : 아직 사용되지 않은 버퍼 풀 Page
  - LRU 리스트 : 디스크 읽기(OUT)가 있는 페이지를 저장하고, 자주 읽힐수록 앞쪽에 위치하고 일정 수준 뒤로 물러나면 제거됨
  - Flush 리스트 : 더티 페이지(디스크 IN)를 관리하고 특정 시점에 더티 페이지를 디스크에 반영함
    - 더티 페이지 발생 시, 이를 Redo Log에 반영함(항상 동기화되지 않고 스토리지 엔진 체크포인트에 동기화됨)
- 버퍼 풀의 장점
  - 캐시 기능 : 버퍼 풀을 통해 캐시를 지원하므로 읽기 성능을 높임(디스크 OUT)
  - 쓰기 버퍼링 : 체크 포인트의 LSN보다 작은 Redo Log와 관련된 더티 페이지를 디스크에 동기화함
    - Redo Log는 데이터의 변경 분만 갖고 있으므로 버퍼 풀과 크기가 동일해야 하는 것은 아님
- 버퍼 풀 플러시
  - 버퍼 풀을 디스크에 동기화하는 것을 의미하고 아래 2가지가 백그라운드 스레드에서 실행됨
  - Flush 리스트 플러시
    - 오래된 리두 공간을 지우고 더티 페이지를 디스크에 동기화함
  - LRU 리스트 플러시
    - 최근에 사용되지 않은 버퍼 풀을 제거함. 캐시가 꽉 찼을 때 발생함
- 버퍼 풀 관련 InnoDB 시스템 변수 설정
  - innodb_page_cleaners : Flush 리스트 플러시를 수행하는 스레드 수. 일반적으로 innodb_buffer_pool_instances와 동일하게 함
  - innodb_max_dirty_pages_pct : 더티 페이지 차지 공간/버퍼 풀 전체 메모리 비율(기본 값 0.9)
  - innodb_io_capacity : 일반적인 상황에서의 초당 디스크 I/O 작업 수(더티 페이지 동기화와 관련 있으나 Disk Read도 고려해야 함)
  - innodb_io_capacity_max : 최대로 더티 페이지를 디스크에 쓸 수 있는 양
    - innodb_io_capacity와 max는 디스크 장비가 처리할 수 있는 수준으로 설정할 것
  - innodb_max_dirt_pages_pct_lwm : 일정 수준 이상의 더티 페이지가 쌓이면 조금씩 더티 페이지를 디스크에 기록함(기본 값 0.1)
  - innodb_adaptive_flushing : innodb_io_capacity, max를 따르지 않고 Redo Log 생성 속도에 따라 디스크 I/O 수를 결정하는 새로운 알고리즘에 따르는 설정
- 버퍼 풀 상태 백업 및 복구
  - 버퍼 풀에 캐시되어 있으면 시작 직후 보다 성능이 좋음(Warm Up 상태)
  - innodb_buffer_pool_dump_now=ON : 버퍼 풀 LRU 리스트 상태 백업
  - innodb_buffer_pool_load_now=ON : 백업된 것 복구
  - ```SHOW STATUS LIKE 'innodb_buffer_pool_dump_status';``` 명령어로 복구 진행 상황 확인 가능
  - ```SET GLOBAL innodb_buffer_pool_load_abort_ON;``` : 버퍼 풀 복구를 중지하고 즉시 실행
  - innodb_buffer_pool_dump_at_shutdown, innodb_buffer_pool_load_at_startup : 서버 시작 및 종료 시 자동으로 백업, 복구 되도록 설정
- 버퍼 풀 적재 내용 확인
  - information_schema.innodb_cached_indexes, innodb_tables, innodb_indexes 테이블을 적절히 조인하여 현재 차지하고 있는 버퍼 풀 데이터 페이지 수를 확인할 수 있음
#### 3.2.7 Double Write Buffer
- 더피 페이지 및 Redo 로그를 디스크에 동기화할 때, 하드웨어 오동작 등으로 비정상 종료될 수 있음
- 이를 방지하기 위해 Double Write Buffer에 더티 페이지를 먼저 저장함
- innodb_doublewrite로 제어 가능
#### 3.2.8 Undo Log
- 트랜잭션 격리 수준에 따라 DML 작업으로 데이터가 변경되기 전에 이전 버전을 백업하는 파일
- 레코드마다 존재함
#### 3.2.9 Redo Log 및 로그 버퍼
- 

































































































