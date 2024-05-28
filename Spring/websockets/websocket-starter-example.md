# WebSocket Example
Spring WebSocket과 STOMP 프로토콜을 활용한 간단한 대화방 구현 예시 및 방법

- https://spring.io/guides/gs/messaging-stomp-websocket를 참고하여 작성함

## 개요
- STOMP : PUB/SUB 패턴으로 메세지를 주고 받기 위한 프로토콜(텍스트, 바이너리 지원)
- Java Spring 및 STOMP를 통해 대화방 기능을 제공하는 백엔드 및 프론트엔드를 개발함
- Spring Web, Websocket 의존성 필요

## Backend

### DTO 생성
```java
// 클라이언트로부터 Subscriber에게 전달하기 위한 메세지 DTO 생성
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AcceptMessage {
    private String name;
}
```

```java
// Subscriber에게 PUB하기 위한 메세지 DTO 생성
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PublishMessage {
    private String content;
}
```
### 컨트롤러 생성
```java
@Controller
public class WebSocketController {
    @MessageMapping("/greetings") // ws://domain/{setApplicationDestinationPrefixes}/greetings 로 오는 요청을 처리하여
    @SendTo("/topic/greetings") // /topic/greetings 메세지 브로커로 PUBLISH
    public PublishMessage greeting(AcceptMessage message) throws Exception {
        Thread.sleep(500); // simulated delay
        return new PublishMessage(HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
```

### WebSocket 설정

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // /topic을 이름으로 한 topic 생성
        config.setApplicationDestinationPrefixes("/app"); // Websocket Destination Prefix 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket"); // Websocket 연결 Endpoint 지정
    }
}
```

***

## 프론트엔드

```html
<!DOCTYPE html>
<html>
<head>
    <title>Hello WebSocket</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link href="main.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7.0.0/bundles/stomp.umd.min.js"></script>
    <script src="app.js"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">WebSocket connection:</label>
                    <button id="connect" class="btn btn-default" type="submit">Connect</button>
                    <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                    </button>
                </div>
            </form>
        </div>
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="name">Send Message To ChatRoom</label>
                    <input type="text" id="name" class="form-control" placeholder="Your name here...">
                </div>
                <button id="send" class="btn btn-default" type="submit">Send</button>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                <tr>
                    <th>Greetings</th>
                </tr>
                </thead>
                <tbody id="greetings">
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
```

```js
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/gs-guide-websocket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/greetings', (greeting) => {
        showGreeting(JSON.parse(greeting.body).content);
        console.log(JSON.parse(greeting.body).content);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    console.log("connected")
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/greetings",
        body: JSON.stringify({'name': $("#name").val()})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});
```



