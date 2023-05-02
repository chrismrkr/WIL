# Git

## 1. Git 특징

### 1.1 분산 관리

원격 저장소의 모든 데이터를 로컬에 저장하는 방식이다. 그러므로, commit한 개발자 수 만큼 local에 백업되는 분산 시스템이다.

### 1.2 데이터 무결성 보장

모든 commit은 checksum 검사를 한다. 히스토리가 변경되면 모든 commit id가 변경된다.

그러므로, id가 동일하면 변경이 되지 않은 것을 의미하고, 이를 통해 데이터 무결성을 보장한다.

### 1.3 3단계 구조

local repository -> stage area -> remote repository 3단계로 나뉜다. git add, git commit, git push 명령어를 사용하여 통제한다.


## 2. 기본 명령어

### 2.1 git help [명령어]

브라우저에 해당 git 명령어 도움말을 띄운다.

### 2.2 git init

local repository를 생성한다. 초기 브랜치 기본 이름은 master이나, github에서는 main을 쓰므로 ```git branch -M main``` 명령어로 수정한다.

### 2.3 git status

현재 작업 중인 파일 상태 확인(현재 branch의 add 및 commit 상태 확인)

### 2.4 git add [파일명]

파일을 stage area로 올린다. 파일명 대신에 -A 옵션을 넣으면 변경된 모든 파일을 stage area로 올린다.

### 2.5 git commit [-m "메세지"]

