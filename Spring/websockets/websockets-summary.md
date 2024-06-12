# WebSocket

## STOMP(Simple Text Oriented Messaging Protocol)
Spring Framework Documentation을 참고하여 STOMP를 정리함

### 1. 개요
- STOMP는 메세지 브로커 연결을 위해 생성된 프로토콜이지만 TCP 연결 및 웹 소켓에서도 사용되는 프로토콜
- 메세지 브로커 연결 목적으로 생성되었으므로 PUBlish, SUBscribe 개념이 존재함
  - 사용자가 메세지를 Message Broker에 PUB하면, 다른 사용자는 Message Broker를 통해 SUB함
- STOMP를 사용하는 웹 소켓 서버는 실제로는 웹 소켓이지만 메세지 브로커와 동일한 인터페이스를 제공함
  - 웹 소켓 서버는 SUB 중인 클라이언트에 메세지를 라우팅함 : 웹 소켓 기능을 PUB/SUB 형태로 제공함
  - 웹 소켓 서버는 Kafka, RabbitMQ와 같은 메세지 브로커와 연결을 맺어 다른 웹 소켓 서버로 메세지를 전달함 : 다중 웹 소켓 서버를 구성할 수 있음
- 해당 프로토콜은 텍스트 또는 바이너리 메세지만을 다룸

### 2. 장점
- 웹 소켓 메세지를 위한 프로토콜을 새롭게 개발하지 않아도 됨
- @Controller로 등록된 모든 객체의 메소드에서 STOMP 기반 메세지 라우팅이 가능함 
- Kafka, RabbitMQ와 같은 메세지 브로커를 연동하여 사용할 수 있음
- Spring Security와의 연동이 가능함

### 3. STOMP 활성화 방법
- STOMP 활성화 방법에 대해 간단히 살펴봄
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/portfolio"); // ---- 1
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setApplicationDestinationPrefixes("/app"); // ---- 2
		config.enableSimpleBroker("/topic"); // ---- 3
	}
}
```

#### 1. Endpoint 설정
Websocket handshake가 필요한 클라이언트에 Http URL EndPoint를 /portfolio로 지정함
#### 2. Message Routing 설정
웹 소켓을 통한 메세지 라우팅을 위해 경로를 /app으로 설정함. 즉, 메세지 PUB을 위한 Path 설정
#### 3. SUB Broker 설정
라우팅된 메세지를 SUB할 수 있는 경로를 /topic로 지정함

클라이언트에서 사용 방법은 아래와 같음
```javascript
const connect = () => {
  client.current = new StompJs.Client({
      brokerURL: 'ws://localhost:8080/portfoilo',
      onConnect: () => {
                client.current.subscribe(`/topic`, (msg) => {
                    const resBody = JSON.parse(msg.body);
                    console.log(msg.body)
                    setMessages((prevMessages) => [...prevMessages, resBody]);
                })
            }
        });
  client.current.activate();
};
```

### 4. 메세지 흐름
- STOMP Endpoint가 노출되면 Spring App은 웹 소켓 연결된 클라이언트의 MessageBroker로 동작함
- STOMP 메세지 흐름을 파악하기 위해서는 아래 개념을 알아야 함
  - MessageHandler : 전달받은 메세지를 처리하는 객체 또는 메소드
  - ClientInboundChannel : 클라이언트가 PUB한 메세지를 MessageHandler 또는 MessageBroker로 전달하는 채널
  - ClientOutboundChannel : MessageBroker가 클라이언트에 메세지를 전달하는 채널
  - BrokerChannel : MessageHandler가 MessageBroker에게 메세지를 전달하는 채널 
- 아래 코드를 통해 STOMP 메세지의 기본적인 흐름에 대해 자세히 설명함
  
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/portfolio"); // ---- Step 1
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic"); // ---- Step 2
		registry.setApplicationDestinationPrefixes("/app"); // ---- Step 3
	}
}

@Controller
public class GreetingController {

	@MessageMapping("/greeting")
	public String handle(String greeting) {
		return "[" + getTimestamp() + ": " + greeting;
	}
}
```

#### Step 1
- 클라이언트는 localhost:8080/portfolio로 웹 소켓 연결함. 연결 완료되면 STOMP 메세지를 주고받을 수 있음

#### Step 2
- /topic/{...} Topic에 해당되는 메세지를 처리하는 MessageBroker를 생성함
  - 클라이언트가 /topic/greeting으로 메세지를 PUB하면, ClientInboundChannel을 통해 MessageBroker로 전달됨
  - MessageBroker는 /topic/greeting을 SUB 중인 클라이언트에게 ClientOutboundChannel을 통해 메세지를 전달함
    - (MessageBroker 또한 메세지를 처리하므로 MessageHandler로 분류되나 설명을 위해 구분하였음)
    
#### Step 3
- /app/으로 시작되는 요청을 처리하는 MessageHandler를 설정함
  - /app/topic/greeting 요청이 ClientInboundChannel을 통해 유입되면, 이를 MessageHandler에서 받음
    - MessageHandler는 @Controller 객체 내 @MessageMapping이 선언된 메소드를 의미함
  - MessageHandler는 BrokerChannel로 전달함
  - BrokerChannel은 /topic/greeting을 처리할 수 있는 MessageBroker에게 전달함
  - MessageBroker는 ClientOutboundChannel을 통해 메세지를 클라이언트에 전달함

#### /topic과 /app/topic의 차이
- /topic으로 메세지를 전송하면 클라이언트는 MessageBroker를 통해 직접 통신함
- /app/topic으로 메세제를 전송하면 @MessageMapping이 선언된 MessageHandler가 이를 받아서 BrokerChannel로 전달 및 처리하도록 위임함

#### BrokerChannel과 관련된 MessageHandler가 존재하는 이유는?
- MessageHandler가 BrokerChannel을 통해 메세지를 단순히 MessageBroker로 전달하는 역할만 하는 경우(기본 설정)
  - MessageHandler를 사용하지 않고 MessageBroker를 통한 직접 통신만을 해도 됨
- 그러나, 웹 소켓 컨테이너가 여러 대, 즉, MessageBroker가 여러개인 상황에서는 MessageHandler 역할이 변경되어야함
  - 단순히 MessageHandler가 BrokerChannel을 통해 MessageBroker로 메세지만 전달한다면, 현재 컨테이너가 아닌 다른 컨테이너에 연결된 클라이언트는 메세지를 받을 수 없음
  - **그러므로 MessageHandler가 다른 컨테이너로 메세지를 Broadcast할 수 있도록 커스터마이징이 필요하기 때문에 MessageHandler는 필요함**

### 5. 관련 Annotation






























