# jMeter

애플리케이션의 성능 및 부하 테스트를 위해 사용되는 오픈 소스 자바 애플리케이션이다.

## 1. 주요 개념

+ Thread Group : 테스트 그룹. 사용자당 1개의 thread가 할당된다.
+ Sampler : thread group이 실행해야할 action
+ Listener : 테스트 결과를 받는 역할(ex. 리포팅, 검증, 그래프)
+ Configuration : Sampler 또는 Listener가 사용할 값을 설정
+ Assertion : 응답이 성공적인지 확인하는 역할

## 2. 설치방법

https://jmeter.apache.org/download_jmeter.cgi에서 zip파일 다운로드하고 압축 해제한다.

그리고, ./bin/jmeter 커맨드로 실행 가능하다.


## 3. 사용 방법

### 3.1 Thread Group 생성

스레드 개수, 스레드 그룹을 만드는데 소요하는 시간, 그리고 loop count를 설정하여 thread group을 생성한다.

### 3.2 sampler 생성

thread group이 실행할 action을 정의한다.(ex. GET /members)

### 3.3 Listener 생성

thread의 결과를 처리할 Listener를 생성한다.(ex. view Results Tree, Summary Report 등)

### 3.4 Assertions 생성

테스트 성공 여부를 커스터마이징 하기 위해 생성한다.

## 4. 주의 사항

+ 1. 테스트하고자 하는 서버와 부하테스트용 서버 jmeter 서버를 분리한다.
+ 2. GUI는 테스트 생성용으로 사용하고, CLI에서 테스트를 실행할 것을 권장한다.
+ 3. jmeter -n -t [파일경로.jmx] CLI로 실행 가능
