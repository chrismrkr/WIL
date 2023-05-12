# 자바스크립트

자바스크립트는 웹 사이트의 동적인 기능을 구현하기 위해 사용된다. 

자바스크립트는 싱글스레드 기반 함수형 프로그래밍 언어로 클로저와 콜백 패턴을 잘 이해하도록 하자.

## 1. 자바스크립트 언어의 특징과 동작방식

자바스크립트는 **싱글 스레드** 언어이다. 그렇다면 어떻게 여러 요청을 싱글 스레드로 처리할 수 있을까?

자바스크립트는 싱글 스레드더라도 런타임에 브라우저의 도움을 받아 **비동기 처리**를 통해 여러 요청을 처리할 수 있다.

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

이는 단일 웹 사이트를 event-driven으로 구현한다는 관점에서 멀티 스레드보다 더 합리적인 선택이라고 볼 수 있다. (WAS 구현과 비교해보면 알 수 있다.)   

## 2. 소스코드 위치

자바스크립트는 일반적으로 HTML 기반으로 만들어진 웹 페이지의 동적인 이벤트를 구현하기 위해 사용된다.

소스코드의 위치는 크게 2가지 이다.

### 2.1 HTML 파일 내부

\<head> \</head> 또는 \<body> \</body>에 구현할 수 있다.

### 2.2 HTML 파일 외부

동일 서버 또는 외부 서버에 위치할 수 있다. .js 파일로 존재하는 소스를 불러오면 된다.

.js를 불러오는 부분에서 웹 화면의 로딩이 멈추게 된다. 또한, js는 보통 HTML의 요소에 적용된다.

그러므로, \</body> 태그 바로 직전에 적용해주는 것이 바람직하다. (\<body> 태그 속의 마지막에 쓰도록 하자.)


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

자바스크립트는 완벽히 객체지향 언어라고 부르기 어려웠다. 물론, 지금은 클래스의 개념이 등장했고, 인터페이스의 개념이 typescript에서 등장했다.

하지만, JAVA의 다형성, 추상성 등의 객체지향적 특성과 비교했을 때 객체지향 언어라고 부르기 어렵다.

### 9.1 선언 방식

최초의 자바스크립트는 객체(Object) 개념만 존재했다. Object는 아래와 같이 선언할 수 있다.

```javascript
var car = {
  name: "G90",
  price: 100
  introduce: function() { console.log(this.name, this.price); 
  };
```

그렇다면, 상속성은 어떻게 해결할 수 있었을까? 처음에는 __proto__을 이용했다. 

```javascript
car.__proto__ = vehicle
```

그래도 결과적으로 ES6에서 클래스의 개념을 도입했다. 아래와 같은 형식으로 선언할 수 있다.

```javascript
class Rectangle {
  constructor(height, width) {
    this.height = height;
    this.width = width;
  }
  
  // Getter
  get area() {
    return this.calcArea();
  }
  
  get height() {
    return this._height;
  }
  
  get width() {
    return this._width;
  }
  
  // Method
  calcArea() {
    return this.height * this.width;
  }
}

const square = new Rectangle(10, 10);
```

필드 변수의 getter를 생성할 때는 this.\_필드변수 와 같은 형식으로 선언해야 한다.

필드 변수를 this 객체를 통해 불러올 때, 묵시적으로 getter가 호출되므로 순환참조 문제가 발생할 수 있다.

### 9.2 ES6+에서 추가된 기능

+ public 필드, private 필드 선언

private 필드는 #const field = ... 와 같은 방식으로 선언할 수 있다.

+ class 상속

Object에 대한 상속은 \_\_proto\_\_로 할 수 있었다. class는 Java와 동일하게 extend를 사용하면 된다.

멤버함수 오버라이딩도 당연히 가능하다.

## 10. 함수

자바스크립트는 함수형 프로그래밍 언어의 특성이 짙다. **왜냐하면, 함수를 값으로써 사용할 수 있는 1급 객체 취급을 할 수 있기 때문이다.** 

함수를 1급 객체로 사용할 수 있다는 특징은 CallBack 패턴으로 프로그래밍을 할 수 있도록 만들었다.

이를 통해 함수형 프로그래밍의 특성을 잘 살릴 수 있었다.

### 10.1 일반함수

아래와 같은 형식으로 선언한다. 재사용이 가능하다.

```javascript
function func1() { ... };
const func = func1();
```

### 10.2 익명함수

아래와 같은 형식으로 선언한다. 재사용이 불가능하지만 함수 자체가 메모리를 차지하지 않는다는 장점이 있다.

```javascript
const func = function() { ... };
```

### 10.3 람다함수(화살표함수)

익명함수와 기능은 같다. 아래와 같이 표현해 선언할 수 있다.

```javascript
const func = () => ...;
```

### 10.4 클로저(Closure) 패턴

자바스크립트와 같은 합수형 프로그래밍 언어에서 중요하게 등장하는 개념이다.

아래의 코드를 예시로 살펴보자.

```javascript
function outerFunc() {
    let x = 10;
    let innerFunc = function() {
        console.log(x);
    }
    innerFunc();
}

outerFunc();
```

outerFunc() 함수 내의 지역변수 x를 선언한 후, 내부적으로 innerFunc()를 정의한 후 outerFunc의 변수 x를 사용하고 있다. 

클로저의 정의는,

**A closure is the combination of a function and the lexical environment within which that function was declared.**

클로저는 정의할 함수의 lexical environment와 정의하는 함수의 조합이다. 라는 뜻이다.

여기서 정의하는 함수는 내부함수 innerFunc()를 의미하고, lexical environment는 outerFunc()를 의미한다.

위의 코드를 참고하면 어느정도 납득할 수 있다. 그렇다면 클로저는 어떻게 사용될까?


#### 10.4.1 상태기억

버튼을 클릭하는 횟수만큼 화면에 클릭 회수를 출력하는 화면을 만든다고 가정하자.

그렇다면, 이전에 몇번 클릭되었는지에 대한 정보를 기억해야 한다. (상태기억 필요)

아래의 코드를 확인하고, 결과가 어떻게 될지 예상해보자.

```html
<!DOCTYPE html>
<html>
<body>

<h1>지역 변수를 사용한 Counting</h1>

<button id="increase">+</button>
<p id="count">0</p>
<script>
  var increaseBtn = document.getElementById('increase');
  var count = document.getElementById('count');

  function increase() {
    // 카운트 상태를 유지하기 위한 지역 변수
    var counter = 0;
    return ++counter;
  }

  increaseBtn.onclick = function () {
    count.innerHTML = increase();
  };
  
</script>
</body>
</html>
```

결과는 클릭을 여러 번해도 항상 1이 표시된다. 왜냐하면, increase() 함수 내에서만 counter가 유효하기 때문이다. 

물론, 전역 변수를 이용해서도 원하는 기능을 구현할 수 있지만 바람직하지 않다.

클로저를 이용해 코드를 수정하도록 하자.

```html
<!DOCTYPE html>
<html>
<body>

<h1>지역 변수를 사용한 Counting</h1>

<button id="increase">+</button>
<p id="count">0</p>
<script>
  var increaseBtn = document.getElementById('increase');
  var count = document.getElementById('count');

  let countFunc = (function() {
    // 카운트 상태를 유지하기 위한 지역 변수
    var counter = 0;
    return {
      increase() {
        counter++;
        return counter;
      }
    }
  })();

  increaseBtn.onclick = function () {
    count.innerHTML = countFunc.increase();
  };
</script>
</body>
</html>
```

이처럼, 렉시컬 환경을 공유하는 클로저를 만들어 사용한다면, 의도치 않은 변경을 방지할 수 있으므로 바람직한 프로그래밍이 된다. 

#### 10.4.2 정보의 은닉

클래스의 필드의 접근자인 private과 public과 같은 기능을 수행할 수 있도록 만들 수 있다.

count를 증가시키고 감소하는 함수를 만들도록 하자. 이때, 현재 count가 얼마인지 필드에 직접 접근할 수 없어야 한다.

```javascript
function counter() {
    let Count = 0;
    this.increase = function () {
        return ++Count;
    }

    this.decrease = function() {
        return --Count;
    }
};
```

## 11. Callback 함수

함수의 1급 객체 특성을 가장 잘 활용할 수 있는 함수형 프로그래밍의 패턴이다.

Callback 함수는 비동기처리를 위해 사용된다. 비동기처리(CallBack)은 Queue에 저장된 후, 하나씩 처리된다.

### 11.1 Promise 객체

Promise 객체는 Callback 함수 자체를 1급 객체화 했다는 점에서 장점이 있다.

Promise 객체는 아래와 같은 형식으로 선언할 수 있다.

```javascript
let Promise = new Promise(function(resolve, reject) {
                ...
              });
```

Promise 객체는 크게 2가지 특징이 있다.

+ State: Pending, fulfilled(resolve), rejected(reject)

이행 상태의 경우 resolve 함수를 then을 통해 실행하고, 거부 상태의 경우 reject 함수를 catch를 통해 실행한다.

+ Producer, Consumer:

new Promise((resolve, reject) => { ... } ) 를 통해 Promise 객체를 Produce한다.

그리고 then, catch, finally절에서 produce한 Promise를 Consume한다.

**then에서는 Promise 객체를 반환하거나, 값을 바로 사용할 수 있다.** 

**물론, Promise 객체를 매개변수로 전달받더라도 값으로 자동으로 값으로 꺼내서 사용할 수 있다.**

(catch는 Promise 객체 chain에 대해서 한번만 적용하면 거부 상태를 잡아낼 수 있다.)

번외로 finally는 이행, 거절 상태와 관계없이 Promise 객체가 반환될 때 마다 항상 실행되도록 만들 수 있다.

아래의 코드를 확인하며 Promise 패턴에 익숙해지도록 하자.

```javascript

// ex1. 숫자 1을 x2 => x3 => -1 => 콘솔 출력하는 비동기 코드를 작성하자
// 1. create producer
const producer = new Promise((resolve, reject) => {
        resolve(1);
       };

// 2. consume
produce.then(num => num*2).then(num => num*3).then(num => num-1).then(console.log(num));

// ex1을 아래와 같이도 작성할 수 있다. 람다식이 익숙하지 않다면 아래가 더 편할 수 있다.
const fetchNumber = new Promise((resolve, reject) => {
    setTimeout(resolve(1), 1000);
});

fetchNumber
.then(function(num) {
    return new Promise((resolve, reject) => {
        resolve(num*2);
    });
})
.then(function(num) {
    return new Promise((resolve, reject) => {
        resolve(num*3);
    })
})
.then(function(num) {
    return new Promise((resolve, reject) => {
        resolve(num-1)
    })
})
.then(num => console.log(num));
```

### 11.2 Promise All


### 11.3 async, await

Promise 객체를 이용해 비동기처리를 실시한다고 가정하자. 

만약, 특정 then절에서 에러가 발생한다면, 어느 곳에서 에러가 발생했는지 찾는데 어려움이 있다.

또한, 계속해서 Promise 객체를 사용해야 한다는 문제가 있다. 그래서 등장한 것이 async, await이다.

postId를 요청 파라미터로 GET REST API를 호출해서 name을 가져오는 예시를 보자. 

fetch는 항상 Promise 객체로 반환한다.

```javascript
function fetchAuthorName(postId) {
  return fetch(`https://jsonplaceholder.typicode.com/posts/${postId}`)
    .then((response) => response.json())
    .then((post) => post.userId)
    .then((userId) => {
      return fetch(`https://jsonplaceholder.typicode.com/users/${userId}`)
        .then((response) => response.json())
        .then((user) => user.name);
    });
}

fetchAuthorName(1).then((name) => console.log("name:", name));
```

***

## 12. 싱글톤 패턴
 
싱글톤 패턴이란 특정 기능을 위해서 하나의 객체만을 생성해 사용하는 패턴을 의미한다. 

Java에서는 static 변수 또는 스프링 빈으로 등록해 사용하는 방법이 있다.

### 12.1 ES5에서 클로저를 활용한 구현

```javascript
const Singleton = (function() {
    let instance;

    function setInstance() {
        instance = {
            id : 1,
            name : "hello"
        };
    }

    return {
        getInstance() {
            if(!instance) {
                setInstance();
            }
            return instance;
        }
    }
})();
```
***

## 13. 객체 생성 방식

리터럴 방식, 생성자 함수 방식, 그리고 클래스 방식 3가지가 있다.

하나씩 살펴보면서 프로토타입 기반의 객체지향 언어인 자바스크립트에 대해서 좀 더 깊게 알아보도록 하자.

### 13.1 리터럴 방식

아래와 같이 객체를 생성하는 것을 의미한다.

```javascript
let object = {
    att1: 10,
    att2: "hello"
};
```

위의 객체의 prototype은 Object이다. 그러므로, 프로토타입의 constructor 함수를 통해 객체가 생성된다.

동일한 모양의 객체를 여러번 만드는 상황에서는 불필요한 코드가 중복된다는 단점이 있다.

### 13.2 생성자 함수 방식

아래와 같이 객체를 생성한다. 메모리 낭비를 막기 위해 즉시실행함수를 사용했다.

```javascript
const Person = (function() {
  function Person(name) {
    this.name = name;
  }
  
  Person.prototype.sayMyName() {
    console.log(`Hello, My name is ${this.name}`); 
  }
  
  return Person;
}());

let kim = new Person("kim");
let lee = new Person("lee");
```

물론, 아래와 같이 코드를 작성하더라도 결과는 동일하다.

```javascript
const Person = (function() {
  function Person(name) {
    this.name = name;
    
    this.sayMyName() {
      console.log(`Hello, My name is ${this.name}`); 
    }
  }
  
  return Person;
}());

let kangok = new Person("kangok");
let lee = new Person("lee");
```

kangok 객체와 lee 객체의 프로토타입은 Person이다. 

두번째 코드에서는 두 sayMyName() 멤버함수가 객체 별로 독립적으로 존재하는 낭비가 있다.

두 객체의 프로토타입은 동일하므로 굳이 두번째와 같이 코드를 작성할 필요는 없다. 

그러므로, 객체 생성 시 첫번째처럼 코드를 작성하는 것이 더 바람직하다. 정적 메소드를 추가하려면 아래와 같이 쓰면 된다.

```javascript
const Person = (function() {
  function Person(name) {
    this.name = name;
  }
  
  Person.prototype.sayMyName() {
    console.log(`Hello, My name is ${this.name}`); 
  }
  
  Person.greet() {
    console.log("Hello everyone!");
  }
  return Person;
}());
```

**생성자 함수 방식을 통해 객체를 생성할 때, Arrow 함수를 사용하면 안되는 이유**

일반함수와 Arrow 함수의 차이점부터 먼저 알아보자.

1. this

this는 함수가 **호출**되는 시점에 정의된다는 것을 유의하자. 렉시컬 스코프와 달리 정의되는 시점이 아닌 호출되는 시점이다.

일반 함수에서 this는 window 객체이다. 일반 함수 내의 callback, 중첩 함수도 마찬가지로 this는 window 객체이다. 

예를 들어, 객체 내의 메소드 함수에서 callback 또는 중첩 함수가 일반함수로 쓰인다면, 여기서의 this는 전역객체(window)이다. 주의하자.

리터럴과 new를 통해 생성한 객체의 this는 해당 객체를 바인딩한다.

메소드 함수에서의 this는 호출하는 객체를 바인딩한다.

생성자 함수에서의 this에는 생성하는 객체를 바인딩한다. 아래의 예제를 보자. 문제가 있는 코드이다.

```javascript
let person = {
  name: "kim",
  sayHi() {
    setTimeout(
      function() {
        console.log(this.name)
      }, 100)
  }
};
```
sayHi()를 통해 name이 정상적으로 출력되지 않는다. 이를 제대로 하려면 setTimeout 부분을 Arrow 함수로 바꾼다.

```javascript
setTimeout(
      () => {
        console.log(this.name)
      }, 100)
```
화살표 함수의 this는 상위 스코프의 this이기 때문에 정상적으로 출력된다.

여기서 화살표 함수 대신 apply, call, 또는 bind 함수를 사용해서 해결할 수 있다.

```javascript
let person = {
  name: "kim",
  sayHi() {
    setTimeout(
      function() {
        console.log(this.name)
      }.bind(this), 100);
  }
};
```
apply, call, bind 모두 함수의 this를 첫번째 인수로 바꾸는 함수이다. 

bind는 apply, call과 달리 함수를 호출하지 않는다. apply는 call과 달리 arguments를 배열로 받아야 한다.

Arrow 함수에서의 this가 정적으로 바인딩 되고, 상위 스코프의 this를 바인딩한다. 상위 스코프는 렉시컬 스코프이다.

2. 프로토타입

Arrow 함수는 일반함수와 달리 prototype이 없다.


**객체를 생성할 때 prototype의 constructor를 이용한다. 그러므로, Arrow 함수는 prototype이 없으므로 객체를 생성할 수 없다.**

### 13.3 클래스 방식

클래스 방식도 내부적으로 생성자 방식을 따른다. super, extends 키워드를 통한 상속을 제공하고, 모든 프로퍼티의 enumeration은 false라는 점에서 생성자 방식과 차이가 있다.

클래스 또한 함수이므로 클래스는 프로토타입 기반 객체지향언어에 익숙하지 않은 프로그래머를 위한 문법적 설탕이라고도 볼 수 있다.

참고로, enumeration이 false이면 for ... in ... 으로 프로퍼티 검색이 불가능하다.

또한, static, get, set 키워드도 존재한다. 

클래스 방식으로 아래와 같이 코드를 작성할 수 있다.

```javascript
class Person {
  constructor(name) {
    this.name = name;
  }
  
  sayMyName() {
    console.log(`Hello, My name is ${this.name}`); 
  }
  
  static greet() {
    console.log("Hello everyone!");
  }
}

let kim = new Person("kim");
```

상속의 경우, 서브클래스(자식클래스)의 생성자는 암묵적으로 super(..args)로 정의된다.

아래와 같이 상속을 구현할 수 있다.

```javascript
class parent {
  constructor(a) {
    this.a = a;
  }
}

class child extends parent {
  constructor(a, b, c) {
    super(a);
    this.b = b;
    this.c = c;
  }
}
```

서브클래스에서 부모클래스의 멤버함수를 사용할 때는 call 메소드를 사용하자. 

***

## 14. function 예약어 사용이 필요할까?

자바스크립트로 짜여진 코드를 읽어보면, function 키워드로 시작하는 경우가 많다.

function으로 시작하는 경우, 

1. 특정 로직을 호출하는 경우인지,
2. 객체를 생성하는 경우인지,
3. 멤버함수를 생성하는지 명확하게 알 수 없다. 

첫번째 경우에는 화살표 함수로 대체할 수 있다.

두번째 경우에는 class 키워드로 대체할 수 있다.

세번째 경우에는 메소드 축약형을 사용할 수 있다. 그러므로, 가독성을 위해서 function 예약어를 자제하는 것도 좋은 프로그래밍 방식이라고 생각한다.

## 15. 브라우저와 관련된 객체

자바스크립트를 통해 요소에 동적인 액션을 줄 수 있다. 브라우저 관련 객체에 대해서 알아보도록 하자.

### 15.1 내장 객체

+ window: 브라우저의 최상위 객체
+ document: \<body> 태그를 지칭하는 객체
+ navigator: 브라우저의 정보를 가진 객체
+ history: 방문기록을 가진 객체
+ location: 현재 URL 정보를 가진 객체
+ screen: 현재 화면정보를 가진 객체

### 15.2 DOM(document) 객체

document 객체로부터 HTML의 요소를 id로 접근해 스크립트를 적용할 수 있도록 한다.

id 뿐만 아니라 class로도 접근할 수 있다. DOM 객체에서 제공하는 API에 대해서 알아보도록 하자.

+ getElementById: id가 있는 HTML 요소에 접근
+ getElementByClassName: class가 있는 HTML 요소에 접근
+ getElementByTagName: 태그 이름으로 HTML 요소에 접근
+ querySelector, querySelectorAll: CSS 선택자를 통해 검색
+ innerText, innerHTML: HTML 요소의 내용을 수정할 수 있음
+ getAttribute, setAttribute: HTML 요소 태그의 속성 값을 get, 또는 set함.

객체에 접근해서 Event를 발생시키기 위해서는 아래의 API를 사용한다.

+ addEventListener("이벤트", 함수)


## 16. Ajax 통신

Ajax 통신이란 비동기 방식으로 데이터를 송수신하는 방법을 의미한다.

자바스크립트에서는 XMLHttpRequest 객체를 통해 Ajax 통신을 한다. (HTML의 \<form>,\<a> 태크와 연동해서 통신한다.)

XMLHttpRequest는 비동기 통신을 위한 프로퍼티와 메소드를 제공하는 객체이다.

### 16.1 JSON 생성 방식

request, response Body는 객체 리터럴 방식으로 생성한다.

+ JSON.stringfy(): 객체를 직렬화 한다.
+ JSON.parse(): 직렬화된 내용을 객체로 변환한다.

### 16.2 XMLHttpRequest 특징

#### 16.2.1 프로퍼티

+ readyState: 현재 HTTP 요청 상태를 나타내는 상수
+ status: HTTP 요청에 대한 응답 상태를 나타내는 정수
+ statusText: HTTP 요청에 대한 응답 메세지를 나타내는 메세지
+ responseType: HTTP 응답 타입(ex. application/json)
+ response: HTTP 요청에 대한 응답 dataBody

#### 16.2.2 이벤트 핸들러

+ onreadystatechange(): readyState 프로퍼티 변경을 감지함.
+ onerror(): HTTP 요청이 에러난 경우
+ onload(): HTTP 요청이 성공적인 경우

#### 16.2.3 메소드

+ open(): HTTP 요청 초기화
+ send(): HTTP 요청 전송
+ abort(): 이미 요청된 HTTP 중단
+ setRequestHeader(): HTTP 헤더 설정

### 16.3 시나리오

#### 16.3.1 HTTP 요청

```javascript

// GET 요청
let xhr = new XMLHttpRequest();

xhr.open('GET', '/users');
xhr.setRequestHeader('content-type', 'application/json');

xhr.send();


// POST 요청
let xhr2 = new XMLHttpRequest();

xhr.open('POST', '/users');
xhr.setRequestHeader('content-type', 'application/json');

xhr.send({id: "user", password: "1111"});
```

Self-Descriptive한 API란 HTTP 응답 상태코드 및 메세지, Content-Type, media 정보를 링크로 첨부하여 생성하는 것이다.

HATEOAS한 API란 링크를 통해 다음 상태로 전이될 수 있는 것을 의미한다. 상세조회 링크를 사용해서 어느 링크로 전이되는지 알 수 있도록 한다.

두가지 특성을 가진 HTTP API를 RESTful API라고 한다.

#### 16.3.2 HTTP 응답

```javascript
xhr.onreadystatechange = () => {
  debugger;
};
```
xhr.readyState의 상태를 확인하여 분기될 수 있도록 코드를 작성한다. 뿐만 아니라 xhr.status를 통해서도 확인할 수 있다.
