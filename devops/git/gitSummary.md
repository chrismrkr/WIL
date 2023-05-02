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

stage area에 저장된 파일을 등록한다. push 커맨드를 통해 remote repository로 보낼 준비를 한다.

### 2.6 git log

commit 이력을 확인한다. 여러 옵션들이 많고, 도움말을 통해 찾아보도록 한다.

**자주 사용하는 옵션**
+ -p : 각 commit 사이의 차이점을 보여준다.
+ --grapth : 그래프 형태(branch, merge 포함)로 commite 이력을 보여준다.
+ --author : git log --author = "..." 커맨드로 사용한다. 해당 이름으로 commit된 것만 로그로 보여준다.
+ --oneline : commit와 메세지만 간략하게 로그로 보여준다.
+ --since, --until, --grep

### 2.7 git reset [commit id] \<options>

특정 commit까지 이력을 초기화한다.

+ --soft 옵션 : commit 상태만 변경하고, index와 working tree는 유지된다.
+ --hard 옵션 : 해당 commit id까지 index와 working tree가 모두 변경된다.

### 2.8 git revert [commit id]

commit id 이전 버전의 이력을 새롭게 다시 commit한다.

## 3. 작업 분기 및 병합


