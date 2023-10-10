# 리액트 네이티브

## 기본 컴포넌트

+ React Native 기본 컴포넌트 참고 링크: https://reactnative.dev/docs/components-and-apis

### \<View>

HTML \<div> 태그와 유사한 기능을 갖는 기본 컴포넌트이다.

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

람다식과 배열을 이용하여 조건부 스타일과 다중 스타일 적용도 가능하다.

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



