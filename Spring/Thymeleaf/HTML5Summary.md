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

\<head> ... \</head>안에 메타 태그를 작성하고, 브라우저가 화면을 구성할 때 필요한 정보를 저장하거나 개발자가 임의로 정한 정보를 기록하는데 사용한다.

브라우저, OS, 디바이스에 따라 필요한 메타 태그가 모두 다르다. 

\<meta ... />로 작성된다. 다양한 것이 많으므로 필요할 때 구글링해서 쓰도록 하자.


## 6. 이미지

이미지 태그를 통해 HTML 화면에 이미지를 보여줄 수 있다.

+ \<src> ... \</src>: 이미지 파일 경로를 입력한다. width, height, border, align 옵션을 사용할 수 있다.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Image</title>
</head>
<body>
    <img src="meme.jpg"/>
    <hr/>

    <img src="noexist.jpg" alt="존재하지 않는 이미지입니다."/>
    <hr/>

    <img src="meme.jpg" width="100" height="100"/>
    <hr/>

    <img src="meme.jpg" border="3"/>
    <hr/>

    <img src="meme.jpg" align="right"/>
</body>
</html>
```


## 7. 테이블

HTML 문서에서 표를 만들 때 사용하는 태그이다.

과거에는 전체 화면을 만들기 위해 사용하기도 했지만 이제는 순수하게 표를 만들 때만 사용한다.

+ \<table> ... \</table>
+ \<tr> ... \</tr>
+ \<td> ... \</td>: colspan, rowspan 옵션을 사용할 수 있다.

위와 같은 구조로 사용한다. 아래의 html 코드를 참고하자.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Table1</title>
</head>
<body>
  <table border="1" style="width:200px">
    <tr>
      <td>1-1</td>
      <td>1-2</td>
      <td>1-3</td>
    </tr>

    <tr>
      <td>2-1</td>
      <td>2-2</td>
      <td>2-3</td>
    </tr>

    <tr>
      <td>3-1</td>
      <td>3-2</td>
    </tr>
  </table>

  <hr/>

  <table border="1" style="width:200px">
    <tr>
      <th>칸1</th>
      <th>칸2</th>
    </tr>
    <tr>
      <td>1-1</td>
      <td>2-1</td>
    </tr>
  </table>

  <hr/>

  <table border="1" style="width:200px" cellpadding="20" cellspacing="20"> <!-- padding 내부, spacing 외부-->
    <tr>
      <th>칸1</th>
      <th>칸2</th>
      <th>칸3</th>
    </tr>
    <tr>
      <td>1-1</td>
      <td>1-2</td>
      <td>1-3</td>
    </tr>
    <tr>
      <td>2-1</td>
      <td>2-2</td>
      <td>2-3</td>
    </tr>
  </table>

  <hr/>

  <table border="1" style="width:200px">
    <tr>
      <th>칸1</th>
      <th>칸2</th>
      <th>칸3</th>
    </tr>
    <tr>
      <td>1-1</td>
      <td>1-2</td>
      <td>1-3</td>
    </tr>
    <tr>
      <td colspan="2">2-1</td>
      <td rowspan="2">2-2</td>
    </tr>

    <tr>
      <td>3-1</td>
      <td>3-2</td>
    </tr>
  </table>

  <hr/>

  <table border="1" style="width:200px">
    <caption>table Title</caption>
    <thead>
      <tr>
        <th>칸1</th>
        <th>칸2</th>
        <th>칸3</th>
      </tr>
    </thead>
    <tbody></tbody>
      <tr>
        <td>1-1</td>
        <td>1-2</td>
        <td>1-3</td>
      </tr>
      <tr>
        <td>2-1</td>
        <td>2-2</td>
        <td>2-3</td>
      </tr>
    <tfoot>
      <tr>
        <td colspan="3">footer</td>
      </tr>
    </tfoot>
  </table>

</body>
</html>
```


## 8. 리스트

HTML 문서에서 리스트를 만들 때 사용하는 태그이다. 테이블을 생성하는 것과 유사하다.

리스트는 주로 탭, 그리고 메뉴 등을 만들 때 사용한다.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ul 태그의 type</title>
</head>
<body>
  <ul type="circle">
    <li>항목1</li>
    <li>항목2</li>
    <li>항목3</li>
  </ul>

  <hr/>

  <ul type="disc">
    <li>항목1</li>
    <li>항목2</li>
    <li>항목3</li>
  </ul>

  <hr/>
  <ul type="square">
    <li>항목1</li>
    <li>항목2</li>
    <li>항목3</li>
  </ul>

</body>
</html>
```

## 9. 링크

클릭시 사용자가 다른 링크로 들어가 서버로부터 웹 페이지를 받을 수 있도록 한다.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>link1</title>
</head>
<body>
    </hr>
    <a href="page1.html" target="_self">_self</a> <!-- 현재 창에서 page1.html로 리다이렉션 -->
    </hr>
    <a href="page1.html" target="_blank">_blank</a> <!-- 새로운 창으로 page1.html을 띄움 -->
    <a href="#a1">a1으로 스크롤</a>
    </br>
    </br>
    </br>
    </br>
    </br>
    </br>
    </br>
    </br>
    </br>
    </br>
    <p id="a1">p태그 부분입니다.</p>
</body>
</html>
```

## 10. 프레임

HTML 문서 안에 다른 HTML 문서를 포함하고 싶을 때 사용하는 기능이다.

즉, HTML 문서를 함수화해서 사용하는 것과 유사하다.

물론, 서버 쪽의 기능을 사용할 수 있지만 HTML 기능 중 iframe을 통해 사용할 수 있다.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>frame1</title>
</head>
<body>
  <h1>frame1.html입니다</h1>
  <hr/>
  <iframe src="page1.html"></iframe> <!-- page1.html을 해당 부분에 넣는다. -->
  <hr/>
  <h1>frame1.html입니다.</h1>
</body>
</html>
```

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>page1</title>
</head>
<body>
  <h1>page1.html입니다.</h1>
</body>
</html>
```

## 11. form

HTML에서는 필요한 데이터를 사용자로부터 입력받을 수 있다. HTML은 입력받기 위한 화면요소만을 제공한다.

데이터를 입력받은 후, 처리하는 것은 서버쪽 코드에서 담당한다.

HTML에서 입력받을 수 있는 요소들이 무엇이 있는지 알아보도록 하자.
(button, reset, submit)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>form2</title>
</head>
<body>
  <form action="result.html" method="get">
    <input type="text" name="a1" value="aaaa"><br/> <!-- method가 get인 경우는 쿼리 파라미터로 전달함. post인 경우는 body에 담아서 전달함. -->
    <input type="text" name="a2" value="bbbb" placeholder="여기는 디폴트가 bbbb에요"><br/> 
    <input type="text" name="a3" placeholder="여기는 최대 5글자만 가능해요" maxlength="5" style="width:500px"><br/>
    <button type="submit">확인</button>
  </form>
</body>
</html>
```
