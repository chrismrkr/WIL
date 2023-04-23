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

**호출 과정**
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

state를 변경할 때는 반드시 this.setState를 사용하여 React가 인식할 수 있도록 한다.
