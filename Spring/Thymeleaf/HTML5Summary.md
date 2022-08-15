# HTML/HTML5 기초 


## 1. HTML 문서 기본 구조

**<태그명>: HTML에서는 태그명을 통해서 처리할 작업을 지정한다.** 

+ \<!DOCTYPE html>: 반드시 HTML5는 이것으로 시작해야 한다.
+ \<html> .. \</html>: html 문서는 html 태그 사이에 작성해야 한다.
+ \<head> .. \</head>: 문서의 정보를 작성하는 부분. 브라우저가 필요한 정보를 세팅하는 부분
+ \<body> .. \</body>: 이곳에 작성된 html 태그를 통해 화면을 구성
+ \<meta charset=utf-8/>: 문자열 처리 방식 지정. utf-8이 다국어를 지원함
+ \<title> .. \</title>: 문서의 타이틀 부분을 지정


## 2. 기본 태그

+ \<hr/>: 가로 방향으로 선을 긋는다. 
+ \<!-- --\>: 주석 처리할 때 사용한다. 여러 줄에 대해서 주석 처리 가능
+ \<h1> ~ \<h6>: 제목을 표시하는 용도로 사용된다. h1에서 h6으로 작아질수록 크기가 작아진다.
+ \<p> .. \<\p>: 문단을 나누는 용도로 사용한다.
+ \<br/>: 줄 바꿈 용도로 사용한다.
+ \<pre> .. \</pre>: 입력한 내용을 그대로 출력하도록 함. 공백, 개행 등을 모두 인식함.

## 3. 주요 속성

웹 브라우저는 HTML 태그 이름을 파악하여 화면을 구성한다.

만약 개발자가 가변적으로 지정해야할 값이 있다면, 속성을 통해 값을 설정한다.

브라우저는 속성을 통해 화면을 구성한다.

+ \<p id=".."> ... \<\p>: 특정 태그를 지칭할 때 사용한다.
+ \<p title="title 속성의 문자열">title 속성\</p>: 태그에 마우스를 올릴 때 표시할 문자열을 지정한다.
+ \<p class = "c1">c1 클래스 적용\</p>: 스타일 시트 속성을 태그에 적용한다
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>주요 속성들</title>
    <style>
      .c1 {
          color : red;
          background-color : black;
      }
    </style>
</head>
<body>
  <p class = "c1">c1 클래스 적용</p>
</body>
</html>
```
+ \<p style="color:red; background-color=black">문자열1\</p>: 스타일 시트 속성을 태그에 적용할 수 있는 다른 방법이다.
+ \<p align="..."> ... \</p>: ... 문자열을 left, center, 또는 right으로 정렬(align)한다.


## 4. 문자열 꾸미기

외울 필요는 없고 문서 작성 시 참고하도록 하자.

+ \<b> ... \</b>: 굵게 
+ \<i> ... \</i>: 이탤릭
+ \<ins> ... \</ins>: 밑줄
+ \<del> ... \</del>: 삭제 문자열
+ <del>\<strike> ... \</del>: 삭제 문자열(HTML5에서는 지원하지 않음)</strike>
+ \<sup> ... \</sup>: 윗 첨자
+ \<sub> ... \</sub>: 아랫 첨자
+ \<small> ... \</small>: 작은 문자열
+ \<em> ... \</em>: 문자열 강조
+ \<mark> ... \</mark>: 표시 문자열     
+ \<strong> ... \</strong>: 강하게 표시하기
+ \<abbr> ... \</abbr>: 약어
+ \<p>\<dfn>특별한 문자열\</dfn>\</p>
+ \<blockquote>인용구\</blockquote>
+ \<p><q>인용구\</q>\</p>
+ \<cite>텍스트 인용구\</cite>
+ \<code> .. \</code>: 프로그램 코드
+ \<kbd> .. \</kdb>: 키보드 문자열
+ \<var> .. \</var>: 태그 안에서 변수를 작성할 때 사용
+ \<samp> .. \</samp>: 프로그램 코드에서 출력문
+ \<address> .. \</address>: 주소

+ \<div> ... \</div>: **HTML 문서 내에서의 영역**을 만들 때 사용한다.
+ \<span> ... \</span>: **문자열 내부의 영역**을 만들 때 사용한다.
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>영역</title>
<style>
  .c1 {
      color : red;
      background-color : black;
  }
</style>
</head>
<body>
  동해물과 백두산이 <div>마르고 닳도록</div> 하느님이 보우하사 우리나라 만세

  <hr/>

  동해물과 백두산이 <span class="c1">마르고 닳도록</span> 하느님이 보우하사 우리나라 만세
</body>
</html>
```

## 5. 메타 태그

메타 태그는 웹 문서의 정보를 설정하는 태그이다.

\<head> ... \</head>안에 메타 태그를 작성하고, 브라우저가 화면을 구성할 때 필요한 정보를 저장하거나 개발자가 임의로 정한 정보를 기록하는데 사용한다
