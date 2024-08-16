# Kafka

개발 환경: JDK 11, Confluent Kafka 7.1.2

## 1. kafka 설치 및 환경설정

### 1.1. 설치 및 환경변수 설정

```shell
wget [kafka 다운로드 링크]
mv [다운로드 링크] [원하는 이름.tar.gz]
tar -cvf [원하는 이름.tar.gz] confluent 
```

```shell
vi ./bashrc

export CONFLUENT_HOME=/home/ubuntu/confluent
export PATH=.:$PATH:$CONFLUENT_HOME/bin
```


### 1.2. kafka 실행

```shell
$CONFLUENT_HOME/bin/zookeeper-server-start $CONFLUENT_HOME/etc/kafka/zookeeper.properties
$CONFLUENT_HOME/bin/kafka-server-start $CONFLUENT_HOME/etc/kafka/server.properties
```

위의 실행 명령어를 아래와 같이 shell로 관리할 수 있다.

```shell
vi zookeeper_start.sh
chmod -x zookeeper_start.sh


vi kafka.sh
chmod -x kafka_start.sh
```

### 1.3. 환경설정 

로그파일 저장 위치를 변경하는 방법은 아래와 같다.

```shell
vi zookeeper.properties
```

```shell
dataDir=[로그파일 저장 경로]
```

kafka 로그파일 저장 경로도 동일한 방법으로 변경할 수 있다.

## 2. Kafka Topic, Producer, Consumer 이해 및 Command Line Interface 실습

### 2.1 Topic, Producer, Consumer 개요

#### Producer

메세지를 생성하는 주체. 메세지를 생성하여 어느 Broker의 Partition으로 전달할지를 결정한다.

producer 생성을 위한 Command Line Interface는 아래와 같다.

```shell
kafka-console-producer --bootstrap-server localhost:9092 --topic test_topic_01
```

#### Consumer

메세지를 읽는 주체. 어떤 Broker의 Partition에서 메세지를 읽을지를 결정한다. 

Consumer 생성을 위한 Command Line Interface는 아래와 같다.

```shell
kafka-console-consumer --bootstrap-server localhost:9092 --topic test_topic_01 [--from-beginning]
```

consumer는 마지막 offset부터 읽는 것을 기본 정책으로 한다. 만약, 첫 offset부터 읽는 것으로 정책을 변경하려면 ```--from-beginning``` 옵션이 필요하다.


#### Topic

메세지를 저장하는 공간. offset(시간 흐름)을 기준으로 순차적으로 저장된다.

Topic은 여러개의 Partition을 가질 수 있으며 Partition들은 서로 독립적이다.

메세지가 Broker(Kafka Server)에 도착하면, 어떤 Topic과 Partition에 저장할 것인지를 결정한다.(병렬성 보장)

Topic은 Leader Partition과 replica Partition을 갖고, replica Partition을 통해 다른 Broker Server 시스템이 다운되더라도 가용성을 지킬 수 있다.

Topic 생성, 조회, 삭제 Command Line Interface는 아래와 같다.

```shell
kafka-topics --bootstrap-server localhost:9092 --create --topic test_topic_01 [--partition N]
kafka-topics --bootstrap-server localhost:9092 --list
kafka-topics --bootstrap-server localhost:9092 --describe --topic test_topic_01
kafka-topics --bootstrap-server locahlost:9092 --delete --topic test_topic_01
```

Topic 생성시 만들어지는 Partition 개수의 기본 값은 server.properties에서 변경할 수 있다.

### 2.2 Topic, Producer, Topic 예제

#### key 값이 존재하는 메세지를 여러 Partition을 갖는 Topic에 Produce하고 consume하기

```shell
# 3개의 partition을 갖는 Topic 생성
kafka-topics --bootstrap-server localhost:9092 --create --topic multi-partition-topic --partitions 3
```
```shell
# key가 존재하는 메세지를 생성하는 producer 실행
kafka-console-producer --bootstrap-server localhost:9092 --topic multi-partition-topic \
--property key.seperator=: --property parse.key=true
```
```shell
# 위에서 생성된 Topic의 메세지를 읽는 consumer 실행
kafka-console-consumer --bootstrap-server localhost:9092 --topic multipart-topic \
--property print.partition=true --from-beginning
```
**결과**: 동일한 key를 갖는 메세지는 동일한 partition으로 push된다. 이에 따라, consumer가 메세지를 읽을 때 순서를 지킬 수 있다.

#### key 값을 가지지 않는 메세지를 여러 Partition을 갖는 Topic에 Produce하고 consume하기

```shell
# Producer 생성
kafka-console-producer --bootstrap-server localhost:9092 --topic multi-partition-topic
```
```shell
# Consumer 생성
kafka-console-consumer --bootstrap-server localhost:9092 --topic multipart-partition-topic
```
**결과**: partition batch가 다 차거나 일정 시간이 지나면 partition에 저장함. round-robin, 또는 stick-partition 전략으로 배분함.

**round-robin**: batch에 순서대로 message를 채우는 방식
**sticky-partition**: 특정 batch가 다 찰 때까지 message를 채우는 방식(batch.size, linger.ms 사용). kafka 2.4 이상 버전에서 채택한 방법.


### 2.3 Consumer-group 

consumer-group이란 1개 이상의 consumer로 구성된 집합이다.

동일 consumer-group 내에서 consumer들은 서로 독립적인 partition을 할당받는다. 동일 그룹 내의 consumer들은 동일한 partition을 할당받을 수 없다.

반대로 서로 다른 consumer-group의 consumer는 동일한 partition을 할당받을 수 있다.

이상적인 consumer-group에서는 partition-consumer가 일대일로 할당된다. 그러나, consumer가 더 적은 경우에는 rebalancing된다.

#### consumer-group 생성
consumer 생성시 group을 지정하면 자동으로 consumer-group이 생성된다.

```shell
kafka-console-consumer --bootstrap-server localhost:9092 --group [그룹명] --topic [토픽명]
```

#### consumer-group과 consumer, 그리고 partition, Lag 관련 정보 조회하기
```shell
kafka-console.consumer --bootstrap-server localhost:9092 --list
kafka-console-consumer --bootstrap-server localhost:9092 --describe --group [그룹명]
```

#### consumer-group 삭제하기

consumer-group 내 consumer가 모두 삭제되더라도 일정 기간동안 group은 존재한다. 강제로 group을 삭제하는 방법은 아래와 같다.
```shell
# consumer-group 내 모든 consumer는 종료되어야 한다.
kafka-console-consumer --bootstrap-server localhost:9092 --delete --group [그룹명]
```

## 3. Producer

### 3.1 Producer 실행 및 Message 생성 흐름
- Step1. Producer는 properties 파일에서 환경변수를 설정함
  - 예를 들어, Spring boot Server가 Producer이면 application.properties를 통해 설정함
- Step2. KafkaProducer 객체 생성
- Step3. 토픽명, 메세지 Key, 메세지 Value를 입력하여 ProducerRecord 객체 생성
  - ```Future<RecordMetaData>```를 사용함
- Step4. KafkaProducer의 send() 메소드로 메세지 전송을 시작
  - Broker로 메세지 전송 시 sync, async 전송을 모두 지원하나 기본적으로 async
  - Kafka Producer 내부의 전송용 Thread가 배치 단위로 메세지를 Broker에 전송함
- KafkaProducer의 close() 메소드로 종료

### 3.2 Producer Details
#### 3.2.1 Ack
- ack = 0
  - Producer는 Broker의 메세지 정상 수신을 확인하지 않고 계속 전송함
  - 그러므로, 수신 실패로 인한 재전송이 없음
- ack = 1
  - Producer는 Leader Broker의 정상 수신 여부를 확인함
  - 그러므로, Follower에 정상적으로 복제되었는지 알 수 없음
- ack = all
  - Producer는 Leader 및 Follower Broker의 정상 수신 여부를 모두 확인함
  - 그러므로, 상대적으로 전송속도가 느릴 수 있음

#### 3.2.2 Sync, Async
- Sync
  - Kafka Producer 스레드가 Broker로 메세지를 전송 후, 응답이 올 때 까지 Block
  - Ack = 0 인 경우에는 대기하는 것 없음
  - ```RecordMetaData recordMetaData = KafkaProducer.send().get()```
- Async
  - Kafka Producer 스레드가 Broker로 메세지 전송 후 Block 하지 않고 계속 진행
  - 수신 응답이 오면 Callback을 통해 새로운 스레드 실행
  - ```producer.send(productRecord, new Callback() { ... })```
 
#### 3.2.3 ProducerRecord 구조
- 아래와 같음
- Mssage = Record = Event
```java
public class ProducerRecord<K, V> {
  private final String topic;
  private final Integer partition;
  private final Headers headers;
  private final K key;
  private final V values;
  private final Long timeStamp;
}
```

#### 3.2.4 Record Accumulator
- Producer가 메세지를 전송하면, Serializer와 Partitioner를 거쳐서 Record Accumulator에 저장함
- Record Accumulator는 파티션 - 토픽 2가지 기준으로 분류하여 Record를 저장함
- Record는 Batch 단위로 저장되어 있음
- Batch는 특정 조건에 의해 Broker로 전송됨
- 관련 파라미터
  - buffer.memory : Record Accumulator에 저장될 수 있는 레코드들의 최대 메모리
  - linger.ms : sender thread가 broker로 배치 단위로 메세지 전송을 위해 대기하는 최대 시간
  - batch.size : Batch 당 저장할 수 있는 레코드 수를 결정. 만약, linger.ms 전에 배치가 꽉 차면 전송함

#### 3.2.5 기타 중요 파라미터
- max.block.ms : producer가 send 후 Ack를 받을 때 까지 대기하는 최대 시간. 초과 시 Timeout Exception
- request.timeout.ms : broker로 전송하는 thread가 메세지를 전송하고 대기하는 최대 시간
  - 그러므로, 적어도 ```max.block.ms >= ligner.ms + request.timeout.ms```를 만족해야함
- delivery.timeout.ms : broker로 전송하는 thread의 메세지 재전송을 멈추는 시간
- retry.backoff.ms : 재전송 주기
- 위 파라미터를 해석하면 아래와 같음
  - producer가 send하면 배치 단위로 thread가 broker로 메세지를 전달함
  - producer는 max.block.ms를 대기하고, thread는 request.timeout.ms를 대기함
  - request.timeout.ms가 지난 후에도 Ack가 도착하지 않으면 retry.backoff.ms 주기로 재전송함
  - 만약 delivery.timeout.ms에 도달하거나, max.block.ms에 도달하면 재전송을 중지함
- max.in.flight.requests.per.connection : 한번에 보낼 수 있는 메세지 배치 개수
  - 만약 해당 값이 2 이상이면, 메세지 도착 순서가 달라질 수 있음
- enable.idempotence=true : PID, SEQ를 통해 Broker에서 메세지 중복 및 순서를 제어할 수 있음

#### 3.2.6 Custom Partitioner
- Producer.send() 시, serializer -> partitioner -> Record Accumulator -> Broker 순서로 전달됨
- 아래 인터페이스를 구현하여 Partitioner를 직접 구현할 수 있음
```java
public interface Partitioner extends Configurable, Closeable {
  int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster);
  ...
}
```

## 4. Consumer
### 4.1 Consumer 생성 및 Message Consume & Poll 흐름
- Step1. Consumer 환경 설정(Properties 객체 이용)
- Step2. KafkaConsumer 생성
- Step3. Topic subscribe()
- Step4. 주기적으로 Topic poll()
  
### 4.2 Consumer Details
#### 4.2.1 poll
- 브로커나 Consumer 내부 Queue에 데이터가 있으면 데이터를 반환함
- 만약 데이터가 없으면, 일정 시간 동안 브로커로부터 데이터를 Fetch함
```java
ConsumerRecords<K, V> consumerRecords = KafkaConsumer.poll(Duration.ofMillis(1000)); // 1000ms 동안 poll
for(ConsumerRecord consumerRecord : consumerRecords) {
  String key = consumerRecord.key();
  String value = consumerRecord.value();
}
```
#### 4.2.2 Fetcher, ConsumerNetworkClient
- ConsumerNetworkClient는 비동기 I/O로 브로커로부터 데이터를 주기적으로 가져와서 Queue에 쌓음
- Fetcher는 Queue에서 데이터를 poll하고, 데이터가 없으면 ConsumerNetworkClient에 데이터를 가져오도록 요청함
- Fetcher 관련 파라미터
  - fetch.min.bytes : 최신 offset부터 데이터를 읽는 경우, fetcher가 파티션 별 데이터를 읽어들이는 최소 byte
  - fetch.max.wait.ms : fetch.min.bytes 만큼 데이터가 쌓이지 않았으면 해당 파라미터만큼 대기
  - max.partition.fetch.bytes : 오래된 offset부터 데이터를 읽는 경우, 해당 파라미터만큼 파티션에서 읽은 후 대기 
  - max.poll.records : fetcher가 한번에 가져올 수 있는 최대 레코드 수
  - auto.offset.reset : consumer group이 topic partition에 대한 offset를 갖고 있지 않으면, 새로운 consumer가 partition의 첫 offset부터 데이터를 가져올지 아니면 마지막 offset부터 데이터를 가져올지를 결정함
    - auto.offset.reset = earliest (첫 offset부터 READ)
    - auto.offset.reset = latest (마지막 offset부터 READ)

#### 4.2.3 Group Coordinator
- counsumer group에 어떤 consumer가 존재하는지 확인하고, consumer가 생성되고 소멸 시 발생하는 Rebalancing을 담당함
  - Rebalance : healthCheck 실패, consumer 종료, consumer 조인, partition 추가 이벤트가 발생할 때, partition을 어느 consumer에 할당할지 결정하는 작업
- Static Group Membership : Rebalance로 인한 오버헤드를 줄이기 위해서 등장함
  - session.timeout.ms 이내에 다시 연결되면 rebalance가 발생하지 않음
  - max.poll.interval.ms 이내에 poll() 성공에 실패하면 rebalance가 발생함
- partition.assignment.strategy : 해당 파라미터를 통해 파티션 할당 전략을 결정함
  - round-robin, sticky는 Eager Protocol이므로 consume lag가 발생할 수 있음

#### 4.2.4 subscribe, poll, commit
- 


