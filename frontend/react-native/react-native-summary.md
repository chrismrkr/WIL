# 리액트 네이티브

## React Native 프로젝트 시작

### with Expo

node.js와 device를 이용하여 개발 환경을 구축하고 프로젝트를 시작할 수 있는 방법

+ 1. install expo: npm install -g expo-cli
+ 2. react native 프로젝트 생성: npx create-expo-app [프로젝트명]
+ 3. App 실행: npx expo start
+ 4. expo 로그인: npx expo login

***

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
            [  styles.container, 
                !disabled && {backgroundColor: PRIMARY.DEFAULT},
                pressed && {backgroundColor: PRIMARY.DARK}
            ]}
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
+ style: 컴포넌트에 스타일을 지정함
+ onPress: 컴포넌트를 눌렀을 때 발생하는 이벤트를 지정함
+ disabled: 컴포넌트를 누를 수 없게 만드는 속성. 동적 이벤트와 연결하여 사용함

***

### \<FontAwesome>, \<MaterialCommunityIcons>
아이콘을 배치하기 위해 사용하는 컴포넌트이다. 아래를 import하여 사용해야 한다.

+ FontAwesome: ```import { FontAwesome } from '@expo/vector-icons'```
+ MaterialCommunityIcons: ```import { MaterialCommunityIcons } from '@expo/vector-icons'```

아래와 같이 사용할 수 있다.

```jsx
const IconButton = ({iconName}) => {
  return (
    <Pressable onPress={()=>console.log('press')} >
      <MaterialCommunityIcons
        name = {iconName}
        size = {30}
        color={'black'}
      />
    </Pressable>
  )
}
```

필요한 Icon은 아래 url에서 찾을 수 있다.

+ FontAwesome: https://fontawesome.com/icons
+ MaterialCommunityIcons: https://static.enapter.com/rn/icons/material-community.html


***
### \<KeyboardAvoidingView>

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
+ behavior: 'height', 'padding', 또는 'position' 지정. 컴포넌트를 선택했을 때 어떻게 동작할지를 결정함.

***
### \<NavigationContainer>

여러 화면 컴포넌트를 담기 위해서 사용되는 컴포넌트이다. TabController와 유사한 기능을 한다.

```npm install @react-native/native``` 설치가 필요하다.

주로 App.js에서 선언하여 사용한다.

```jsx
import { NavigationContainer } from '@react-navigation/native';
import ScreenStack from '../navigations/ScreenStack';

const App = () => {
  return (
    <NavigationContainer>
      <ScreenStack />
    </NavigationContainer>
  );
};
```

***
### Native Stack Navigator

\<NavigationContainer> 안에서 **화면 이동**을 위해서 사용되는 객체이다. (컴포넌트는 아니다.)

Stack이 LIFO(Last In, First Out) 특성을 갖는 것과 유사하게 Native Stack Navigator도 화면 컴포넌트를 Stack에 쌓아서 관리한다. 

자세한 내용은 reactnavigation.org/docs를 참고할 수 있고, ```npm install @react-native/native``` 설치가 필요하다.

아래와 같이 선언할 수 있다.

```jsx
import { SignInScreen } from '../screens/SignInScreen';
import { MainScreen } from '../screens/MainScreen';
import { createNativeStackNavigator } from '@react-native/native-stack';
import { HeaderLeftButton } from 
const Stack = createNativeStackNavigator();

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

#### 화면 이동
Navigation Stack 내에서 사용되는 컴포넌트(화면)은 {navigation, route} 객체를 속성으로 전달받는다. 

해당 객체를 이용하여 화면을 이동할 수 있다.

+ navigation.push: navigation stack에 쌓으며 화면 이동(뒤로가기 가능)
+ navigation.navigate: navigation stack에 쌓지 않고 화면 이동(뒤로가기 불가능)

```jsx
const SignInScreen = ({navigation, route}) => {
    const onSubmit = () => {
        navigation.push('List', { param: ...}); // Stack.Screen의 속성인 name(List)를 key로 컴포넌트를 Stack에 쌓는다.
    };

    return (
        <View>
          <Button onPress={()=>onSubmit()}/>
        </View>
    );
};
```

#### screenOptions 속성
+ contentStyle
+ headerTitleAlign: 헤더 정렬 방식을 결정
+ headerTintColor: 헤더 중 타이틀 색을 결정
+ headderTitleStyle: 전체 헤더 색을 결정
+ headerLeft: headerLeft에 설정할 컴포넌트를 결정(ex. ```headerLeft: (props) => HeaderLeftButon(),```)

#### options 속성
+ title: Screen title을 결정. name 속성으로도 지정이 가능하나 name은 key이므로 중복될 수 없음.
+ headerTitle: screenOptions의 headerLeft와 유사한 기능(ex. ```headerTitle: (props) => HeaderTitle(),```)

추가로, 화면(컴포넌트)에서 options 설정이 직접 가능하다. 예를 들어, 위 예시의 SignInScreen 컴포넌트에서 직접 options 설정이 가능하다.

#### Navigator를 활용한 로그인 상태 관리
Navigator와 state를 활용하여 로그인 상태에 따라 화면 컴포넌트를 관리할 수 있다.

```jsx
import { useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';

const App = () => {
  const [user, setUser] = useState(null);
  return (
    <NavigationContainer>
      {user ? <MainStack /> : <AuthStack setUser={()=>setUser()}/>}
    </NavigationContainer>
  );
}
```

***
### \<SafeAreaView>
스마트폰 노치 등에 의해 화면이 가려지는 것을 해결하기 위해 사용하는 컴포넌트이다. 

```jsx
import { SafeAreaView, } from "react-native-safe-area-context";
const comp = () => {
    return (
      <SafeAreaView>
        {...}
      </SafeAreaView>
    );
};
```

또는, useSafeAreaInsets Hook을 사용하여 화면 가리짐을 피하기 위한 padding 값을 알아낼 수 있다.

```jsx
const insets = useSafeAreaInsets();
console.log(insets);
/*  결과
{"bottom": 34, "left": 0, "right": 0, "top": 47}
*/
```

***



## 주요 API

### Platform
특정 platform(ex. ios, android)에서만 동작하도록 하는 API이다.

아래와 같이 사용할 수 있다.

```jsx
import { KeyboardAvoidingView, Platform, View } from "react-native";
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

### 리액트 네이티브 제공 아이콘
icons.expo.fyl 사이트에서 원하는 아이콘을 찾아서 사용할 수 있다.


***
### Context API
자식 컴포넌트에게 파라미터를 전달하기 위해서 state를 사용한다. 하지만, 자식 컴포넌트로 state를 전달할 때마다 렌더링이 다시 발생한다.(prop drilling)

이에 따라 state 추적의 어려움이 있고, 이를 해결하기 위해서 Context API가 존재한다.

Context API는 전역에서 state를 선언하여 여러 컴포넌트에서 이를 공유할 수 있도록 하는 API이다.

사용법은 아래와 같다. **Provider에서 Context state(전역변수)를 생성하고, Consumer에서 이를 사용한다.**

```jsx
// Context Provider
import { createContext, useState, } from "react";
export const Context = createContext(); // Context 생성

export const ProviderComp = ({children}) => { // Context를 제공할 Provider 생성
  const [state, setState] = useState(null);
  return (
    <Context.Provider value={{state, setState}}>
      {children}
    </Context.Provider>
  );
}
```
```jsx
// Context Consumer
const ConsumerComp = () => {
  return (
    <Context.Consumer>
      {({setState}) => {
          return ( ... );
        }
      }
    </Context.Consumer/>
  );
};
```

하지만, 매번 Consumer를 통해 context를 받아오는 것은 번거로우므로 useContext Hook을 이용할 수 있다.

```jsx
const ConsumerComp = () => {
  const {setState} = useContext(Context);
  return (
    ...
  );
}
```


#### Context API를 활용한 로그인 상태 관리

state 대신 context 전역변수를 통해 로그인 상태를 관리할 수 있다.

```jsx
// UserContext.js
import { createContext, } from "react";
export const UserContext = createContext();

const UserProvider = ({children}) => {
  const [user, setUser] = useState(null);
  return (
    <UserContext.Provider value={{user, setUser}}>
      {children}
    </UserContext>
  );
};
export default UserProvider;
```
```jsx
// App.js
const App = () => {
  return (
    <UserProvider>
      <Navigation />
    </UserProvider>
  );
}
```
```jsx
// Navigation.js
const Navigation = () => {
  const {user} = useContext(UserContext);
  return (
    <NavigationContainer>
        {user ? <MainStack/> : <AuthStack/>}
    </NavigationContainer>
  )
};
```
```jsx
const AuthStack = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen name={'SignIn'} component={SignInScreen}>
      </Stack.Screen>
    </Stack.Navigator>
  )
};
```
```jsx
const SignInScreen = ({navigation, route}) => {
  const [setUser] = useContext(UserContext);
  const onSubmit = ({setUser}) => {
    const data = await login('/...');
    setUser(date);
  }
  return (
    ...
  );
};
```

***


## 스타일 적용 방법
CSS와 유사한 방법으로 적용할 수 있다.

+ 적용할 스타일을 정의한다. 방법은 CSS 클래스를 정의하는 것과 유사하다.
```jsx
import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  style1: {
    flexDirection: 'row',
    justifyContent: 'space-evenly',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
  }});
```

+ 스타일을 컴포넌트에 적용한다. 배열, 람다식, 조건문을 사용할 수 있다.
```jsx
<View style={[styles.style1, {backgroundColor: '#047857'}, ]} >
  {children}
</View>
<Pressable
    style={({pressed}) => 
            [styles.container,
             {backgroundColor: PRIMARY.DEFAULT},
             pressed && {backgroundColor: PRIMARY.DARK}, ]
          }
> </Pressable>
```


## 리액트 Hook

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

+ useSafeAreaInsets: 화면 가려짐을 피하기 위한 padding이 얼마인지 알 수 있는 Hook
```jsx
import { useSafeAreaInsets } from "react-native-safe-area-context";

const Comp = () => {
    const insets = useSafeAreaInsets();
    console.log(insets);
};
``` 

+ useContext: 전역 state를 사용하기 위한 Hook


***

## 리액트 프로그래밍
