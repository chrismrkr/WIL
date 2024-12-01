# RabbitMQ

## 주요 개념 및 동작 원리
- Producer: 메세지를 생성하여 Exchange에 전달
- Exchange: 정해진 Rule에 의해 특정 Queue에 메세지 전달
- Consumer: Queue에 쌓인 메세지를 수신함

## 주요 패턴
### 1. Work Queue
- Queue에 쌓인 메세지를 클라이언트가 consume하되, 하나의 메세지는 정확히 하나의 클라이언트에게만 전달됨
- 시간이 오래 걸리는 작업을 Queue에 쌓고, 클라이언트들 끼리 나누어 받아서 처리하는 형태의 Task에 사용됨
- Default Exchange를 사용하여 Queue에 메세지를 쌓을 수 있음

### 2. Fan-out
- Queue에 쌓인 메세지를 클라이언트가 consume하고, Queue를 구독 중인 모든 클라이언트에 전달됨
- FanoutExchange에 Queue를 Binding한 후, Producer가 FanoutExchange에 메세지를 전달함
- FanoutExchange에 등록된 Queue를 구독 중인 모든 클라이언트가 메세지를 수신함
- https://www.rabbitmq.com/tutorials/tutorial-three-spring-amqp

### 3. Routing
- Exchange에 등록된 Queue에 메세지를 전달하되, 메세지 Routing key과 Queue의 Binding key가 일치하는 쪽으로만 전달함
- 동일한 binding key를 가진 여러 Queue를 Exhange에 등록할 수 있음
- DirectExchange를 사용함
- https://www.rabbitmq.com/tutorials/tutorial-four-spring-amqp

### 4. Topic Exchange
- Exchange에 등록된 Queue로 메세지를 전달하되, 메세지 Routing key가 Queue의 Binding pattern과 일치하는 쪽으로만 전달함
- 예를 들어서, Queue의 Binding pattern이 *.pattern.*이라면, Routing key가 my.pattern.1, you.pattern.2 등인 메세지를 Queue로 전달함
- 패턴에 #(단어 0개 또는 그 이상), *(정확히 1개 단어) 특수문자 사용 가능
- https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp

### 5. RPC
- https://www.rabbitmq.com/tutorials/tutorial-six-spring-amqp

## Consumer Acknowledgments & Publisher Confirms

신뢰성 있는 데이터 전달을 위해 producer가 보낸 메세지를 broker가 전달받았는지, 또는 consumer가 broker가 보낸 메세지를 정상적으로 받았는지 확인할 수 있는 방법을 제공함

참고: https://www.rabbitmq.com/docs/confirms

### 1. Consumer Acknowledgments
- Consumer가 Ack를 통해 Broker에게 메세지 수신 여부를 알림
- Ack
  - Automatic Ack: Broker가 메세지를 전송한 후, Ack를 기다리지 않음(fire-and-forget)
  - Mannual Ack: Broker가 메세지를 전송한 후, Ack를 기다림. Positive & Negative Ack로 구분됨
- Positive Ack: Consumer는 메세지 수신 성공 시, 브로커에 Ack를 보냄
  - One Message at once: Consumer가 하나의 메세지를 수신받을 때 마다 Ack를 보내는 방식
  - Multiple Message at once: Consumer가 여러 개의 메세지를 수신받은 후, 마지막에 전송한 메세지에 대해서만 Ack를 보내는 방식
- Negative Ack: Consumer가 해당 메세지를 수신하지 않겠다고 브로커에게 알리는 Ack
  - Work Queue 패턴에서 사용되며 현재 Consumer가 메세지를 처리하기 어려운 상황에 사용함
  - Broker는 메세지를 큐에 다시 쌓아 다른 Consumer가 메세지를 받도록 하거나 버릴 수 있음
- Channel Prefetch Setting(QoS): Consumer가 Ack를 보내기 전까지 받을 수 있는 최대 메세지 수 지정
  - multiple Message at one 방식으로 메세지를 보내는 상황에서 Ack를 보내지 못하면, buffer가 가득 찰 수 있음
  - 예를 들어, 메세지를 최대 3개까지 한번에 보낼 수 있고 Qos가 3이라고 하면, Ack를 받기 전까지 메세지를 더 전송하지 않음
  - Qos는 채널 또는 Consumer에 설정됨
  - Qos 설정이 없을 때, Ack가 지연될수록 메모리 사용량이 증가하므로 주의해야함
- Requeuing Policy: Ack를 받지 못한 메세지가 전달된 Channel(Connection)이 닫히면, Broker는 자동으로 메세지를 재전송함
  - 이에 따라, 동일한 메세지가 중복해서 Consumer에 전달될 수 있으므로 멱등성(Idempotency)을 위한 메커니즘을 자체적으로 구현해야함

### 2. Publisher Confirms
- publisher가 전달한 메세지가 broker에 정상적으로 전달되었는지 Ack를 통해 확인하는 메커니즘을 publisher confirms라 함
- confirm mode: publisher confirms을 사용하는 기능
  - channel을 통해 Publiser의 confirm.select, 그리고 Broker의 confirm.select-ok를 통해 confirm mode가 활성화됨
- 동작 방식
  - basic.ack: broker가 메세지를 수신하여 Queue와 Disk에 저장하면, publisher에게 Ack를 전송함
    - basic.ack.delivery-tag 필드에 현재 Ack된 메세지의 Seq가 기록됨
    - basic.ack.multiple 필드를 true로 활성화하면 특정 시퀀스까지의 메세지가 모두 Ack되었음을 나타냄
    - durable mode가 동작하지 않으면 Queue에 저장되었을 때만 Ack를 전송함
    - 수신 받은 메세지에 대한 Ack를 여러개 묶어서 한번에 보내는 옵션도 존재함
  - nack: broker가 메세지를 정상적으로 처리하지 못함을 알림
    - 모든 큐로 전달하지 못하면 nack르 반환함. 즉, 하나라도 실패하면 nack를 반환함
    - 이에 대한 재전송 정책이 필요함
- 디스크 저장 방식
  - broker는 메세지를 일정 주기로 디스크에 저장함
  - 그러므로, 성능을 높이기 위해 배치 단위로 메세지를 주고 받거나 publisher는 Ack를 비동기적으로 처리해야함
- 메세지 순서
  - broker는 publisher가 전송한 메세지를 순서대로 받음
  - 그러나, Ack는 비동기적으로 전달되므로 publisher의 Ack 수신은 순서대로가 아님에 주의해야함
- 디스크 기록 실패
  - publisher가 메세지를 보낸 후 Ack를 받지 못하면, broker가 Disk Write에 실패했을 수도 있음
  - 만약 Disk Write에 실패했다면, broker는 재시작됨
  - 이에 따라, consumer는 메세지를 받을 수 없음

## RabbitMQ Cluster
### 1. Preview
![rabbitmq-cluster drawio](https://github.com/user-attachments/assets/7c7880e7-e4fd-4cfb-b8d5-44dc14d6e3ed)
- RabbitMQ Cluster는 위와 같은 형태로 클러스터링됨
- 설명
  - RabbitMQ 노드에 Exchange와 Queue를 등록함
  - 해당 정보들을 메타데이터라고 하며, 메타데이터는 노드 내 MnesiaDB(Khepri)에 저장됨
  - 메타 데이터의 변동사항은 25672 포트를 통해 클러스터 내 다른 노드들로 전달되어 MnesiaDB를 통기화함
  - Producer가 RabbitMQ Cluster 중 하나의 Node로 메세지를 PUB함(ex. /message.1)
  - Node는 메세지를 받아 25672 포트를 통해 다른 노드로 전달함
  - Exchange에 등록된 바인딩 키를 통해 메세지의 라우팅 키와 매치되는 Queue에 메세지를 전달함
 
### 2. Metadata 저장소
- 사용자, 권한, virtual hosts, topology, exchange, queue, binding 등을 메타데이터라고 함
- Metadata는 MnesiaDB 또는 Khepri에 저장됨
- MnesiaDB
  - RabbitMQ 3.13.x 이하 버전에서 사용됨
  - 트랜잭션을 통해 메타데이터를 수정함
  - 단점 : 네트워크 파티션 등의 장애 상황에서 MnesiaDB는 단절된 노드들에 대한 메타데이터를 모두 삭제함
- Khepri
  - RabbitMQ 4.0.x 이상 버전에서 사용됨

- 참고사항: Metadatadml Exchange는 아래와 같이 트리 구조로 저장됨


![rabbitmq-metadata drawio](https://github.com/user-attachments/assets/4469bcdc-0b3a-4a90-a58d-667b460a7221)


### 3. 클러스터링 가이드
#### 3.1 클러스터 생성
- 1. 클러스터 생성 방법
  - Config 파일을 통해 노드 간 연결 가능
  - DNS-based Discovery 가능
- 2. Node Names
  - Node Name을 클러스터 내 노드 사이 Identifier로 사용되고, RABBITMQ_NODENAME 환경변수에 저장됨
#### 3.2 클러스터 형성 필요 조건
- 1. Host Resolution
  - /etc/hosts 파일 또는 DNS 설정 필요
- 2. Port Access
  - 클라이언트 연결, CLI, 노드 간 연결 등을 위한 포트 할당 및 방화벽 해제 필요
#### 3.3 클러스터 내 노드의 특징
- 1. 복제되는 것
  - Queue를 제외한 모든 메타 데이터가 노드 사이에 동기화됨
- 2. Equal Nodes
  - 노드 사이에 master - replica 개념이 없음. 모든 노드는 다른 노드에 명령어를 보낼 수 있음
- 3. 노드 간 인증 방법
  - 클러스터 내 노드 끼리는 Erlang cookie를 공유하여 인증함
  - 인증이 완료된 노드 끼리만 클러스터를 형성할 수 있음
  - Erlang cookie는 /var/lib/rabbitmq/.erlang.cookie, $HOME/.erlang.cookie에 저장
  - 전자는 server, 후자는 cli에 사용됨
- 4. Node 개수 및 Quorum Queue
  - 클러스터 내 노드 개수는 홀수로 유지하는게 좋음
  - 왜냐하면 네트워크 파티션 상황 시, 노드 개수가 짝수라면 다수가 어디인지 알 수 없는 상황이 발생할 수 있기 때문임
  - Quorum Queue
    - 고가용성(HA) Queue이다. Leader-follower가 있는 Queue로 Leader가 존재하는 노드에서 장애가 발생하면, Leader를 재선출함
    - Leader Queue와 Follower Queue는 서로 다른 노드에 존재함
    - Classic Queue는 Follower가 존재하지 않음
- 5. Clustering and Clients
  - Queue Clients
    - 클러스터 내 노드들은 서로 메세지를 라우팅하므로 클라이언트는 어느 노드와 연결되어도 상관 없음
    - 그러므로, 클라이언트는 연결 가는ㅇ한 노드들을 리스트로 등록할 수 있음
  - Stream Clients
    - Queue Publishing과 동일하게 Stream Client는 어느 노드에 연결되어 Pub해도 상관없음
    - 그러나, SUB은 리더 또는 팔로워 Stream에 직접 연결되어야 하므로 토폴로지를 알아야 함
  - Queue and Stream Leaer Replica Placement
    - 특정 노드로 Queue가 몰리지 않도록 노드에 적절히 분배될 수 있어야함
    - queue_leader_locator 설정
      - client-local : 클라이언트가 직접 노드를 선택
      - balanced : 노드에 현재 생성된 큐 개수를 고려하여 연결






