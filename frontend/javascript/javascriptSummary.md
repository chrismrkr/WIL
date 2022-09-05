# 자바스크립트

자바스크립트는 웹 사이트의 동적인 기능을 구현하기 위해 사용된다. 

## 1. 자바스크립트 언어의 특징과 동작방식

자바스크립트는 **싱글 스레드** 언어이다. 그렇다면 어떻게 여러 요청을 싱글 스레드로 처리할 수 있을까?

자바스크립트는 싱글 스레드더라도 런타임에 **비동기 처리**를 통해 여러 요청을 처리할 수 있다.

자바스크립트가 실행될 때 아래의 요소들이 실행을 돕는다.

+ Call Stack: 자바스크립트가 수행해야할 함수를 순차적으로 스택에 담는 용도
+ Web API: 웹 브라우저에서 제공하는 API로 비동기 작업을 담당함(Ajax, DOM, TimeOut)
+ Callback Queue: Web API에서 넘겨받은 Callback 함수를 저장함
+ Event Loop: Call Stack이 비어있다면, Callback Queue의 작업을 Call Stack으로 옮김

이를 이해하기 위해 아래의 코드를 살펴보자.

```javascript
setTimeOut(()=>console.log('async call'), 2000);
console.log('standard call')
```

순서는 asyn call, standard call이지만, 실제로는 standard call, async call로 출력된다. 

동작과정을 살펴보자.

setTimeOut이 실행되면, setTimeOut이 Call Stack이 저장된다.

그리고, setTimeOut은 Web API가 처리하므로 callback 함수인 console.log('async call')을 Callback Queue에 저장한다.

Callback Queue에 저장된 상태에서 console.log('standard call')이 Call Stack에 추가된다.

그리고, console.log('standard call')을 실행한다.

그 후, Event Loop는 Call Stack이 비어있는 것을 확인하여, CallBack Queue의 내용을 Call Stack으로 옮긴다.

마지막으로, console.log('async call')를 실행한다. 위와 같은 방식으로 비동기 작업을 런타임에 실행한다.

**그렇다면, 멀티 스레드로 여러 작업을 동시에 처리할 수 있는데 왜 하필 자바스크립트는 싱글 스레드일까?**

힙에 저장되는 공유 필드(자원)에 대해 접근할 때 발생할 수 있는 동시성 문제를 차단하기 위해서이다.

이는 단일 웹 사이트를 구현한다는 관점에서 멀티 스레드보다 더 합리적인 선택이라고 볼 수 있다. (WAS 구현과 비교해보면 알 수 있다.)   

## 2. 소스코드 위치

자바스크립트는 일반적으로 HTML 기반으로 만들어진 웹 페이지의 동적인 이벤트를 구현하기 위해 사용된다.

소스코드의 위치는 크게 2가지 이다.

### 2.1 HTML 파일 내부

\<head> \</head> 또는 \<body> \</body>에 구현할 수 있다.

### 2.2 HTML 파일 외부

동일 서버 또는 외부 서버에 위치할 수 있다. .js 파일로 존재하는 소스를 불러오면 된다.

.js를 불러오는 부분에서 웹 화면의 로딩이 멈추게 된다.


## 3. 변수와 자료형

자바스크립트는 동적타입 언어로 타입이 컴파일 타임에 결정되며, 런타임에 묵시적으로 변경될 수 있다.

변수는 다른 언어와 유사하게 primitive 타입, 그리고 object 타입 2가지가 존재한다.

전자는 call by value이고, 후자는 call by refernce이다.

전자는 대표적으로 string이 있고, 후자는 json, class, object가 있다.

### 3.1 var, let, const

var는 지역변수로 함수 내에서 유효하다. 

let은 block scoped로 함수 내의 특정 block에서만 유효하다.

const도 let과 마찬가지로 block scoped이지만, let과 달리 수정 불가능하다.

## 4. 출력문

자바스크립트의 출력문 생성 방법은 여러가지가 있다.

### 4.1 HTML 문서 출력
```html
<script>
  document.write(5);
</script> 
```
document 인터페이스는 브라우저가 불러온 웹 페이지의 인터페이스이다.

### 4.2 HTML 문서의 특정 부분에 동적 렌더링
```html
<script>
  document.getElementById("result").innerHTML = 5;
</script>

<body>
  <div id="result> </div>
</body>
```

### 4.3 alert를 이용한 출력
```javascript
alert(5);
```

### 4.4 콘솔창을 이용한 출력
```html
<script>
  console.log(5);
```


## 5. 연산자

다른 언어와 유사하고 아래만 새로 익히도록 하자.

+ === : 타입이나 값이 같은지 비교
+ !== : 타입이나 값이 다른지 비교

## 6. 반복문

다른 언어와 동일하다. (기본적인 for, while문 제공)

리스트 객체에 대해서는 파이썬과 유사하게 foreach문을 제공한다.
```javascript
list.foreach( { ... } );
```

## 7. 배열

동적 자료형으로 타입이 다른 변수, 객체를 모두 저장할 수 있다.

배열에 객체를 저장해 json 형태로 변환할 수 있다.

## 8. Map, Set

다른 언어와 동일하다. 배열, Map, Set의 멤버함수를 레퍼런스를 참고하도록 하자.

## 9. 객체와 클래스

자바스크립트는 객체지향 언어라고 부르기 어려웠다. 물론, 지금은 클래스의 개념이 등장했고, 인터페이스의 개념이 typescript에서 등장했다.

하지만, JAVA의 다형성, 추상성 등의 객체지향적 특성과 비교했을 때 객체지향 언어라고 부르기 어렵다.

최초의 자바스크립트는 객체(Object) 개념만 존재했다. Object는 아래와 같이 선언할 수 있다.

```javascript
var car = {
  name: "G90",
  price: 100
  introduce: function() { console.log(this.name, this.price); 
  };
```

그렇다면, 상속성은 어떻게 해결할 수 있었을까? __protype__을 이용했다.







