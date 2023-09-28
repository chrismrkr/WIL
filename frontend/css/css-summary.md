# CSS

HTML 문서를 꾸미기 위해 존재하는 웹 표준 기술

## 1. CSS의 기본

### 1.1 웹 문서에 디자인 입히기

CSS는 웹 문서에 디자인을 적용하는 기술이다.

### 1.2 스타일과 스타일 시트

스타일의 기본 형식은 아래와 같다. 

```css
선택자 { 속성1: 속성값1; 속성2: 속성값2; }
```

#### 1.2.1 인라인 스타일

body의 태그에 직접 스타일을 적용하는 방법이다.

```html
<p style="color: blue;">동해물과 백두산이 마르고 닳도록.</p>
```

#### 1.2.2 내부 스타일

같은 html 파일 안에 선택자로 정의한 후 적용하는 방법이다.

```html
<head>
  <style>
    h1: {
      padding: 10px;
      color: #fff;
      background-color: #222;
    }
  </style>
</head>
```

#### 1.2.3 외부 스타일

같은 프로젝트에 독립적으로 .css 파일을 만든 후, 이를 html 파일에서 import해서 사용하는 방식이다.

```html
  <link rel="stylesheet" href="3css/style.css">
```

### 1.3 CSS 기본 선택자

```css
* { 속성1: 속성값1; ... } /* 전체 선택자*/
```

일반적으로, 전체 선택자를 통하여 아래와 같이 css를 작성한 후 스타일링을 시작한다.
```css
* { box-sizing: border-box; }
html, body { margin: 0; padding: 0; }
```

```css
태그명 { 스타일 규칙 }
```

```css
.클래스명 { 스타일 규칙; }
```

```css
#아이디명 { 스타일 규칙; }
```
### 1.4 Cascading

둘 이상의 스타일을 적용할 때, 우선순위에 따라 적용할 스타일을 결정한다. 이것이 캐스케이딩이다.

+ 자식에게 상속되는 속성: color, font-family, font-size
+ 자식에게 상속되지 않는 속성: padding, margin

***
## 2. 기본 스타일 속성

### 2.1 텍스트 관련 속성

이름으로 속성 유추가 가능하다.

```css
  font-family: <글꼴 이름 1>, <글꼴 이름 2>, ...
```

```css
  font-size: <글자 크기>;
```

```css
  font-style: normal | italic | oblique;
```

```css
@font-face { /* 웹 폰트를 사용하는 방법*/
  font-family: myFontFamily; /* 글꼴 이름을 지정 */
  src: url('.....') format('...');
}

.myClass {
  font-family: 'myFontFamily';
}
```

```css
  color: <색상>; /* 16진수, hsl, rgba 등으로 표현 가능*/
```

```css
  text-align: [start | end | center | justify];
```
```css
  line-height: [px | ..%];
```

```css
  text-decoration: [none | underline | overline | linethrough ];
```

```css
  text-transform: [capitalize | uppercase | lowercase];
```

```css
  overflow: [visible | hidden | scroll | auto]; /* 텍스트가 박스를 넘어갈 때 어떻게 처리할지를 결정 */
```

```css
  text-overflow: ..; /* 텍스트가 줄바꿈 없이 가로로 넘쳤을 때의 대응 방법을 결정*/
```

### 2.2 목록 스타일

아래의 태그로 스타일을 지정한다.

```css
  list-style-type: [none|decimal|...];
  list-style-image: ...; /* 불릿 모양 지정 가능*/
  list-style: ...(단축 속성 사용 가능);
```

### 2.3 기타 스타일

```css
  caption-side: top | bottom; /* 제목 위치를 지정하는 CSS*/
```
```css
  border: 1px solid black; /* 테두리 관련 CSS */
```
```css
/* 셀 사이 여백 지정 속성: 한 셀의 수평거리와 수직거리를 지정함 */
  border-spacing: 수평거리 수직거리; 
```
```css
/* 테두리 합성 속성 */
  border-collpase: collapse | seperate;
```

### 2.4 단위

#### 절대단위
+ px: 픽셀
+ pt: 인쇄시 사용하기 위한 단위

#### 상대단위
+ \%: 부모 컴포넌트 속성에 비례하여 적용
+ em: 자신 컴포넌트 font-size에 비례하여 적용(1em = font-size * 16)
+ rem: 최상위 html 컴포넌트 font-size에 비례하여 적용(1rem = font-size * 16) 
+ vw: 10vw = width * 0.1
+ vh: 10hw = height * 0.1

***

## 3. CSS 레이아웃

### 3.1 블록 요소

#### 블록 요소와 인라인 요소

+ 블록 요소: 한 줄을 차지하는 width: 100%의 요소. (ex. div)

+ 인라인 요소: 한 줄을 차지하지 않음. (ex. p, li)

#### 블록 요소 관련 속성

+ display: 블록 요소 타입을 결정함(ex. block, inline, flex, grid)

+ width: 너비를 지정함. px이나 %로 지정한다.

+ height: 높이를 지정함. px이나 %로 지정한다.

+ box-sizing: 블록 요소의 범위를 결정함. 

boxing-size을 디폴트로 하거나 content-box로 하면 width, height는 **컨텐츠 영역의 크기**이다.

boxing-size을 border-box로 지정하면 width와 height는 **border, padding, 컨텐츠 영역을 합한 영역 크기**이다.

+ border 관련 속성

```css
border-width: ...; /* 테두리 두께를 지정한다. */
border-style: ...; /* 테두리 스타일을 지정한다. */
border-color: ...; /* 테두리 색깔을 지정한다. */
border: width style color; /* border 단축 속성 */
border-radius: ...; /* 둥근 테두리를 지정할 수 있다. 꼭짓점마다 다르게 지정할 수 있다. */
/* example*/
border: 1px solid red;
```

+ position 관련 속성

```css
position: static; /* 컴포넌트의 원래 흐름에 맞게 배치(default) */
position: relative; /* relative가 적용된 컴포넌트 다음으로 등장하는 컴포넌트가 위치를 임의로 조정할 수 있게 함 */
position: absolute; /* 특정 컴포넌트의 절대 좌표를 기준으로 위치를 조절하도록 함 */
position: fixed; /* viewport를 기준으로 위치 설정(스크롤과 무관) */
position: sticky; /* 원래 위치(static)에 있다가 스크롤이 움직이면 fixed처럼 동작함 */
```

예를 들어, .parent를 기준으로 .child를 배치하려면 아래와 같이 CSS를 작성하면 된다.
```css
.parent {
  position: relative;
}
.child {
  position: absolute;
  top: 100px;
  left: 100px;
}
```

+ overflow: 내용물이 요소를 넘었을 때 적용되는 속성
```css
overflow: hidden;
```

+ 여백 관련 속성

margin은 두 박스 모델 사이의 여백을 의미하고, padding 테두리와 컨텐츠 사이의 여백을 의미한다.

```css
  margin: ...; /* px로 조절하고, top -> right -> bottom -> left를 사용해서 특정 방향만 지정할 수 있다.*/
  margin: 10px auto /* 상단과 하단에 10px 마진을 주고, 좌우를 자동(auto)으로 대칭을 맞춘다. */
```

### 3.2 float 레이아웃

float 레이아웃이란 컴포넌트를 좌측 또는 우측에 띄우는 레이아웃이다.

```css
float: left;
float: right;
```

다음 컴포넌트가 이전 컴포넌트의 float 속성에 영향을 받으므로 주의해야 한다.

float 속성을 해제하려면 clear 속성을 사용한다.
```css
clear: left;
clear: right;
```

다음 컴포넌트에 필요한 경우 매번 clear 속성을 지정하는 것을 번거롭다.

그러므로, 아래와 같이 .clearfix 클래스를 생성하여, 필요한 경우 사용하도록 한다.
```css
.clearfix { clear: both; }
```
```html
  <div class="float-left">left</div>
  <div class="clearfix"></div>
  <div> i'm free </div>
```

### 3.3 flex 레이아웃

```display: flex``` 속성을 이용하여 레이아웃을 만드는 방법이다.

viewport는 device마다 다르므로 이에 유동적으로 대응하여 레이아웃을 만드는 방법이다.

+ flex-direction: 하위 컴포넌트를 어느 방향으로 배치할지 결정하는 속성
```css
flex-direction: row | column;
```

+ justify-content: 하위 컴포넌트를 어떻게 정렬할 것인지를 결정하는 속성
```css
justify-content: flex-start | flex-end | center | space-between | space-around | space-even;
```

+ align-items : 하위 컴포넌트를 배치 방향의 수직 기준으로 어떻게 정렬할 것인지 결정하는 속성
```css
align-items: flex-start | flex-end | center | space-between | space-around | space-even;
```

+ flex-wrap: 정렬된 요소들의 총 width가 부모 width보다 큰 경우에 다음 줄에 이어서 나열하는 기능
```css
flex-wrap: wrap;
```

+ align-content: 두 줄 이상의 align-items를 어떻게 배치할 것인지를 결정하는 속성
```css
align-content: flex-start | flex-end | center | space-between | space-around | space-even;
```

자세한 flex 관련 속성은 MDM을 참고하면 된다.

### 3.4 grid 레이아웃

```display: grid;``` 속성을 이용하여 레이아웃을 만드는 방법이다.

grid 레이아웃은 큰 규모의 레이아웃이 명확히 결정되었을 때 사용하는 방법이다.

+ grid-template-rows: grid-container의 행의 개수와 크기를 결정하는 속성
```css
grid-template-rows: 1fr 2fr 1fr 1fr; /* 1:2:1:1 비율로 총 4개의 행을 가진 그리드 생성 */
grid-template-rows: 200px 1fr 2fr; /* 200px, 1:2 비율로 총 3개의 행을 가진 그리드 생성 */ 
grid-template-rows: 200px repeat(2, 1fr); /* 200px, 1:1 비율로 총 3개의 행을 가진 그리드 생성 */
```

+ grid-template-columns: grid-container의 열의 개수와 크기를 결정하는 속성
```css
grid-template-columns: ...; /* grid-template-rows와 동일하게 지정할 수 있음 */
```

+ grid-row, grid-col : grid-item의 크기를 지정하는 속성
```css
/* (1행-3행), (2열-3열)을 1칸의 grid-item으로 설정한다. */
grid-row: 1/3;
grid-col: 2/3;
```

grid 레이아웃 예시는 아래와 같다. item1은 (2행-3행), (1열-3열)에 배치된다.

```css
.container {
    display: grid;
    grid-template-columns: repeat(2, 1fr 2fr);
    grid-template-rows: repeat(4, 100px);
    grid-gap: 5px;
}
.item1 {
  grid-row: 2/3;
  grid-column: 1/3;
}
```

***

## 4. 배경 관련 속성

+ **background**: 단축 속성
```css
background: color image repeat position size attachment;
```
+ background-color: 배경색을 지정하는 속성 

+ background-clip: 배경 범위를 지정하는 속성
```css
background-clip: border-box; /* 외곽 테두리까지 적용 */
background-clip: padding-box; /* 패딩 범위까지 적용 */
background-clip: content-box; /* 패딩을 제외한 컨텐츠 영역에만 적용 */
```

+ background-image: 배경 이미지를 삽입하는 속성
```css
background-image: url(...);
```

+ background-repeat: 배경 이미지 반복
```css
background-repeat: repeat; /* repeat, repeat-x, repeat-y, no-repeat 존재 */
```

+ background-position: 배경 이미지 위치 조절

이미지의 수평 또는 수직 위치를 지정한다. 

left(top), center, right(bottom), <백분율>, <길이 값>으로 지정할 수 있다.
```css
background-position: <수평위치> <수직위치>;
```

+ background-origin: 배경 이미지 적용 범위 조정

centent-box, border-box, padding-box 총 3가지가 있다.

+ background-attachment: 배경 이미지 고정

scroll, fixed, local 3가지 속성이 있다. scroll은 이미지가 함께 스크롤되고, fixed는 스크롤 시 이미지는 고정된다. 

+ background-size: 배경 이미지 크기 조절

배경 이미지가 필요보다 작거나 클 경우 조절할 수 있다.

auto, contain, cover, <크기>, <백분율> 총 5가지 방법이 있다.

contain은 요소 안에 배경 이미지가 다 들어오도록 하고, cover는 배경 이미지로 요소를 모두 덮도록 한다.

+ object-fit: \<img> 태그를 통해 설정한 배경의 속성을 변경할 때 사용함.
```css
object-fit: cover; /* <img> 배경을 컴포넌트에 꽉 채운다. */
```


***

## 5. CSS 고급 선택자

항상 id와 class를 지정하고 기억하여 스타일을 적용하는 것 외 다른 방법도 있다.

+ **사용자 동작에 반응하는 가상 클래스**

1. :link; 방문하지 않은 링크에 스타일 적용
2. :visited; 방문한 링크에 스타일 적용
3. :hover; 마우스 포인터를 올려놓으면 스타일 적용
4. :active; 웹 요소를 활성화 했을 때 스타일 적용(ex. 클릭)
5. :focus; 웹 요소에 초점이 맞추어져 있을 때 스타일 적용

**link -> visited -> hover -> active 순서로 적용하지 않으면 제대로 작동하지 않음에 주의하자!**
(LoVe HAte로 외우자)

+ **가상 요소 선택자**

html 컴포넌트에 또 다른 컴포넌트가 존재하는 것 같은 효과를 준다.

예를 들어, box1 클래스를 가진 컴포넌트의 끝에 새로운 컴포넌트가 있도록 하려면 아래와 같이 만들면 된다.
```css
box1::after {
  background: rgba(0,0,0,0.2);
}
```

가상 요소 선택자 예시는 아래와 같다.
```
::before
::after
::first-letter
::first-line
::selection
::slotted()
::cue
::cue-region
```


+ 하위 선택자: 상위 요소에 포함된 모든 하위 요소를 지정하는 선택자이다.
```css
  body p { ... } / * <body> 태그 안의 모든 <p> 태그에 스타일을 적용한다. */
```

+ 자식 선택자: 모든 하위 요소가 아닌 바로 아래의 자식 요소만을 지정하는 선택자이다.
```css
  body > p { ... } /* <body> 태그 바로 밑의 <p> 태그에만 스타일을 적용한다. */
```


+ 인접형제 선택자: 형제 요소 중 첫번째 동생 요소만을 선택하는 것을 의미한다.
```css
  h1 + p { ... }; /* <h1> 태그 바로 뒤에 나오는 <p> 태그에만 스타일을 적용한다. */
```

+ 형제 선택자: 인접형제 선택자와 달리 모든 형제 요소를 선택하는 것을 의미한다.

```css
  h1 ~ p { ... }; /* <h1> 태그와 형제인 모든 <p> 태그에 스타일을 적용한다. */
```

+ \[속성] 선택자
```css
a[href] { ... } /* <a> 태그 중, href 속성이 있는 요소에 스타일을 적용한다. */
```

+ \[속성 = 속성값] 선택자
```css
a[target = "blank"] { ... } /* 속성이 속성값과 일치하는 <a> 태그에만 스타일을 적용한다. */
```

+ \[속성 ~= 값] 선택자
```css
[class ~= button] { ... } /* 여러 속성값 중에서 해당 속성값이 포함된 요소를 선택한다. */
```

+ \[속성 |= 값] 선택자
```css
a[title |= us] { ... } /* 특정 속성 값이 포함된 <a> 태그에 스타일을 적용한다. */
```

\[속성 ~= 값]]과 다른 점은 ~= 에서는 하이픈(-)를 인식하지 못한다.

+ \[속성 ^= 값]
```css
a[title ^= "eng"] { ... } /* title 속성 중에 값이 "eng"로 시작하는 <a> 태그에 스타일을 적용한다. */
```

+ \[속성 $= 값]
```css
[href $= xls] { ... } /* href 속성 중, 값이 xls로 끝나는 태그들에 스타일을 적용한다. */
```

+ \[속성 \*= 값] 
```css
[href *= w3] { ... } /* href 속성 중, 값에 "w3"가 포함된 태그들에 스타일을 적용한다. */
```

***

## 6. 트랜지션과 애니메이션

CSS는 웹 요소에 시각적인 효과를 추가하는 기능이었다.

애니메이션을 통해 자바스크립트 없이도 웹 사이트의 메뉴를 부드럽게 열고, 또 웹 요소를 움직일 수 있다.

### 6.1 transition

특정 시점에 컴포넌트에 애니메이션 효과를 부여하기 위한 기능이다.

+ transition-property: 어떤 속성에 transition 효과를 적용할지를 결정
+ transition-duration: transition이 실행되는 설정(단위: s, ms)
+ transition-timing-function: 주로 ease-in-out을 적용
+ transition-delay: 일정 시간 후에 transition을 실행하도록 설정

아래와 같은 단축 속성도 존재한다.

```css
transition: [property] [duration] [timing-function] [delay];
```

예를 들어, 아래와 같이 transition 효과를 줄 수 있다.

```css
.box1:hover {
/* .box1:hover시 1초동안 ease-in-out으로 width를 1000px로 변경 */
  width: 1000px;
  transition: width 1s ease-in-out 0s;
}
```

### 6.2 transform

컴포넌트 위치 및 모양 등을 변경하기 위한 속성이다

+ translate(x, y): 컴포넌트의 위치를 x-y축 기준으로 이동시키는 속성 (단위: px, %, em)
+ scale(x, y): 컴포넌트를 x-y축 방향으로 확대 및 축소하는 속성 (단위: px, %, em)
+ skew(x, y): 컴포넌트를 x-y축 방향으로 기울이는 속성 (단위: deg)
+ rotate(deg): 컴포넌트를 회전시키는 속성 (단위: deg)

예를 들어, 아래와 같이 transition과 함께 사용할 수 있다.
```css
.box1:hover {
  transform: translate(100px, 100px);
  transition: transform 1s ease-in-out 0s;
}
```
translateX(x), translateY(y), scale(n%) 등의 방법으로 사용할 수 있다.


### 6.3 Animation

트랜지션만으로 애니메이션 효과를 만들기 어려울 때 사용하는 방법이다.

예를 들어, 특정 시점 없이 컴포넌트에 애니메이션을 적용하고 싶거나 복잡한 애니메이션을 적용이 필요할 때 사용한다.

애니메이션을 @keyframes로 정의하고, animation-name을 통해 적용한다.

속성은 아래와 같다.

+ animation-timing-function: 시간 곡선 조절
+ animation-iteration-count: 반복 횟수를 지정한다.(infinite도 가능)
+ animation-direction: from -> to로 진행하는데 진행방향을 바꾼다.

transition과 마찬가지로 단축 속성도 존재한다.
```css
animation: [name] [duration] [timing-function] [delay] [iteration-count] [direction]
```


**Example**
```css
@keyframes shape-from-to {
  from {
    border: 1px solid transparent;
  }
  to {
    border: 1px solid #000;
    border-radius: 50%;
  }
}
@keyframes shape-percentage {
  0% {
    border: 1px solid transparent;
  }
  50% {
    border: 1.5px solid #000;
    border-radius: 40%;
  }
  100% {
    border: 2px solid #000;
    border-radius: 50%;
  }
}

#box1 {
  border: 1px solid transparent;
  animation-name: shape;
  animation-duration: 3s;
}
```

***

## 7. 반응형 웹과 미디어 쿼리

viewport에 맞추어 사이트의 레이아웃을 자연스럽게 바꿔서 보여주는 것을 반응형 웹 디자인이라고 한다. 

### 7.1 반응형 웹

viewport란 디바이스 화면에서 실제로 표시되는 영역을 의미한다. 

웹키트를 기반으로 한 모바일 브라우저(ex. 사파리, 크롬, 엣지 등등)은 기본 뷰포트가 980px이다.

그러므로, 스마트폰 뷰포트가 320px 기준으로 개발하면 너비가 맞지 않는다. 그러므로, 뷰 포트를 커스터마이징 해야한다.

기본형은 아래와 같다.

```html
 <meta name="viewport" content= "속성1=값1, 속성2=값2, ...">
```

뷰포트의 속성은 아래와 같다.
+ width: device-width 또는 vw 단위 사용
+ height: device-height 또는 vh 단위 사용
+ user-scalable(확대, 축소 가능여부): yes 또는 no
+ initial-scale(초기 확대, 축소 값): 1~10

***

### 7.2 미디어 쿼리

미디어 쿼리란 기기의 **해상도에 따라 서로 다른 스타일 시트를 적용**하는 것을 의미한다.

미디어 쿼리의 기본형은 아래와 같다.

```css
@media [only | not] 미디어_유형 [and 조건]* 
```

예를 들어, 아래와 같이 미디어 쿼리를 작성한다.

```css
@media screen and (min-device-width: 375px) and (min-device-height: 812px) {
  ...
}

@media screen and (min-device-width: 812px) and (orientation: landscape) {
  ...
}
```

아이폰 10S의 경우, 너비는 375px, 높이는 812px이다. 세로모드와 가로모드일 때, 화면 스타일을 달리해야 한다.

위와 같이 미디어 쿼리로 구분하여 스타일을 적용할 수 있다.

미디어_유형과 and 조건에 들어갈 수 있는 속성은 다양하게 존재한다. 알아서 확인해서 쓰자.

외부에서 css를 작성한 후, 아래와 같이 미디어 쿼리를 사용해서 적용할 수 있다.

```css
<link rel="stylesheet" media="미디어 쿼리" href="css 파일 경로">
@import url("css/table.css") only screen and (mid-width: 321px) and (max-width: 768px);
```

가급적이면 <link ... >를 쓰자. IE에서는 @import 보다 javascript를 먼저 받기 때문에 에러가 날 수 있다. 


