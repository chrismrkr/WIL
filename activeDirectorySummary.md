# Active Directory

Active Directory(이하 AD)란 MS가 윈도우 환경에서 사용하기 위해 제작한 디렉토리 서비스이다.

디렉토리 서비스란 사용자와 리소스에 대한 정보를 중앙(Admin)에서 조직하고 관리하는 소프트웨어이다.

관리자 계정(admin)에서 사용자 인증(Authentication), 그리고 권한(Authorization)을 관리하는 방법으로, 중앙에서 통제할 수 있다는 점에서 장점이 있다.

물론, 관리를 위한 별도의 콘솔이 존재한다.

이번 Summary에서는 AD 콘솔을 사용하는 방법보다는 여기서 등장하는 개념과, 그것의 특징에 대해서 설명하도록 한다.

***

## 1. 도메인(Domain), 트리(Tree), 그리고 포리스트(Forest)

도메인은 관리 대상의 기본 단위이다. (일반 도메인처럼 xxxxx.com과 같은 모양이다.)

트리는 도메인의 집합을 의미한다. 트리 내에서는 namespace의 연속성이 존재한다.

포리스트는 2개 이상의 트리가 존재하는 논리적인 개념이다. 포리스트의 특징은 아래와 같다.

+ 1. 포리스트는 Security Boundary를 공유한다.
+ 2. 포리스트는 Schema를 공유한다. Schema란 Object를 생성하기 위한 속성 정보를 저장한 테이블을 의미한다.

***

## 2. Object

Object는 도메인 내의 사용자(User)와 컴퓨터을이 사용하는 개체이다.

그러므로, Object를 관리한다는 것은 사용자 계정을 관리하거나, 컴퓨터를 관리한다는 것과 같은 의미이다.

또한, 여러 Object를 한번에 수월하게 관리하기 위해 Group이라는 개념도 존재한다.


### Group

#### 1. Type

Type은 Distribution과 Security 그룹이 존재한다.

Distribution은 이메일 배포 목적으로 사용한다.

Security는 특정 Object에 대한 접근 권한, 또는 사용 권한을 부여하는 group이다.

#### 2. Scope

+ 1. Univeral: 같은 도메인 트리나 포레스트에 존재하는 모든 그룹을 포함할 수 있다. **동일 포리스트 자원에 대해서 모두 접근 권한을 부여할 수 있다.**
+ 2. Global: 같은 도메인에 존재하는 모든 Group을 포함할 수 있다. **동일 포리스트 자원에 대해서 모두 접근 권한을 부여할 수 있다.**
+ 3. Domain Local: 같은 도메인과 동일 포리스트의의 다른 Group을 포함할 수 있다. **로컬 자원에 대해서만 접근 권한을 부여할 수 있다.** 

***

## 3. OU(Organizational Unit) 

OU란 Object를 하나로 묶어 관리하기 위한 개념이다. 

이 개념은 권한 위임, 그룹 정책을 적용할 때 더욱 명확해진다.


### Group vs OU

전자는 사용자를 묶는 개념이고, 후자는 Object를 묶는 개념이다.

***

## 4. Domain Controller(DC)

도메인에 대한 권한 및 인증(ex. 로그인, 권한 확인, 사용자 등록, 암호 변경)들을 관리하는 서버 컴퓨터를 의미한다.

+ Authorization: DC는 공유 폴더, DB 등의 호스팅하고 동기화한다.
+ Authentication: DC는 KDC를 통해 token을 제공해 인증할 수 있도록 한다.

***

## 5. Global Catalog(GC)

한 도메인에서 Object를 찾을 때, 찾고자 하는 Object가 없다면 도메인이 소속된 포리스트를 검색해야한다. 

이때, Global Catalog를 통해 더욱 빠른 속도로 검색할 수 있다.

즉, GC란 포리스트에 소속된 도메인의 Object 정보들이 소속된 통합 저장소이다.

***







