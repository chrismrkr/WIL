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

신뢰성 있는 데이터 전달을 위해 Publisher는 broker에 정상적으로 전달이 되었는지 확인해야 함

또한, Broker는 Consumer가 메세지를 정상적으로 수신했는지 확인할 수 있는 메커니즘 필요

참고: https://www.rabbitmq.com/docs/confirms

### 1. Consumer Acknowledgments
- Ack를 통해 Broker에게 메세지 수신 여부를 전달함
- fire-and-shot: Ack를 사용하지 않음
- Positive Ack: Subscriber는 메세지 수신 시, 브로커에 Ack를 보냄
  - 메세지를 수신받을 때 마다 Ack를 보내는 방식과 Batch 단위로 Ack를 보내는 방식 모두 존재함
- Negative Ack: Subscriber가 해당 메세지를 수신하지 않겠다고 브로커에게 알리는 Ack
  - 현재 Subscriber가 메세지를 처리하기 어려운 상황에 사용함
  - Broker가 메세지를 다른 큐로 재전송하거나 버릴 수 있음
- 주의사항: 네트워크 지연 등으로 Broker가 Ack를 받지 못하여 재전송을 했을 때, 멱등성을 위해 Subscriber는 중복된 메세지를 처리하는 프로세스를 자체적으로 구현해야함
  - 그렇지 않은 경우에는 동일한 메세지를 중복하여 수신할 수 있음


