# 스프링 MVC 2

## 1. 메세지, 국제화

### 1.1 메세지

화면에 보이는 여러가지 뷰 속에는 공통으로 포함된 메세지가 있을 수 있다. 만약, 공통으로 포함된 메세지를 모두 바꾸고자 한다면 시간낭비가 클 수 있다.

공통으로 포함된 메세지를 한 곳에서 관리하는 기능을 **메세지** 기능이라고 한다.

메세지 기능은 MessageSource 인터페이스를 통해 사용하지만, 스프링 부트에서는 자동으로 사용할 수 있다.

메세지 기능을 사용하는 방법은 아래와 같다.

+ application.properties에 메세지 소스를 설정한다.

```java
  spring.messages.basename=messages
```

messages는 디폴트로 설정되어 있으므로 추가로 MessageSource를 사용하지 않으면 application.properties에 설정하지 않아도 된다.

+ messages 파일을 생성한다

```java
# messages.properties

label.item=상품
label.item.id=상품 ID
label.item.itemName=상품명
label.item.price=가격
label.item.quantity=수량

page.items=상품 목록
page.item=상품 상세
page.addItem=상품 등록
page.updateItem=상품 수정

button.save=저장
button.cancel=취소
```

국제화를 위해서 아래의 코드를 추가할 수 있다.

```java
# messages_en.properties

label.item=Item
label.item.id=Item ID
label.item.itemName=Item Name
label.item.price=price
label.item.quantity=quantity

page.items=Item List
page.item=Item Detail
page.addItem=Item Add
page.updateItem=Item Update

button.save=Save
button.cancel=Cancel
```

+ th:text="#{label.item.price}" 방법으로 원래 메세지를 치환한다.

```HTML
<form action="item.html" th:action th:object="${item}" method="post">
        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>
            <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price" th:text="#{label.item.price}">가격</label>
            <input type="text" id="price" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity" th:text="#{label.item.quantity}">수량</label>
            <input type="text" id="quantity" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
        </div>

        <hr class="my-4">

        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit" th:text="#{button.save}">상품 등록</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/message/items}'|"
                        type="button" th:text="#{button.cancel}">취소</button>
            </div>
        </div>

    </form>
```

### 1.2 국제화

messages.properties 대신에 messages_en.properties 추가함으로써 자동으로 국제화 기능을 추가할 수 있다.

자동으로 추가되는 이유는 스프링 부트는 AcceptHeaderLocaleResolver를 사용하기 때문이다.

LocaleResolver는 Locale을 선택하는 인터페이스이고, AcceptHeaderLocaleResolver는 Accept-Language에 기반해 국제화하는 구현체이다.

**LocaleResolver의 구현체를 적절히 변경해 쿠키 기반으로 국제화를 할 수 있다.**

***

## 2. 검증1 - Validation

검증은 서버에서 Http Request를 받았을 때, Http Request가 정상적인지를 판별하는 작업이다. 

예를 들어, 웹 화면에서 클라이언트가 Integer를 입력해 서버로 Request 한다고 하자. 서버 입장에서는 클라이언트가 특정 범위 내의 Integer만으로 제한해야 할 수 있고, Integer 이외의 다른 타입의 값을 입력하는 것을 막아야한다.

이러한 일련의 작업을 검증이라고 한다.

### 2.1 검증 직접 처리

검증 작업을 하기 이전에 스프링 MVC의 동작 메커니즘을 다시 살펴볼 필요가 있다.

사용자가 특정 웹 페이지에서 Http API를 통해 서버에 Request한다. 서버의 컨트롤러는 Request를 받아

