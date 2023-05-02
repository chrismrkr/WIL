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

git은 branch를 통해 비선형적인 개발이 가능하다.

### 3.1 git branch

현재 존재하는 모든 branch를 확인한다.

### 3.2 git switch -c [브랜치명]

새로운 branch를 생성하고, 그 branch로 이동한다.

### 3.3 git merge [브랜치명]

현재 브랜치에 [브랜치명] branch를 병합한다.

### 3.4 confilct 해법

branch 병합 시, conflict가 발생할 수 있다. 발생 가능하 시나리오는 아래와 같다.

1. 특정 branch에 병합 또는 push 하려고 하는데, 기존 branch가 변경된 경우.

이때, 변경이란 동일한 코드 블록이 변경되었거나, 파일 이름이 변경되었거나, 파일이 삭제된 경우를 의미한다.

이 경우에는 git pull을 통해 기존과 동기화를 한 후, add -> commit을 한다.

그러면 branch 상태가 branch|merging으로 바뀌고, 이때 원하는 코드만을 남기고 파일을 저장한다.

그리고 add -> git commit -> x! option을 사용하면 conflict가 해결된다.

2. 동일한 이름의 파일이 서로 다른 branch에서 생성 또는 변경된 후, 공통 branch로 merge되는 경우에 conflict가 발생한다.

이외에도 파일 권한 및 속성이 변경될 때도 conflict가 발생할 수 있다.

merge를 취소하려면 ```git merge --abort``` 커맨드를 사용한다.

기존 branch가 동기화가 되지 않아서 conflict가 발생하므로, 경우에 따라 어떻게 할지 결정하면 된다.


## 4. Github(원격 저장소) 공유

### 4.1 원격 저장소 저장 커맨드

+ 1. git remote add [원격 저장소 참조 이름] [github URL]

예를 들어, ```git remote add origin https://github.com/xxx/test.git``` 커맨드는,

해당 URL 원격 저장소를 등록하고, origin 이름으로 참조한다는 것을 의미한다. 여러 원격 저장소를 1개의 git에 등록할 수 있다.

+ 2. git push [원격 저장소 참조 이름] [branch명]

예를 들어, ```git push origin main```은 원격 저장소 origin에 main branch를 push한다는 뜻이다. 여기서 conflict가 발생할 수 있다.

```git remote -v```로 원격 저장소 리스트를 확인할 수 있고, ```git remote rm [원격 저장소 참조명]```으로 원격 저장소 참조를 삭제할 수 있다.

### 4.2 git clone [URL] [local 디렉토리명]

local 디렉토리에 URL에 해당하는 원격 저장소를 복제한다. 

### 4.3 git pull [원격 저장소 참조명] [branch명]

원격 저장소와 로컬 저장소를 동기화한다.



