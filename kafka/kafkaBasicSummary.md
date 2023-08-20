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

Command Line Interface는 아래와 같다.

```shell
kafka-console-producer --bootstrap-server localhost:9092 --topic test_topic_01
```

#### Consumer

메세지를 읽는 주체. 어떤 Broker의 Partition에서 메세지를 읽을지를 결정한다. 

Command Line Interface는 아래와 같다.

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

