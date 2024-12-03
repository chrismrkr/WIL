# Redis
## 1. Redis 주요 특징
- 싱글 스레드
- RDB(Redis Database), AOF(Append only file)을 통한 영속성 제공
  - RDB : 스냅샷을 통해 백업 데이터를 저장함
  - AOF : Write 작업을 모두 로그에 저장함
  
## 2. Redis 실행 
### 2.1 in MacOS
- 실행 : ```brew services redis start```
- 중지 : ```brew services redis stop```
- CLI : ```redis-cli```
### 2.2 in Docker
- 실행 : ```docker run -d -p [foward_id]:6379 [image_name]```
- 중지 : ```docker stop [container]```
- CLI : ```redis-cli -p [forward-id]``` 

## 3. 데이터 타입
### 3.1 String
- 문자열, 숫자, JSON Object
```
SET [key] [value]
GET [key]

MSET [key1] [value1] [key2] [value2] ...
MGET [key1] [key2] ...

INCR [num-key]
INCRBY [num-key] [amount]
```
- 예시
  - ```SET '{"key1": "value1", "key2": "value2"}'```
  - ```SET redis:ko:amount 10```

 
### 3.2 List
- 스트링을 LinkedList 형태로 저장함
```
LPUSH queue job1 job2 job3 
RPOP queue

LPUSH stack job1 job2 job3
LPOP stack

LPUSH queue job1 job2 job3 (0(-3) # job3, 1(-2): job2, 2(-1): job3)
LRANGE queue -2 -1 # job1, job2를 반환
```
- LPUSH 및 LTRIM 명령어를 활용하여 고정된 크기의 LIST를 유지할 수 있음
- 인덱스나 중간 데이터를 통해 리스트에 접근하는 것은 O(n)으로 처리되므로 주의 필요


### 3.3 Hash
- key-value를 저장하는 자료구조
```
HSET [hash-name] [key1] [value1] [key2] [value2] ...

HGET [hash-name] [key1]
HMGET [hash-name] [key1] [key2] [key3] ...

HINCRBY [hash-name] [numeric-key1] [number]
```


### 3.4 Sets
- Unique Value를 저장하는 정렬되지 않은 자료구조
```
SADD myset:version:1 val1 val2 val3

SMEMBER myset:version:1 # value 조회
SCARD myset:version:1 # 개수 조회
SISMBMER myset:version:1 val2 # 존재 여부 확인
SREM myseet:version:1 val1 # 원소 삭제
SPOP myset:version:1 val1 # 원소 획득 후 삭제

SINTER myset:version:1 myset:version:2 # 교집합
SDIFF myset:version:1 myset:version:2 # 차집합
SUNION myset:version:1 myset:version2 # 합집합
```
- 활용 사례
  1. 좋아요 개수 집계
     - 게시글 ID를 Key로 하여 좋아요를 한 사용자를 원소로 Set에 저장

### 3.5 Sorted Sets
- score로 정렬 상태를 유지하는 Set 자료구조
- 같은 score 끼리는 사전 순서로 정렬됨
- O(n)으로 인덱스를 통해 원소에 접근할 수 있음
```
ZADD [sorted-sets-key] [score1] [val1] [score2] [val2]
ZRANGE [sorted-sets-key] 0 -1
ZREVRANGE [sorted-sets-key] 0 -1
ZRANGE [sorted-sets-key] 0 -1 REV WITHSCORES
ZINCRBY [sorted-sets-key] [INCR-AMOUNT] [val1] # [val1]의 score를 [INCR-AMOUT] 만큼 증가
ZRANK [sorted-sets-name] [val1] # val1의 순위 출력
```
- 활용 사례
  - 1. 실시간 리더보드
    - 원소는 score를 기준으로 정렬되므로 이 특성을 활용함
  - 2. 랭킹 합산
    - 일일 점수를 [scores]:[day]와 같은 Key를 가진 Sorted Sets에 저장할 수 있음
    - 해당 Sorted Sets을 ```ZUIONSTORE``` 명령어를 통해 집계할 수 있음
    - ```ZUIONSTORE scores:weekend 2 scores:sat scores:sun weights 1 2```와 같이 가중치를 사용할 수 있음
  - 3. 최근 검색 기록
    - 사용자 마다 1개의 Sorted Set을 갖고, 이 자료구조에 최근 검색 기록을 저장함
    - 최근 검색 시간을 Score로 하여 최근 검색된 것이 어떤 것인지 유지할 수 있음
    - 만약 검색 기록 5개만 유지가 필요하면, ```ZADD [key] [timestamp] [value]``` 및 ```ZREMRANGEBYRANK [key] -6 -6``` 명령어를 사용할 수 있음
  - 4. 태그 기능
    - 게시물 마다 1개의 Sorted Set을 갖고, 이 자료구조에 태그를 저장함
    - 태그 마다 1개의 Sorted Set을 갖고, 이 자료구조에 게시물을 저장함
    - Sorted Sets의 교집합 연산을 통해서 태그 기능을 구현할 수 있음


### 3.6 Streams
- 이벤트를 등록할 수 있는 append-only log 자료구조
- Redis를 메세지 브로커로 사용할 수 있게 만든 자료구조
- 특징
  - Redis Stream은 Kafka와 달리 Partition이 없음. 
```
XADD [event-name] [event-id(*)] action [event-description]
XRANGE [event-name] - +
XDEL [event-name] [event-id]
```
- PUB/SUB 기능과 관련하여 아래에서 자세히 설명할 예정


### 3.7 Geospatials
- 공간 좌표를 저장할 수 있는 자료구조
```
GEOADD seoul:station 126.7666456 37.5566245 hongdae 127.0275462 37.49794352 gangnam
GEODIST seoul:station hongdae gangnam [단위] # hongdae - gangnam 사이 거리 측정
```
- 활용 사례
  - 1. 특정 위치 근처에 있는 사용자에게 메세지 전송하기


### 3.8 Bitmaps
- String에 bit 연산이 수행 가능하도록 확장한 구조.
- String은 최대 512MB = 약 42억 비트 이므로 42억개 표현이 가능함
```
SETBIT user:login:24-03-13 [자리수] [bit]
SETBIT user:login:24-03-13 456 1 # 456번째 비트를 1로 설정
SETBIT user:login:24-03-13 789 1
SETBIT user:login:24-03-14 456 1

BITCOUNT user:login:24-03-13
BITOP AND result user:login:24-03-13 user:login:24-03-14
GETBIT result 123
```
- 활용 사례
  - 1. DAU 집계
    - BitMaps를 통해 512MB는 약 42억개의 비트로 되어 있으므로 42억명 사용자에 대한 DAU를 구할 수 있음


### 3.9 HyperLogLog
- 집합의 cardinality(개수)를 추정할 수 있는 확률형 자료구조
- 평균 1% 미만의 오차가 있지만 메모리를 절약하며 차수를 추정할 수 있음
```
PFADD fruits apple banana orange grape kiwi
PFCOUNT fruits
```
### 3.10 BloonFilter
- element가 집합에 포함되었는지 확인할 수 있는 자료구조
```
BF.MADD fruits apple orange
BF.EXISTS fruits apple # true
BF.EXISTS fruits banana # false
```

## 4. Redis 특수 명령어
### 4.1 데이터 만료
- 데이터가 특정 시간 이후 만료되도록 설정할 수 있음
```
SET key mykey
EXPIRE mykey 10 # 10초 뒤 mykey가 만료되도록 설정
TTL mykey # 남은 시간 확인
```
### 4.2 NX/XX
- NX : 해당 키가 존재하지 않은 경우에 SET
- XX : 해당 키가 이미 존재하는 경우에만 SET
```
SET already hello
SET already hello2 NX # 이미 존재하므로 SET되지 않음
SET already hello3 XX # 이미 존재하므로 SET됨
```
### 4.3 PUB/SUB
- Publisher와 Subscriber가 서로 알지 못하여도 통신이 가능하도록 decoupling된 자료구조
- Subscriber가 구독하기 전에 Publisher가 생성한 메세지는 수신할 수 없음
```
SUBSCIRE ch:order ch:payment
PUBLISH ch:order order1
PUBLISH ch:payment order2
```
### 4.4 Transaction
- 다수의 명령을 하나의 트랜잭션으로 처리하도록 함(Atomicity)
```
MULTI # 트랜잭션 시작
SET foo 1
INCR foo
DISCARD # 트랜잭션 롤백
EXEC # 트랜잭션 완료
```

## 5. Redis를 캐시로 사용하기
- Redis를 캐시로 사용했을 때의 장점
  - 가용성 : 센티널 또는 클러스터 기능을 사용하면 마스터 노드 장애를 자동으로 감지하여 페일오버 할 수 있음
    
### 5.1 캐싱 전략
- 읽기 전략
  - look aside : 원하는 데이터를 캐시에서 먼저 찾고, 존재하지 않으면(Cache Miss) 원본 데이터베이스에서 찾음
  - Cache Miss르 줄이기 위해 주기적으로 캐시에 데이터를 밀어 넣는 Cache Warming도 존재함
- 쓰기 전략
  - write through : 데이터베이스를 업데이트 하기 이전에 캐시를 항상 업데이트하는 전략
  - cache invalidation : 데이터베이스에 업데이트를 하면 해당 캐시를 삭제하는 전략
  - write back : 캐시에 먼저 업데이트 후, 일정 주기로 데이터베이스에 업데이트하는 전략
### 5.2 캐시에서의 데이터 흐름
캐시는 원본 데이터베이스의 Subset이므로 가득 차지 않도록 적절한 전략이 필요함
- TTL(만료시간)
  - 일정 시간이 지나면 데이터가 자동으로 만료되도록 함
    - passive 방식 : TTL 만료 즉시 데이터를 삭제하지 않고, 클라이언트가 요청하는 시점에 TTL을 체크하여 삭제하는 방식.
    - 그러므로, 실제 저장된 것 보다 메모리가 더 많이 점유될 수 있음
    - active 방식 : 주기적으로 TTL이 있는 데이터를 일부분 확인하여 만료되었는지 체크하여 삭제함
- 메모리 관리와 maxmemory-policy
  - 메모리 저장용량이 가득차면 내부 정책을 통해 어떤 키를 삭제할지 결정함
  - maxmemory : 최대 메모리 용량을 의미함
  - maxmemory-policy : 최대 메모리 초과 시 처리 방식
    - Noeviction 정책
      - 가득 차더라도 임의로 삭제하지 않고 더 이상 저장할 수 없다는 에러를 뱉음
    - LRU Eviction 정책
      - volatile-lru : TTL이 있는 키에 한하여 LRU 방식으로 삭제함
      - allkeys-lru : 모든 키에 대해 LRU 삭제
    - LFU Eviction 정책(Least-Frequently Use)
      - volatile-lfu
      - allkeys-lfu
    - Random Eviction 정책
      - volatile-random
      - allkeys-random
### 5.3 캐시 스탬피드 현상
- 여러 동시 요청이 발생했으나 캐시에 데이터가 없을 때, 데이터베이스 중복 읽기 및 캐시 중복 쓰기가 발생하는 현상
- 적절한 TTL 선택, 캐시 워밍, 뮤텍스 사용 등의 전략이 필요함
### 5.4 세션 스토어로의 Redis
- Redis를 서버 사이 공유 세션처럼 사용할 수 있음


## 6. Redis를 메세지 브로커로 사용하기
### 6.1 메세지 큐와 이벤트 스트림
- 메세지 큐 : 생산자는 소비자의 큐로 직접 전달하므로 2개의 서로 다른 서비스로 메세지를 보내기 위해서는 2개의 큐에 메세지를 전달해야함
  - 소비자가 메세지를 읽으면 큐에서 메세지를 삭제함
  - List를 메세지 큐로 사용할 수 있고 fire-and-forget으로 동작하므로 신뢰성이 필요한 경우에는 사용되지 않아야함
- 이벤트 스트림 : 생산자는 특정 저장소에 메세지를 쌓고, 서로 다른 소비자는 이를 Poll할 수 있음
  - 소비자가 메세지를 읽으면 offset을 통해 현재 어디까지 읽었는지 기록함
  - Stream 자료구조를 이벤트 스트림으로 사용할 수 있음
### 6.2 List를 메세지 큐로 활용하기
- LPUSH, RPUSH, LPOP, RPOP, RPUSHX 등의 명령어를 통해 큐로 활용할 수 있음
- 폴링 대신 BRPOP, BLPOP 명령어를 통해 Block 기능을 제공함
- ex. 소셜미디어 타임라인 기능에 활용될 수 있음
### 6.3 Stream을 이벤트 스트림으로 활용하기
- Stream은 데이터를 계속해서 추가하여 저장되는 append-only 자료구조임
- 레디스 Stream 자료구조는 하나의 Stream만 갖음(Kafka가 Topic내 여러 Partition을 갖는 것과는 다른 구조)
- Stream 내 저장된 메세지는 저장된 순서대로 <MilliSec>-<Seq>를 ID로 갖음
- Stream은 따로 생성하는 과정이 필요하지 않고 XADD 커맨드로 메세지를 Stream에 쌓을 수 있음
  - ```XADD Email * subject "first" body "hello"```
  - Email가 Key인 Stream에 *를 ID로 {subject: "first, body: "hello"} 데이터를 쌓음
  - *는 <MilliSec>-<Seq> 형태로 된 timestamp임
- 데이터 조회 방식
  - 실시간 데이터 리스닝
  - ID를 통해 필요한 데이터 검색
  - ex1. ```XREAD BLOCK 0 STREAMS EMAIL 0```
    - BLOCK 0 : 읽을 데이터가 없어도 계속 기다리고
    - STREAMS EMAIL 0 : Key가 EMAIL인 스트림의 ID가 0 이상인 메세지를 읽는다.(즉, 모두 읽는다)
  - ex2. ```XREAD BLOCK 0 STREAMS EMAIL $```
    - 위와 동일하나 커맨드 실행 이후 들어온 데이터만 읽음
  - ex3. ```XRANGE EMAIL - +```
    - EMAIL 스트림의 모든 테이터를 읽어옴
  - ex4. ```XRANGE EMAIL (11231-0 + ```
    - ID가 11231-0을 초과한 데이터를 읽어옴
- 소비자와 소비자 그룹
  - 서로 다른 소비자는 동일 Stream 내 메세지를 동시에 받아갈 수 있음(Fan-out)
  - 동일 Stream 내 메세지들 소비자 끼리 나눠서 가져가는 것이 필요하다면, 해당 소비자들을 소비자 그룹으로 묶으면 됨
    - Redis의 소비자 그룹과 Kafka의 소비자 그룹은 서로 다르게 동작함
    - Redis 소비자 그룹 : 동일한 Stream 내 메세지를 소비자 그룹에서 나눠서 가져감. 읽지 않은 데이터를 나눠서 읽어감
    - Kafka 소비자 그룹 : 소비자 그룹 내 참여자 끼리는 동일한 파티션을 구독할 수 없음
  - ```XGROUP CREATE [Stream-key] [Group-Name] $``` : [Stream-key] Stream을 읽는 [Group-Name] 소비자 그룹을 생성하고 현재($) 연결된 시점 이후 부터만 읽어감
  - ```XREADGROUP GROUP [Group-Name] [Consumer-Name] COUNT [N] Streams [Stream-key] >```
- Ack와 보류 리스트
  - 소비자에게 메세지를 전달한 후, Stream에서 소비자가 전달한 Ack를 받을 때 까지 해당 메세지를 보류 리스트에 저장함
  - Stream이 Ack를 받으면 보류 리스트에서 삭제함
  - Stream은 메세지를 전달하고 last_delivered_id를 통해 Stream의 어느 메세지까지 전송했는지 저장함
  - 장애로 인해 시스템이 종료되었을 때, Ack, 보류 리스트, last_delivered_id를 통해 어느 시점부터 다시 전송할지를 알 수 있음
  - ```XACK [Stream-Key] [Group-Name] [Message-ID]``` : 소비자 그룹 중 한명이 해당 메세지를 받았다고 Ack를 보냄
  - ```XPENDIG [Stream-key] [Group-Name]``` : 보류 리스트를 확인함
  - 특정 소비자가 장애가 발생한다면, 그룹 내 다른 소비자가 처리할 수 있도록 


## 7. 데이터 백업
### 7.1 레디스 데이터 영구 저장 방법
- 레디스는 데이터를 메모리에 저장하지만 AOF 및 RDB를 통해 영구적으로 저장할 수 있음
- AOF와 RDB를 동시에 사용할 것을 권장함
### 7.2 RDB 데이터 백업
- 메모리 자체를 스냅숏 찍듯 백업하여 디스크에 저장함
- 특정 조건에 자동으로 RDB 파일 생성할 수 있음
  - ```save <기간(초)> <기간 내 변경된 키의 개수>```
  - ```dbfilename <RDB 파일명>```
  - ```dir <RDB 파일이 저장되 경로>```
- redis.conf에 save 설정 가능함
- 레디스를 실행 중인 상태에서 변경하려면 redis-cli ```CONFIG SET SAVE ... ```으로 설정을 변경하고, CONFIGE REWRITE가 필요함
- RDB 백업은 자식 프로세스가 백그라운드에서 진행함
### 7.3 AOF 데이터 백업
- AOF는 Redis 커맨드가 파일 뒤쪽에 계속 추가되는 방식임
- 그러므로, 점점 커지는 파일을 압축하기 위해 재구성이 필요함
### 7.4 AOF 파일 재구성 방법
- 버전 7 이전의 AOF
  - 레디스 메모리를 백그라운드 프로세스를 통해 RDB 파일로 복제함
  - 백그라운드 프로세스 중 실행된 커맨드는 버퍼에 쌓음
  - RDB 파일이 완성되면 버퍼에 쌓인 커맨드를 추가하여 덮어씀
- 버전 7 이후의 AOF
  - manifest 파일에 AOF 파일 정보를 저장함(rdb, aof 위치 및 파일 명)
  - AOF 재구성 실행 시, 백그라운드 프로세스에서 새로운 rdb 파일을 생성함
  - 백그라운드 실행 중 입력되는 커맨드는 aof 파일에 저장함
  - 백그라운드 프로세스가 완료되면, 새로 생성된 rdb를 기존 것과 대체함
### 7.5 자동 AOF 재구성
- aof_base_size : AOF 파일 재구성 기준
- auto_aof_rewrite_percentage : aof_base_size에 비해 aof_current_size가 (100+auto_aof_rewrite_percentage)%이 되는 시점에 자동으로 재구성함
### 7.6 수동 AOF 재구성
- BGREWRTIEAOF 커맨드를 통해 직접 수동으로 AOF 파일 재구성이 가능함
### 7.7 AOF 파일 안정성
- 일반적으로 어플리케이션에서 디스크에 Write 작업을 할 때, OS Buffer에 저장하고 여유가 되는 시점에 디스크에 이를 반영함
- FSYNC란 OS 버퍼에 저장된 내용을 디스크에 쓰도록 강제하는 명령어임
- AOF 파일도 FSYNC와 관련한 옵션이 있음
- APPENDFSYNC
  - no : OS Buffer에만 Write하고 FSYNC를 강제하지 않음
  - always : 항상 Write와 FSYNC를 실행함
  - everysec : 1초에 1번 FSYNC를 호출함
 ### 7.8 백업 시 주의사항
 - 백업은 백그라운드 프로세스를 통해 레디스 메모리 스냅샷을 임시 파일로 저장하고, 완료된 이후에 덮어쓰는 방식이였음
 - 그러므로, 기존 메모리 용량의 최대 2배까지 차지할 수 있음

## 8. 복제
### 8.1 복제 구조
- Redis는 멀티 마스터 구조를 지원하지 않고, 하나의 인스턴스는 마스터 노드 이거나 복제 노드임
- 인스턴스 B(192.168.0.3)가 인스턴스 A(192.168.0.2)의 복제 노드가 되도록 만드는 명령어는 아래와 같음
  - ```REPLICAOF 192.168.0.2 6379```
- 복제본 노드의 masterpass 옵션에 마스터 노드의 requirepass에 설정된 패스워드 값 입력이 필요함
### 8.2 복제 메커니즘
- 복제 메커니즘 자체는 자동으로 이루어지며 사용자 개입이 필요하지 않음
- 디스크를 사용하는 방식(repli-diskless-sync = no)
  - ```REPLICAOF <master-ip> <port>``` 명령어로 복제 시작
  - 마스터 노드의 데이터를 백그라운드에서 RDB 파일로 복제
  - 백그라운드 실행 중 마스터 노드에 발생한 커맨드는 임시 버퍼에 저장
  - RDB 파일 복제가 완료되면, 이를 복제 노드에 전달하고 임시 버퍼에 저장된 내용을 반영함
- 디스크를 사용하지 않는 방식(repli-disless-sync = yes)
  - ```REPLICAOF <master-ip> <port>``` 명령어로 복제 시작
  - 마스터 노드는 복제 노드와 소켓으로 연결하여 데이터 셋을 전달함
  - 복제 노드는 데이터 셋을 전달 받아 이를 디스크 내에 저장함
  - 데이터 셋 전달 중 마스터 노드에 발생한 커맨드는 임시 버퍼에 저장
  - 데이터 셋 저장이 완료되면, 복제 노드는 이를 메모리에 로드하고 임시 버퍼에 저장된 커맨드를 동기화함
  - 소켓 연결 완료 상태인 마스터 노드는 다른 복제 노드의 연결 요청을 받을 수 없으므로 repli-diskless-sync-delay 옵션으로 기간 내 또 다른 복제 연결을 받을 수 있음
- 복제 ID 및 부분 동기화
  - ```INFO Replication``` 명령어를 통해 레디스 인스턴스의 복제 상태를 확인할 수 있음
  - 예를 들어, master의 offset이 810이고 replica의 offset이 802인 상황에서 연결이 끊길 수 있음
  - 복제 노드는 마스터 노드의 백로그 버퍼와 offset 값을 활용하여 부분 동기화를 할 수 있음
```
# Replication
role:master
connted_slave:0
slave0:ip=...,port=..., ...
...
master_replid:abcabcabc123123abcabcabc
master_repli_offset:810

```

```
# Replication
role:slave
master_replid:abcabcabc123123abcabcabc
master_repli_offset:802
...
```
- Secondary 복제 ID
  - 마스터 노드의 장애 상황에서 빠르게 복제 노드가 마스터로 승격되어 동기화되기 위해 사용됨
  - 마스터 노드(A)는 2개 이상의 복제 노드(B, C)를 갖을 수 있음
  - 만약 마스터 노드(A)에 장애가 발생하면, 페일 오버를 통해 복제본 노드(B)가 마스터로 승격될 수 있음
  - 이때, master_replid는 복제본 노드(B)의 노드 ID로 변경되고, 기존 마스터 노드(A)의 ID는 master_replid2에 저장됨
  - 복제 노드(C)는 새로운 마스터 노드(B)에 부분 재동기화를 시도할 수 있음. 왜냐하면, master_replid2가 동일하므로 동일한 데이터 셋이기 때문임
 
- Read-Only 복제본 노드
  - Redis 2.6 버전 이후에 복제 노드는 기본적으로 Read-only로 동작함
  - 테스트 목적으로 read-only 옵션을 종료하더라도 복제 본 노드의 변경사항은 다른 노드로 전파되지 않음
  - 연결 실패 등의 이유로 부분 재동기화가 발생하면 복제본 노드의 변경사항은 제거됨
  - 복제 노드가 마스터 노드가 정확하지 일치하지 않는 상황일 때, replica-serve-stale-data=no 옵션을 통해 READ 요청을 거절할 수 있음
    - replica-serve-stale-data의 기본 값은 yes임

- 백업을 사용하지 않는 경우에서의 데이터 복제
  - 백업 기능 사용하지 않으면, 재부팅 후 레디스의 자동 재시작 옵션을 사용하지 않는 것을 권장함
  - 왜냐하면, 재시작할 때 마스터 노드는 복원된 내용을 복제 노드에 전달하는데 백업 파일이 없다면 빈 값을 전달함
 
## 9. 센티널
별다른 장치 없이 마스터-복제 노드를 사용하면, 마스터 노드 장애 시 수동으로 복제 노드를 마스터로 승격시켜야 한다는 단점이 있음
### 9.1 센티널이란?
- 레디스 HA(High Availability)를 위해 사용하는 기술로 자동 페일오버 기능, 실시간 모니터링, 인스턴스 정보를 제공함
- 분산 시스템으로 동작하는 센티널
  - 센티널이 그 자체로 SPOF가 되는 것을 방지하기 위해 최소 3대 이상일 때 정상적으로 동작하도록 설계됨
  - 레디스 인스턴스는 센티널에 먼저 연결해 마스터 정보를 받아옴
  - 쿼럼(Quorum)은 마스터 노드가 비정상적으로 동작한다는 것에 동의해야 하는 센티널의 수로 센티널의 과반수 이상으로 설정함
  - 예를 들어, 센티널 인스턴스가 3개라면 쿼럼은 2로 설정함
- 센티널 인스턴스 배치 방법
  - 센티널 인스턴스는 물리적으로 서로 영향 받지 않는 서버에 실행하는 것을 권장함
  - 그러므로, 물리 서버 3대 이상을 필요로 함
  - 예를 들어, 물리 서버 3대에 각각 레디스 인스턴스와 센티널 인스턴스를 1개씩 갖고 있을 때,
  - 마스터 노드(A)에서 장애가 발생하면, 페일오버를 통해 복제 노드(B)가 마스터 노드로 승격되고 레디스로 새롭게 들어오는 커넥션은 모두 새로운 마스터 노드(B)와 연결됨
  - A 노드가 복구되면, 마스터가 된 B 노드의 복제본으로 연결됨
 
### 9.2 센티널 인스턴스 실행
- 센티널 프로세스 실행
  - 레디스 인스턴스와 센티널 인스턴스의 포트는 각각 6379, 26379임
  - Step1. 마스터 노드 및 복제 노드 연결
    - ```REPLICA OF <master-ip> <port>``` 명령어로 마스터-복제 노드 간 연결
  - Step2. 센티널 프로세스 실행
    - sentinel.conf 파일에 아래의 내용을 추가할 것
    - ```port 26379```
    - ```sentinel monitor <master-node-name> <master-node-ip> <port> <quorum>```
    - 복제본 정보는 입력하지 않아도 됨
    - 그리고, 아래 커맨드를 실행함
    - ```redis-sentinel /path/to/sentinel.conf``` or ```redis-server /path/to/sentinel.conf --sentinel```
    - 모든 센티널에 커맨드를 실행해야함. 커맨드 실행이 다른 센티널 인스턴스로 전파되지 않음
  - Step3. 센티널 인스턴스 접속
    - ```redis-cli -p 26379```
    - ```SENTINEL master <master-node-name>```
    - 하나의 센티널 인스턴스에서 모든 레디스 및 센티널 인스턴스 정보를 확인할 수 있음
  - 추가적인 커맨드
    - ```SENTINEL replicas <master-node>``` : 복제 노드 정보 출력
    - ```SENTINEL sentinels <master-node>``` : 복제 노드 정보 출력
    - ```SENTINEL ckquorum <master-node>``` : 쿼럼이 센티널 개수의 과반 이상인지 체크함
- 페일 오버 테스트
  - 수동 페일 오버 : ```SENTINEL FAILOVER <master-node-name>``` 명령어로 마스터-복제 노드 롤 체인지
  - 자동 페일 오버
    - ```redis-cli -h <master-node-host> -p <port> shutdown``` 명령어로 레디스 마스터 인스턴스 강제 종료
    - 센티널은 마스터 노드와 주기적으로 헬스체크를 하며 sentinel.conf에 지정된 after-milliseconds 내에 헬스 체크에 실패하면 자동 페일 오버를 시작함
   
### 9.3 센티널 운영하기
- 패스워드 인증
  - 레디스 인스턴스 마스터-복제 노드 사이에 requirepass/masterauth 패스워드가 설정된 경우, 센티널도 패스워드 설정이 필요함
  - sentinel.conf 파일 내 ```sentinel auth-pass <master-node-name> <password>``` 설정 필요
- 복제본 우선 순위
  - 모든 레디스 인스턴스는 replica-priority 파라미터를 갖고, 해당 값이 가장 작은 값이 페일 오버 시 마스터 노드로 승격됨
  - 단, 0인 경우에는 마스터로 선출되지 않음
- 운영 중 센티널 구성 정보 변경
  - 운영 중 센티널이 모니터링할 마스터의 추가, 제거, 변경 가능
  - **모든 센티널에 개별적으로 적용해야함**
  - ```SENTINEL MONITOR <master-node-name> <ip> <port> <quorum>``` : 새로운 마스터 노드를 모니터링 하도록 변경
  - ```SENTINEL REMOVE <master-node-name>``` : 마스터 노드를 모니터링 하지 않도록 변경
  - ```SENTINEL SET <master-node-name> [<option> <value>]``` : 마스터 노드의 특정 파라미터 변경
  - ```SENTINEL CONFIG SET <configuration-name> <value>``` : 센티널 고유 설정 변경
- 센티널 초기화
  - 센티널은 비정상적이라고 판단된 노드 모니터링을 멈추지 않음
  - 예를 들어, 페일 오버 후 기존 장애 발생한 마스터 노드나 장애 발생한 복제 노드도 모니터링함
  - 그러므로, ```SENTINEL RESET <master-node>``` 명령어로 직접 초기화 해야함
- 센티널 노드 추가 및 제거
  - 마스터 노드를 모니터링 하도록 센티널을 추가로 실행하면, 자동 검색 메커니즘에 의해 자동으로 센티널 known-list에 추가됨
- 센티널의 자동 페일오버 동작 방식
  - Step1. 레디스 마스터 인스턴스의 장애 상황 감지
    - 센티널의 down-after-milliseconds 파라미터에 지정된 값 이상 동안 헬스 체크를 실패하면 장애 상황으로 인식
  - Step2. sdown 플래깅
    - 센티널은 마스터 노드의 상태를 sdown(subjectly down)으로 플래깅함
    - 다른 센티널 노드들에 ```SENTINEL is-master-down-by-addr <master-ip> <port> <current-epoch> <*>``` 커맨드로 장애 사실 전파
  - Step3. odown 플래깅
    - 자신을 포함한 다른 센티널의 장애 인지가 **쿼럼 값 이상**이면 odown(objectly down)으로 플래깅함
    - 복제 노드의 장애의 경우에도 Step1-2 과정을 거치나 odown 플래깅을 하지는 않음
  - Step4. Epoch(에포크) 증가
    - 처음으로 마스터 노드 odown을 인지한 센티널이 페일 오버 시작 전에 Epoch 값을 1 증가 시킴
    - 페일 오버가 발생할 때 마다 에포크가 1씩 증가하고, 동일한 에포크 값을 통해 센티널은 동일한 페일 오버를 진행하고 있다는 것을 알 수 있음
  - Step5. 센티널 리더 선출
    - 에포크를 최초로 증가시킨 센티널은 다른 센티널 노드에 센티널 리더를 선출하기 위해 투표하라는 메세지를 전달함
    - 메세지를 전달받은 센티널은 자신의 에포크를 증가시킨 후, 센티널 리더에 투표하겠다고 응답함
    - 이 시점에 에포크가 모든 센티널 사이에 동기화됨
  - Step6. 마스터 노드 재선출
    - 과반 수 이상의 센티널이 페일 오버에 동의하면, 마스터 노드 재선출이 시작됨
    - replica-priority > master_repli_offset > runID 사전 순서로 마스터 노드가 선택됨
  - 모든 과정은 sentinel.log에 기록이 남음
 
## 10. 클러스터 
### 10.1 레디스 클러스터와 확장성
- 레디스는 싱글 스레드로 동작하므로 스케일 업으로 성능을 높이기 어려울 수 있으므로 Scale Out을 권장함
- 레디스 클러스터 기능
  - 데이터 샤딩

