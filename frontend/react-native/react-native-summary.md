# 리액트 네이티브

## 기본 컴포넌트

+ React Native 기본 컴포넌트 참고 링크: https://reactnative.dev/docs/components-and-apis

### \<View>

HTML \<div> 태그와 유사한 기능을 갖는 기본 컴포넌트이다.

***

### \<Text>

HTML \<span> 태그와 유사한 기능을 갖는 기본 컴포넌트이다.

ex. View, Text 컴포넌트

```jsx
const Comp = ({prop1, prop2, ...rest}) => {
  return (
    <View>
      <Text>Hello World!</Text>   
    </View>
  )
};
```

***
### \<TextInput>

HTML의 \<input>과 유사한 컴포넌트이다.

#### 주요 속성
+ autoCapitalize
+ autoCorrect
+ keyboardType: 키보드 타입을 지정함
+ returnkeyType
+ textContentType: 인증코드나 기기등록 이메일이 나타나도록 함
+ secureTextEntry: input type을 특수 문자로 변경함. HTML \<input type='password'>와 유사함.
+ keyboardApperance: 키보드 색 변경 
+ onSubmitEditing: 확인 버튼을 눌렀을 때 발생시킬 이벤트를 지정함.
+ placeholder
+ placeholderTextColor

***
### \<KeyboardAvodingView>

키보드가 화면을 가리는 문제를 해결하기 위해 사용하는 컴포넌트이다.

#### 주요 속성
+ behavior: 'height', 'padding', 'position' 지정 가능

***
### \<Image>

이미지를 삽입할 때 사용하는 컴포넌트이다.

#### 주요 속성
+ source: 저장된 이미지 파일이나 url을 통해 이미지를 받아오기 위해 사용됨
+ resizeMode: 이미지를 어떻게 보여줄 것인지를 결정(ex. contain, cover, strecth, repeat, center)

```jsx
<Image
  source={require(absolutePath)}
  resizeMode={'cover'}
  style={styles.image}
/>
```
***

## 주요 API

### Platform
특정 platform(ex. ios, android)에서만 동작하도록 하는 API이다.

아래와 같이 사용할 수 있다.

```jsx
import { StKeyboardAvoidingView, Platform, View } from "react-native";
<KeyboardAvoidingView behavior={Platform.select({ios: 'padding'})}>
  <View> {comp} </View>
</KeyboardAvoidingView>
```
***
### Keyboard
키보드 관련 이벤트를 관리하는 API이다.

#### 주요 속성
+ dismiss: 키보드가 사라지도록 함
***

## 리액트 네이티브 제공 아이콘
icons.expo.fyl 사이트에서 원하는 아이콘을 찾아서 사용할 수 있다.

## 스타일 적용 방법

CSS와 유사한 방법으로 적용할 수 있다.

+ 1. 적용할 스타일을 정의한다. 방법은 CSS 클래스를 정의하는 것과 유사하다.
```jsx
import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  style1: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
  }
});
```

+ 2. 컴포넌트에 정의한 스타일을 적용한다.

```jsx
<View style={styles.style1}>
  ...
</View>
```

+ 조건부 및 다중 스타일: 람다식과 배열을 이용하여 조건부 스타일 또는 다중 스타일 적용이 가능하다.

```jsx
const Button = ({buttonStyle, ...rest}) => {
    return (
        <Pressable
          style = ({pressed}) => [
            buttonStyle,
            pressed & { backgroundColor: 'orange'},
          ]
        >
          <Text>Button</Text>
        </Pressable>
    );
};
```

## 기타 주요 기능

### 컴포넌트 특별 속성

#### key
리스트로 컴포넌트를 정의할 때 사용되는 unique ID

#### ref

### React Hook

+ useEffect



