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
  - 물론, GC 대상이 아니므로 메모리 관리에 주의하지 않으면 누수가 발생할 수 있음
- ByteBuffer와 추가적인 개념을 통해 Blocking I/O의 단점을 해결하고자 나온 아키텍처가 NIO(Non-blocking I/O)임

## 2. Non-blocking I/O
### 2.1 비유
- Blocking 및 Non-blocking I/O 동작 방식을 가게 고객 응대 예시로 비유하면 아래와 같음
  - Blocking I/O : 고객이 주문하면, 1건 당 점원 1명이 맡아서 주문을 처리함. 점원은 처리가 끝날 때 까지 다음 고객을 받을 수 없음.
  - Non-Blocking I/O : 고객이 주문하면, 고객은 번호표를 받고 기다리고 점원은 주문을 처리함. 처리가 완료되면 번호표에 맞는 알림을 보냄.
### 2.2 Non-blocking I/O 관련 개념
- Socket Channel : 클라이언트 요청이 올 때 마다 생성됨. 클라이언트는 Channel을 통해 요청을 보내고 응답을 받음
- ByteBuffer : 클라이언트 요청 & 응답 관련 Stream을 임시로 저장해놓기 위한 버퍼(Kernel Buffer)
- Selector : Socket Channel 지속적 모니터링을 담당함. Socket Channel 상태에 따라 데이터를 처리함
- Thread : 클라이언트의 요청의 비즈니스적인 부분을 담당하여 처리하는 스레드
### 2.3 Non-blocking I/O 동작 방식
- 1. 클라이언트 요청으로 인한 SocketChannel 생성
```java
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
serverSocketChannel.bind(new InetSocketAddress(8080));
serverSocketChannel.configureBlocking(false);
// Socket Channel이 생성되면, Selector에 Socket Channel 상태를 OP_ACCEPT로 등록함
serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); 
```
- 2. Selector의 Channel 감지 후 처리
```java
while (true) {
    selector.select(); // 하나 이상의 채널이 준비될 때까지 대기
    Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 준비된 키들을 가져옴
    for (SelectionKey key : selectedKeys) {
        if (key.isAcceptable()) {
            // 연결 수락 수행 
        } else if (key.isReadable())) {
            // 읽기 작업 수행
        } else if (key.isWritable()) {
            // 쓰기 작업 수행
        }
    }
    selectedKeys.clear(); // 처리 완료된 키를 제거
}
```
- 이를 통해, 클라이언트 요청이 들어오더라도 비즈니스 로직을 처리하는 스레드가 커넥션 수 만큼 Block되지 않음
