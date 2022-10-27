# HTML 내용 정리 

html은 tag를 통해 웹 문서를 제작하는 언어이다.

## 1. HTML 기본 문서 만들기 

### 1.1 HTML 기본 구조 파악하기

HTML은 \<head>, \<body>, 그리고 \<tail> 3가지 영역으로 구성된다.

전체적인 구조는 아래와 같다.

```html
<!DOCTYPE html>
<html>
  <head> </head>
  <body> </body>
  <tail> </tail>
</html>
```

이제부터 하나씩 뜯어서 살펴보도록 하자.

#### 1.1.1 \<html> 태그

웹 문서가 시작한다는 것을 의미하는 태그이다. 모든 태그는 여기에 포함된다.

#### 1.1.2 \<head> 태그

웹 브라우저가 알아야할 내용을 적는 부분이다. \<meta>와 \<title> 태그가 일반적으로 포함된다.

\<meta> 태그는 웹 문서와 관련된 정보를 지정할 때 사용한다. 예를 들어, 인코딩 타입을 지정할 수 있다.
```html
<head>
   <meta charset="UTF-8">
</head>
```

그 이외에도 키워드, 웹 문서에 대한 설명, 웹 문서의 소유자 및 제작자를 설정할 수 있다.

\<title> 태그는 웹 문서의 제목을 지정한다.

#### 1.1.3 \<body> 태그

실제 웹 브라우저에 표시할 내용을 입력한다. 자세한 방법은 이후에 설명하도록 한다.

***

### 1.2 웹 문서 구조를 만드는 시맨틱 태그

HTML 태그는 시맨틱 태그로 즉, 태그 자체로 의미가 통하는 이라는 뜻을 가진 태그이다.

시맨틱 태그는 소스 코드의 태그만 확인해도 어느 부분인지 한번에 알 수 있기 때문에 필요하다.

**즉, 특별한 기능은 없지만, HTML 문서 자체의 가독성을 높이기 위해서 필요하다는 것을 의미한다.**

여러가지 시맨틱 태그에 대해서 알아보도록 한다.

#### 1.2.1 \<header> 태그

웹 페이지의 최상단 부분을 지칭할 때 사용한다. \<head> 태그와 헷갈리지 않도록 하자.

#### 1.2.2 \<nav> 태그

navigation의 약어로 웹 문서 안에서 다른 위치 또는 링크로 연결하기 위해 사용하는 시맨틱 태그이다.

보통 top Menu를 생성할 때 사용한다.

#### 1.2.3 \<article> 태그

블로그의 포스트처럼 독립된 컨텐츠를 나타낼 때 사용한다.

#### 1.2.4 \<section> 태그

몇개의 콘텐츠를 묶는 용도로 사용한다. 주로, id와 class를 적용하기 위해 사용한다.

#### 1.2.5 \<footer>

웹 문서에서 맨 아래쪽에 있는 footer 영역을 만든다.

#### 1.2.6 \<div>

id와 class 속성을 사용해서 문서 구조를 만들거나 스타일을 적용할 때 사용하는 태그이다.

***
***

## 2. 웹 문서에서 내용을 입력하는 다양한 방법

기본 텍스트 입력하는 것 외에 여러가지 추가 기능이 있다.

**텍스트 자체를 강조하는 방법, 리스트를 입력하는 방법, 표를 입력하는 방법, 다양한 멀티미디어를 입력하는 방법이 있다.**

### 2.1 일반 텍스트 관련 태그

#### 2.1.1 \<hn> 태그

n은 1부터 6까지 사용할 수 있다. 다른 텍스트보다 크고 진하게 표시한다.

#### 2.1.2 \<p> 태그와 \<br> 태그

\<br>과 \<p> 모두 줄을 바꾼다는 점에서 공통적이다. 그러나, \<p>는 줄을 바꿈과 동시에 단락을 구분하므로 스타일(CSS) 적용에 유리하다.

#### 2.1.3 \<blockquote> 태그

다른 텍스트보다 안으로 들여쓰도록 만들 때 사용하는 태그이다.

#### 2.1.4 \<strong>, \<b> 태그

글자를 굵게 처리할 때 사용한다. 둘의 차이는 \<strong>은 음성인식을 지원한다는 점이다.

#### 2.1.5 \<em>, \<i>

기울임체를 입력할 때 사용한다.

그 이외에도 여러가지가 존재한다. 필요시 참고하도록 하자.

***

### 2.2 리스트 만드는 방법

#### 2.2.1 순서가 있는 리스트

인덱스가 있는 리스트를 의미한다. type과 start 속성을 통해 인덱스의 시작 번호와 형태를 변경할 수 있다.

\<ol>과 \<li> 태그를 사용해서 만들 수 있다.

#### 2.2.2 순서가 없는 리스트

인덱스가 없는 리스트를 의미한다.

\<ul>과 \<li> 태그를 사용해서 만들 수 있다.


#### 2.2.3 설명 목록

\<dl>, \<dt>, \<dd> 태그로 만들 수 있다.

작성 방법은 아래와 같다.

```html
<dl>
  <dt> 프로그래밍 언어 </dt>
  <dd> C </dd>
  <dd> C++ </dd>
  <dd> Java </dd>
  <dd> Python </dd>
  <dd> Ruby </dd>
</dl>
```

***

### 2.3 표 만들기

표는 행, 열, 그리고 셀로 구분된다. 셀 간에는 병합이 가능하므로 표를 만들 때는 항상 손으로 그림을 그려가며 확인하는 것이 안전하다.

#### 2.3.1 \<table> 태그, \<caption> 태그

\<caption>은 표 상단에 제목을 붙인다. \<table> 태그는 표를 만들 때 사용한다. 

#### 2.3.2 \<tr>, \<td>, \<th> 태그

\<tr> 태그는 행을 만들고, \<td> 행 안에 있는 셀을 만든다. 즉, \<td>는 열이라고 생각하면 된다.

\<th>는 행들의 제목 행(?)을 만든다. 작성은 아래와 같이 한다.

```html
<table>
  <tr>
    <th> 이름 </th>
    <th> 성적 </th>
  </tr>
  <tr> 
    <td>kim</td>
    <td>100</td>
  </tr>
  <tr> 
    <td>lee</td>
    <td>88</td>
  </tr>
</table>
```

#### 2.3.3 표의 구조를 지정하는 태그

태그로는 \<thead>, \<tbody>, \<tfoot> 3가지가 있다. 각 제목, 본문, 요약을 나타낸다. HTML 코드의 가독성을 높이기 위해 사용된다.
