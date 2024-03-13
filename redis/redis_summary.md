# Redis
## 1. Redis 주요 특징
- 싱글 스레드 기반
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
- 


