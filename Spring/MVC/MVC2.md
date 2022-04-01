# 스프링 MVC 2

## 1. 메세지, 국제화

화면에 보이는 여러가지 뷰 속에는 공통으로 포함된 메세지가 있을 수 있다. 만약, 공통으로 포함된 메세지를 모두 바꾸고자 한다면 시간낭비가 클 수 있다.

공통으로 포함된 메세지를 한 곳에서 관리하는 기능을 **메세지** 기능이라고 한다.

메세지 기능은 MessageSource 인터페이스를 통해 사용하지만, 스프링 부트에서는 자동으로 사용할 수 있다.

메세지 기능을 사용하는 방법은 아래와 같다.

+ application.properties에 메세지 소스를 설정한다.

```java
  spring.messages.basename=messages
```

messages는 디폴트로 설정되어 있으므로 추가로 MessageSource를 사용하지 않으면 application.properties에 설정하지 않아도 된다.



