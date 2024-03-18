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
### 3.3 Sets
- Unique Value를 저장하는 정렬되지 않은 자료구조
```
SADD myset:version:1 val1 val2 val3

SMEMBER myset:version:1 # value 조회
SCARD myset:version:1 # 개수 조회
SISMBMER myset:version:1 val2 # 존재 여부 확인

SINTER myset:version:1 myset:version:2 # 교집합
SDIFF myset:version:1 myset:version:2 # 차집합
SUNION myset:version:1 myset:version2 # 합집합
```
### 3.4 Hashes
- key-value를 저장하는 자료구조
```
HSET [hash-name] [key1] [value1] [key2] [value2] ...

HGET [hash-name] [key1]
HMGET [hash-name] [key1] [key2] [key3] ...

HINCRBY [hash-name] [numeric-key1] [number]
```
### 3.5 Sorted Sets
- score로 정렬 상태를 유지하는 Set 자료구조
```
ZADD [sorted-sets-name] [score1] [val1] [score2] [val2]
ZRANGE [sorted-sets-name] 0 -1
ZRANGE [sorted-sets-name] 0 -1 REV WITHSCORES

ZRANK [sorted-sets-name] [val1] # val1의 순위 출력
```
### 3.6 Streams
- 이벤트를 등록할 수 있는 append-only log 자료구조
```
XADD [event-name] [event-id(*)] action [event-description]
XRANGE [event-name] - +
XDEL [event-name] [event-id]
```
### 3.7 Geospatials
- 공간 좌표를 저장할 수 있는 자료구조
```
GEOADD seoul:station 126.7666456 37.5566245 hongdae 127.0275462 37.49794352 gangnam
GEODIST seoul:station hongdae gangnam [단위] # hongdae - gangnam 사이 거리 측정
```
### 3.8 Bitmaps
- String에 binary operation을 적용하여 최대 42억개 binary 데이터 표현
```
SETBIT user:login:24-03-13 [key] [bit]
SETBIT user:login:24-03-13 456 1
SETBIT user:login:24-03-13 789 1
SETBIT user:login:24-03-14 456 1

BITCOUNT user:login:24-03-13
BITOP AND result user:login:24-03-13 user:login:24-03-14
GETBIT result 123
```
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

## 5. Redis 데이터 타입 활용 예제
- 분산 락
- 중복 요청 방지
- 로그인 상태 헬스 체크

## 6. 주의사항
### 6.1 O(N) 명령어
- KEYS
- SMEMBERS
- HGETALL
- SORT
### 6.2 Thundering Herd Problem
- 레디스 캐시가 만료되어 Cache Miss가 발생해 DB와 서버에 부하가 걸리는 문제
### 6.3 Stale Cache Invalidation
- 레디스 캐시와 실제 리포지토리 데이터가 서로 동기화 되지 않은 문제

