# React

## 1. JSX

Javascript Extension, React element를 생성하는데 사용하는 문법이다.

+ HTML 마크업을 넣어서 사용할 수 있음
+ 중괄호를 사용하여 마크업 태그에 자바스크립트 변수 바인딩 가능(ex. \<h1> {this.props.title} \</h1>)

## 2. Element 렌더링

element는 React App에서의 가장 작은 단위로 화면에 표시할 내용을 의미한다.

React로 개발된 App은 일반적으로 하나의 Root Dom 노드를 갖고, Root Dom에 element를 전달하여 렌더링한다.

React Dom은 element를 이전 element 상태와 비교하여 필요한 경우에 업데이트한다.

## 3. Components와 props

아래의 예시를 보면 react component를 렌더링하는 방법을 알 수 있다.

```jsx
class Hello extends React.Component {
  constructor(props) { super(props); }
  render() {
    return <hl> hello, {this.props.name} </h1>;
  }
}

const root = ReactDOM.createRoot(document.getElementById('root');
const element = <Hello name='kangok'/>;
root.render(element);
```

+ 컴포넌트 이름은 항상 대문자이어야 한다.
+ 컴포넌트는 합성 및 추출할 수 있다. 추출은 컴포넌트의 여러 개의 작은 컴포넌트로 나누는 것을 의미한다.
+ props는 읽기 전용이며, 부모에서 자식 컴포넌트로 값을 전달할 때 사용하기도 한다.


## 4. state와 생명주기

1초에 한번씩 현재 시간을 출력하는 Clock 컴포넌트는 아래와 같다.

```jsx
class Clock extends React.Component {
  constructor(props) {
    super(props);
    this.state = {date: new Date()};
  }
  componentDidMount() {
    this.timeID = setInterval( () => this.tick(), 1000);
  }
  componentWillUnmount() {
    clearInterval(this.timeID);
  }
  tick() {
    this.setState({date: new Date()});
  }
  render() {
    return (
      <div>
        <h1> Clock </h1>
        <h2> {this.state.date.toLocaleTimeString()} </h2>
      </div>
    );
  }
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(</Clock />);
```

\<**호출 과정**> : 
root.render() 
-> DOM 업데이트 
-> DOM 업데이트 시 componentDidMount() 호출 
-> this.setState(..)로 state 변경 
-> React는 state 변경을 감지하여 다시 render() 호출
-> render() 호출 직전에 componentWillUnmount() 호출

위의 예제를 통해 state와 변경감지에 대해 알게된 점을 정리하면 아래와 같다.

+ state는 컴포넌트 변경 값을 저장하는 필드이다. React는 이를 감지하여 변경될 때마다 render() 함수를 호출한다.
+ componentDidMount()는 render() 함수 이후에 실행되는 함수이다.
+ componentWillUnmount() react가 변경을 감지하여 render() 함수를 호출하기 직전에 실행된다.
+ 그러므로, componentDidMount, componentWillUnmount로 **생명주기**를 관리할 수 있다.

state는 비동기적으로 변화할 수 있고, 생성자에서 정의한다.

**state를 변경할 때는 반드시 this.setState를 사용하여 React가 인식할 수 있도록 한다.**

## 5. 이벤트 처리하기

DOM element와 React element의 이벤트 처리방식은 유사하다.

react element는 자바스크립트 함수 자체 이벤트 핸들러를 전달한다.

```jsx
<button onClick={activatefunction}> BUTTON </button>
```
```html
<button onclick="activateFunction()"> BUTTON </button>
```

이벤트 처리시, 아래의 3가지를 주의한다.

### e.preventDefault()

웹 브라우저의 고유 동작을 막아준다. 예를 들어, \<form> 태그 내의 \<button>을 눌렀을 때의 기본 동작은 다른 웹 페이지로 이동하는 것이다.

```jsx

class App extends React.Component {
  constructor(props) {
    super(props);
  }
  
  handleSubmit(e) {
    alert("hello");
    // e.preventDefault();
  }
  
  render() {
    return (
      <div>
        <h1> preventDefault 테스트 </h1>
        <form onSubmit={this.handleSubmit}>
          <button type="submit">버튼</button>  
        </form>
      </div>
    );  
  }
}
```
만약 e.preventDefault()가 없다면, 특정 url로 redirect한다. 그러므로, 해당 작업을 원하지 않으면 e.prevnetDefault가 필요하다.

### e.stopPropagation()

대표적으로 자식 컴포넌트에서의 onClick 이벤트가 부모 컴포넌트까지 전달되지 않는 것을 원할 때 사용하는 메서드이다.

### this 바인딩

자바스크립트에서의 this는 함수 호출 시점에 결정된다.

일반함수에서의 this는 전역객체를 바인딩하므로 중괄호를 통해 이벤트 함수를 전달할 때는 반드시 this 바인딩을 명시적으로 해야 한다.

그렇지 않은 경우에는 화살표 함수를 통해 이벤트 함수를 전달할 수 있으나, 이벤트 함수가 다른 함수를 호출 할 수 있으므로 명시적인 this 바인딩을 사용하자.

```jsx
  class Toggle extends React.Component {
    constructor(props) {
      super(props);
      this.state = {isToggle : true};
      this.click = this.click.bind(this);
    }
    click() {
      this.setState({isToggle: !this.isToggle});
    }
    render() {
      return <button onClick={click}> {this.state.isToggle ? "ON" : "OFF"} </button>;
    }
  }
```

## 6. 조건부 렌더링

자바스크립트 조건문을 렌더링에도 사용할 수 있다. 

## 7. 리스트와 key

자바스크립트에서의 리스트 변환 방법은 아래와 같다.

```js
const numbers = [1,2,3,4,5];
numbers.map(number => number*2);
```

유사한 방법으로 반복적으로 컴포넌트를 렌더링할 수 있다.

```jsx
class NumberList extends React.Component {
  constructor(props) { super(props); }
  render() {
    const numbers = [1,2,3,4,5];
    const liList = numbers.map(number => <li>{number}</li>);  
    return (
      <ul>{liList}</ul>
    );
  }
}
```

DOM 노드가 리스트를 순차 순회할 때, React도 리스트를 순차 순회한다.

React는 리스트가 기존과 동일한지 순차적으로 확인하면서 element가 추가되었다면 변경한다.

그러므로, 만약 리스트의 맨 앞에 element가 추가되면, react는 순차 순회하므로 모든 자식을 변경해야하는 문제가 있다.

이를 해결하기 위해서 \<li>에는 key가 존재한다. key는 아래와 같이 추가하면 된다.

```jsx
class NumberList extends React.Component {
  constructor(props) { super(props); }
  render() {
    const numbers = [1,2,3,4,5];
    const liList = numbers.map(number => <li key=number.toString()>{number}</li>);  
    return (
      <ul>{liList}</ul>
    );
  }
}
```

## 8. Form



