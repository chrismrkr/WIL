# 리액트 네이티브

## 주요 컴포넌트

+ React Native 기본 컴포넌트 참고 링크: https://reactnative.dev/docs/components-and-apis

### \<View>

HTML \<div> 태그와 유사한 기능을 갖는 기본 컴포넌트이다.

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
### \<Pressable> 

컴포넌트를 사용자가 터치하여 누를 수 있게 만드는 컴포넌트이다.

```jsx
const Button = ({title, disabled, onPress, isLoading}) => {
  return (
    <Pressable onPress={()=>onPress()}
        style={({pressed}) => 
            [styles.container, 
                !disabled && {backgroundColor: PRIMARY.DEFAULT},
                pressed && {backgroundColor: PRIMARY.DARK}]}
        disabled={disabled}
    >
            {isLoading ? 
            (<ActivityIndicator size={'small'} color={GRAY.DEFAULT}/>) :
            (<Text style={styles.title}>{title}</Text>)}
    </Pressable>
  )
};
```

#### 주요 속성
+ style: 컴포넌트에 스타일을 지정함함
+ onPress: 컴포넌트를 눌렀을 때 발생하는 이벤트를 지정함


***
### \<KeyboardAvodingView>

키보드가 화면을 가리는 문제를 해결하기 위해 사용하는 컴포넌트이다.

주로 \<Pressable>에서 사용한다.

```jsx
import { KeyboardAvoidingView, Pressable, Platform, Keyboard } from "react-native";

const InputView = ({children}) => {
    return (
        <KeyboardAvoidingView style={styles.avoid} behavior={Platform.select({ios: 'padding'})}>
            <Pressable style={styles.avoid} onPress={() => Keyboard.dismiss()}>
                {children}
            </Pressable>
        </KeyboardAvoidingView>
    )
}
```

#### 주요 속성
+ behavior: 'height', 'padding', 'position' 지정. 컴포넌트를 선택했을 때 어떻게 동작할지를 결정함.

***
### \<NavigationContainer>

여러 화면 컴포넌트를 담기 위해서 사용되는 컴포넌트이다. TabController와 유사한 기능을 한다.

주로 App.js에서 선언하여 사용한다.

```jsx
import { NavigationContainer } from '@react-navigation/native';
const App = () => {
  return (
    <NavigationContainer>
      ...
    </NavigationContainer>
  );
};
```

***
### Native Stack Navigator

\<NavigationContainer> 안에서 **화면 이동**을 위해서 사용되는 객체이다. (컴포넌트는 아니다.)

Stack이 LIFO(Last In, First Out) 특성을 갖는 것과 유사하게 Native Stack Navigator도 화면 컴포넌트를 Stack에 쌓아서 관리한다. 

자세한 내용은 reactnavigation.org/docs를 참고할 수 있고, 아래와 같이 선언할 수 있다.

```jsx
import { createNativeStackNavigator } from '@react-native/native-stack';
import { HeaderLeftButton } from 
const stack = createNativeStackNavigator();

const ScreenStack = () => {
  return (
    <Stack.Navigator
        initialRouteName={'SignIn'}
        screenOptions={{...}}
    >
      <Stack.Screen name={'SignIn'}
                    component={SignInScreen}
                    options={{...}}
      />
      <Stack.Screen name={'Main'} component={MainScreen} />
    </Stack.Navigator>
  );
};
```

#### screenOptions 속성
+ contentStyle
+ headTitleAlign: 헤더 정렬 방식을 결정
+ headerTintColor: 헤더의 타이틀 색을 결정
+ headderTitleStyle: 헤더 전체 색을 결정
+ headerLeft: headerLeft에 설정할 컴포넌트를 결정(ex. ```headerLeft: (props) => HeaderLeftButon(),```)

#### options 속성
+ title: Screen title을 결정. name 속성으로도 지정이 가능하나 name은 key이므로 중복될 수 없음.
+ headerTitle: screenOptions의 headerLeft와 유사한 기능(ex. ```headerTitle: (props) => HeaderTitle(),```)

추가로, 화면(컴포넌트)에서 options 설정이 직접 가능하다. 예를 들어, 위 예시의 SignInScreen 컴포넌트에서 직접 options 설정이 가능하다.

#### 화면 이동
+ navigation.push: navigation stack에 쌓으며 화면 이동(즉, 뒤로가기 가능)
+ navigation.navigate: navigation stack에 쌓지 않고 화면 이동

```jsx
const SignInScreen = ({navigation, route}) => {
    const onSubmit = () => {
        navigation.push('List', { param: ...}); // Stack.Screen의 속성인 name(List)를 key로 컴포넌트를 스택에 쌓는다.
    };

    return (
        <View>
          <Button onPress={()=>onSubmit()}/>
        </View>
    );
};
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
  함 

+ useEffect: 컴포넌트가 렌더링되는 시점에 실행되는 Hook
```jsx
const Comp = () => {
  const [var, setVar] = useState('');
  useEffect(() => {
    setVar('init');
  }, [param1, param2]);
}
```
+ useRef: 컴포넌트 내에서 다른 컴포넌트를 참조할 수 있도록 만드는 Hook
```jsx
const Comp = () => {
    const refComp = useRef(null);
    return (
        // onSubmitEditing 이벤트가 발생하면 ref를 통해 다음 컴포넌트로 자동으로 이동함
        <View>
          <Input onSubmitEditing={() => refComp.current.focus()} />
          <Input ref={refComp}/>
        </View>
    
    );
}
```


