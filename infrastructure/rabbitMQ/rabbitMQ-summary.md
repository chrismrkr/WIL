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
  - channel을 통해 confirm.select, confirm.select-ok를 통해 confirm mode가 활성화됨
- 동작 방식
  - ack: broker가 메세지를 수신하여 Queue와 Disk에 저장하면, publisher에게 Ack를 전송함
    - durable mode가 동작하지 않으면 Queue에 저장되었을 때만 Ack를 전송함
    - 수신 받은 메세지에 대한 Ack를 여러개 묶어서 한번에 보내는 옵션도 존재함
  - nack: broker가 메세지를 정상적으로 처리할 수 없는 상황임을 알림
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

## Clustering
### 1. 주요 개념
- Identifier : RabbitMQ 노드마다 하나씩 부여되고, Identifier를 통해 식별하여 클러스터링할 수 있음
  - 환경변수 ```RABBITMQ_NODENAME```를 통해서 Identifier를 설정할 수 있음
  - [prefix]@[hostname] 형태로 되어있고, hostname을 찾기 위한 DNS 또는 host 파일 설정이 필요함
- Port Access : 아래 포트가 허용되어 있어야함
  - 4369, 6000-6500, 25672, 35672-35682
### 2. 클러스터 내 노드 특징
- 복제 대상
  - RabbitMQ를 실행하는데 필요한 모든 데이터/상태는 노드들끼리 복제하여 사용함
  - 메세지 큐는 특정 노드에만 존재함
  - 단, Queue Mirroring 기법 등을 통해서 노드마다 개별적으로 메세지 큐를 갖도록 할 수 있음
- Equal Peers
  - 노드들은 Leader, Follower가 존재하지 않고 모두 동일하므로 어느 노드에서 조회하더라도 클러스터 정보를 조회할 수 있음
  - 클러스터 내 노드끼리는 동일한 Erlang Cookie를 공유하여 통신할 수 있음
    - cookie는 로컬에 파일로 존재하며 600 이상의 권한 필요
    - /var/lib/rabbitmq/.erlang.cookie(서버), $HOME/.erlang.cookie(CLI tool)
- 클러스터 노드 개수는 홀수를 권장함. 왜냐하면, 장애상황으로 노드가 정상적으로 클러스터링되지 않을 때도 quorum을 형성하기 위함임
### 3. 클러스터링 방법
- 
