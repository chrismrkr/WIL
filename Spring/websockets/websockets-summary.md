# WebSocket

## STOMP(Simple Text Oriented Messaging Protocol)
Spring Framework Documentation을 참고하여 STOMP를 정리함

### 개요
- STOMP는 메세지 브로커 연결을 위해 생성된 프로토콜로, TCP 연결 및 웹 소켓에서도 사용됨
- 메세지 브로커 연결 목적으로 생성되었으므로 PUBlish, SUBscribe 개념이 존재함
  - 사용자가 메세지를 Message Broker에 PUB하면, 다른 사용자는 Message Broker를 통해 SUB함
- STOMP를 사용하는 웹 소켓 서버는 실제로는 웹 소켓이지만 메세지 브로커와 동일한 인터페이스를 제공함
  - 웹 소켓 서버는 SUB 중인 클라이언트에 메세지를 라우팅함 : 웹 소켓 기능을 PUB/SUB 형태로 제공함
  - 웹 소켓 서버는 Kafka, RabbitMQ와 같은 메세지 브로커와 연결을 맺어 다른 웹 소켓 서버로 메세지를 전달함 : 다중 웹 소켓 서버를 구성할 수 있음
- 해당 프로토콜은 텍스트 또는 바이너리 메세지만을 다룸

## 장점
- 웹 소켓 메세지를 위한 프로토콜을 새롭게 개발하지 않아도 됨
- @Controller로 등록된 모든 객체의 메소드에서 STOMP 기반 메세지 라우팅이 가능함 











