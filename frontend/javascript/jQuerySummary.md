# JQuery 정리

## 1. 기본 문법

선택 및 동작함수1, 동작함수2로 동작한다.

```javascript
$(선택자. CSS와 동일).동작함수1().동작함수2();
```

## 2. 예제

문법에 대해서 정리하기 전에 아래의 요구사항을 만족하기 위해 jQuery 및 자바스크립트로 코드를 작성하자.

**li 요소들을 클릭하면 클래스 이름을 selected로 바꾸는 jQuery 및 스크립트를 작성하시오.**

```html
    <ul id="navigation">
      <li>HTML</li>
      <li>CSS</li>
      <li>javascript</li>
      <li class="selected">jQuery</li>
      <li>PHP</li>
      <li>mysql</li>
    </ul>
```

```javascript
  let liList = document.querySelectirAll("#navigation li");
  liList.forEach((li, idx) => {
    li.className = "";
    li.className = "selected";
  });
  
  $("#navigation li").on("click", () => {
    $("#navigation li").removeClass('selected');
    $(this).addClass('selected');
  });
```

## 3. HTML을 인수로 전달받는 경우

```javascript
$('<p id="test">My <em>new</em> text</p>').appendTo('body');
```

## 4. Javascript 객체를 인수로 받는 경우

```javascript
let foo = {
    foo: 'bar',
    hello: 'world'
};

let bar = $(foo).prop('foo');
let hello = $(foo).prop('hello');
$(foo).prop('hello', 'modifyHello');
```

## 5. 특징

+ $()는 자기 자신을 반환하므로 체이닝이 가능하다.
+ this 사용 가능
