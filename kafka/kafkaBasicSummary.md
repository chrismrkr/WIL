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










