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


## 7. 데이터 백업 방법
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



















