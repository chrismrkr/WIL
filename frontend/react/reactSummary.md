# React

React Document를 공부하여 정리한 내용이다.

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

\<form> element는 다른 React DOM element와 달리 내부 상태를 갖는다.

순수 HTML \<form>은 name 속성을 통해 입력 값을 받는다.

```html
<form action="...">
  <input type='text' name='username' />
</form>
```

기본적으로 \<form>은 새로운 페이지로 이동하는 동작을 한다.

그러나, 자바스크립트를 통해 \<form>을 제출한 후, 입력 데이터에 접근하는 방식이 사용되기도 한다. 이를 제어 컴포넌트라고 한다.

### 제어 컴포넌트

\<input>, \<textarea>, \<select>와 같은 form element는 사용자 입력을 바탕으로 컴포넌트의 state를 관리한다.(setState({...})

state를 단일 출처로 하여 입력과 이벤트를 제어한다. 아래의 예제를 살펴보자.

```jsx
// <input>에 값을 입력한 후 submit시, alert에 <input> 값이 담기는 컴포넌트 구현하기
class InputButton extends React.Component {
  constructor(props) {
    super(props);
    this.state = {value: ""};
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleChange = this.handleChange.bind(this);
  }
  handleChange(e) {
    this.setState({value : e.target.value});
  }
  handleSubmit(e) {
    alert(this.state.value);
    e.preventDefault();
  }
  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <input type="text" value={this.state.value} onChange={this.handleChange} />
        <submit type="submit" value="submit"/>
      </form>
    );
  }
}
```

제어 컴포넌트의 핵심은 state에 Single Source of Truth를 만들고, 이를 입력과 이벤트 핸들러에서 사용한다는 것이다.

form 태그와 관련된 Formik 라이브러리에 대해서도 알아보면 좋다.

## 9. state 끌어올리기

동일한 데이터의 변경사항을 여러 컴포넌트에 반영해야 하는 경우도 있다. 

이러한 경우에 가까운 부모 컴포넌트에 state를 저장하여 자식으로 전달하는 것을 state 끌어올리기라고 한다.

```jsx
// 섭씨 온도를 입력하여 물이 끓는지 아닌지를 확인하는 온도 계산기 구현
class Calculator extends React.Component {
  constructor(props) {
    super(props);
    this.state = {temparature: ""};
    this.handleChange = this.handleChange.bind(this);
  }
  handleChange(e) {
    this.setState({temparature : e.target.value});
  }
  return (
    <fieldset>
      <legend>온도를 입력하세요</legend>
      <input value={this.state.temparature} onChange={this.handleChange} />
      <BoilingVerdict celsius={parseFloat(this.state.temparature)} />
    </fieldset>
  );
}

class BoilingVerdict extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    if(this.props.celsius > 100) return <p> 끓는 중.. </p>;
    else return <p> 아직 안 끓는 중.. </o>;
  }
}
```

this.state는 데이터의 변경사항을 반영하기 위해 사용된다. (반대로, props는 불변 속성을 저장한다.)

그러므로, state 내의 속성은 서로 독립적이어야 한다. 예를 들어, 배열과 배열의 길이가 this.state 속성에 같이 있으면 안된다.

배열이 변경될 때 배열의 길이는 항상 변화하므로, 데이터의 정합성이 깨질 위험성이 있기 때문이다.

## 10. 합성과 상속

### 컴포넌트를 다른 컴포넌트에 담기

첫번째로 props.chilldren 속성을 사용할 수 있다.

```jsx
class Border extends React.Component {
  render() {
    return <div> {props.children} </div>;
  }
}
```

뿐만 아니라 고유 식별자로도 컴포넌트를 담을 수 있다.

```jsx
class Border extends React.Component {
  render() {
    return (
      <div>
        <div>{props.left}</div>
        <div>{props.right}</div>
      </div>
    );
  }
}

<Border left={...} right={...} />
```

구체적인 컴포넌트가 일반적인 컴포넌트를 렌더링 하는 방식으로 합성이 일어난다.

## 11. React로 생각하기

백엔드 API 서버로부터 내려 받은 데이터로 프론트엔드를 구성한다고 하자. 고려해야할 단계별 과정은 아래와 같다.

### Step1. 컴포넌트 계층구조화

디자이너가 제작한 구성대로 모든 컴포넌트(하위 컴포넌트 포함)를 만든다.

컴포넌트는 단일 책임 원칙(하나의 컴포넌트는 한가지 기능을 담당한다는 원칙)으로 계층 구조화하여 생성한다.

### Step2. 정적 버전 만들기

서버로 부터 받은 데이터를 바인딩하기 전에 정적인 버전의 프론트엔드를 제작한다.

그러므로, 컴포넌트는 state 속성을 사용하지 않는다. (state는 컴포넌트의 변동사항을 기록하기 위해 사용함)

상향식, 또는 하향식으로 만들 수 있지만 복잡한 App의 경우에는 상향식으로 만드는 것이 더 테스트하기에 적합하다.

상위 컴포넌트가

### Step3. 최소 state 구성찾기

정적 버전의 React App을 만든 후, 서버로부터 받은 데이터를 바인딩하기 위해서 컴포넌트에 state가 필요하다.

이때, state는 가능하면 중복없이 최소 집합으로 만들어야 한다. 가령, 배열이 state에 있다면, length 함수를 통해 배열의 길이를 구할 수 있으므로 length는 state로 저장하지 않는다.

또한, props와 state의 역할을 명확히 하여 state를 구성한다. props는 불변 객체 및 속성을 담당하므로 props에 해당되는 것을 state에 저장할 필요는 없다.

### Step4. state의 위치 결정

React는 컴포넌트 계층구조를 따라 단방향 데이터 흐름을 갖는다. 

state 위치를 결정하기 위해서 React Document에서 제시한 방법은 아래와 같다.

+ state를 갖는 컴포넌트를 모두 찾을 것
+ 특정 state가 있어야 하는 컴포넌트들의 공통 컴포넌트를 찾을 것
+ 공통 또는 더 상위에 있는 컴포넌트가 state를 가져야 한다.
+ 적절한 공통 컴포넌트가 없다면 컴포넌트를 새로 생성한다.

### Step5. 역방향 데이터 흐름 추가

