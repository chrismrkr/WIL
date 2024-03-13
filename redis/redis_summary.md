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
### 3.1 스트링
- 문자열, 숫자, JSON Object
```
# SET [key] [value]
# GET [key]

# MSET [key1] [value1] [key2] [value2] ...
# MGET [key1] [key2] ...

# INCR [num-key]
# INCRBY [num-key] [amount]
```
- 예시
  - ```SET '{"key1": "value1", "key2": "value2"}'```
  - ```SET redis:ko:amount 10```
### 3.2 리스트
- 
