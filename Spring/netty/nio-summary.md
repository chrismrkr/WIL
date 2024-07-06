# NIO(Non-blocking I/O)
Non-Blocking I/O 동작 방식에 대해서 설명함

## 1. Blocking I/O
### 1.1 Blocking I/O 동작 방식
- 클라이언트 요청마다 스레드가 할당되고, 응답이 올 때 까지 Block함
### 1.2 Blocking I/O 단점
- 스레드가 모두 점유되어 있으면, 다른 클라이언트는 계속 기다려야함
- 서드 파티(ex. DB)의 응답 지연이 발생하여 스레드가 계속 점유된다면, 자원이 효율적으로 사용되지 않을 수 있음
- 스레드가 많은 상태에서 GC가 발생하면, Stop the World 및 CPU 점유율 상승으로 프로세스가 강제로 종료되어 장애가 발생할 수 있음
### 1.3 Java I/O가 느린 이유
- 소켓, 파일 등과 같은 Stream이 들어오면, OS는 커널 버퍼를 통해 받을 수 있음
- JDK 1.4 미만에서는 커널 버퍼에 코드를 통해 직접 접근할 수 없고, 아래 과정으로 읽을 수 있음
  - JVM이 OS kernel에 read() 명령을 보내어 디스크나 소켓으로부터 Stream을 읽음
  - OS는 kernel buffer에 데이터를 복사함
  - JVM은 kernel buffer를 내부 버퍼로 복사하여 사용할 수 있음
- 이에 따라, Java I/O는 느리고 내부 버퍼도 GC 대상이므로 성능 저하로 이어질 수 있음
### 1.4 Java I/O 지연 해결 방법
- JDK 1.4 이상에서 ByteBuffer 클래스를 통해 커널 버퍼에 직접 접근할 수 있게 되었음
- ByteBuffer와 추가적인 개념을 통해 Blocking I/O의 단점을 해결하고자 나온 아키텍처가 NIO(Non-blocking I/O)임

## 2. Non-blocking I/O
### 2.1 비유
- Blocking 및 Non-blocking I/O 동작 방식을 가게 고객 응대 예시로 비유하면 아래와 같음
  - Blocking I/O : 고객이 주문하면, 점주는 주문을 처리하고 고객은 주문이 완료되어 상품이 나올 때 까지 점주를 기다리고 있어야 함
  - Non-Blocking I/O : 고객이 주문하면, 점주는 주문을 처리하고 고객은 점주를 기다리지 않고 본인 할 일을 하다가 완료되었다는 알림 이벤트를 받으면 그때 상품을 받아감
### 2.2 Non-blocking I/O 관련 개념
- Socket Channel : 클라이언트 요청이 올 때 마다 생성됨. 클라이언트는 Channel을 통해 요청을 보내고 응답을 받음
- ByteBuffer : 
