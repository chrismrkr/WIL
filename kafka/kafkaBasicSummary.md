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


