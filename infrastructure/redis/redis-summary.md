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
- Redis 서버 사이 공유 세션처럼 사용할 수 있음
- 캐시란 모든 사용

## 6. Redis를 메세지 브로커로 사용하기


