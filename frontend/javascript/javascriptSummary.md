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

비록 순서는 asyn call, standard call이지만, 실제 결과는 standard call, async call 순서로 출력된다. 

동작과정을 살펴보자.

setTimeOut이 실행되면, setTimeOut이 Call Stack이 저장된다.

그리고, setTimeOut은 Web API가 처리하므로 callback 함수인 console.log('async call')을 Callback Queue에 저장한다.

Callback Queue에 저장된 상태에서 console.log('standard call')이 Call Stack에 추가된다.

그리고, console.log('standard call')을 실행한다.

그 후, Event Loop는 Call Stack이 비어있는 것을 확인하여, CallBack Queue의 내용을 Call Stack으로 옮긴다.

마지막으로, console.log('async call')를 실행한다. 위와 같은 방식으로 비동기 작업을 런타임에 실행한다.

**그렇다면, 멀티 스레드로 여러 작업을 동시에 처리할 수 있는데 왜 하필 자바스크립트는 싱글 스레드일까?**

