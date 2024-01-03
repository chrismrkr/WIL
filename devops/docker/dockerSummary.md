# Docker

## 1. Docker 기본

### 1.1 도커를 사용하는 이유

프로그램 다운로드를 쉽고 간편하게 만들기 위해 도커를 사용한다.

패키지 버전, 서버 OS 등에 따라 설치하는 과정에서 에러를 만날 수 있지만 도커를 사용하면 이를 어느정도 피할 수 있다.

### 1.2 도커란?

컨테이너를 실행하는 도구이자 컨테이너 기반 오픈소스 플랫폼을 의미한다.

### 1.3 컨테이너란?

컨테이너는 다양한 프로그램과 실행환경을 추상화하여 동일한 인터페이스를 제공한다. 이를 통해 배포 및 관리를 쉽고 편리하게 한다.

### 1.4 도커 이미지란?

컨테이너를 생성하기 위해 필요한 모든 것을 포함한 파일을 의미한다.

그러므로, 도커 이미지를 통해 컨테이너를 생성하고, 컨테이너를 통해 프로그램을 서버 OS, 패키지 등에 제약받지 않고 실행할 수 있다.

### 1.5 이미지로부터 컨테이너가 실행되는 과정

이미지는 시작 명령어와 스냅샷을 포함하고 있다. 생성할 컨테이너에 스냅샷을 올린 후, 시작 명령어를 통해서 컨테이너를 생성한다.

```
docker run 이미지이름 [사용자 지정 시작 명령어]
```

이미지에 저장된 시작 명령어가 기본적으로 실행되지만, 원하는 경우에는 다른 명령어를 사용할 수 있다.

### 1.6 cgroup과 namespace

cgroup은 cpu, 메모리, I/O Bandwidth 등 하드웨어 리소스 사용량을 컨테이너마다 할당해주는 것을 의미한다.

예를 들어, cgroup은 특정 컨테이너의 메모리를 2GB까지 사용할 수 있도록 제한할 수 있다.

namespace는 컨테이너(프로세스)를 다른 컨테이너와 격리시키는 기술을 의미한다. 두가지 모두 리눅스 OS에서 사용할 수 있다.


## 2. Docker 기본 명령어

### 컨테이너 내려받기
```shell
# docker pull [id]/[repository]:tag
docker pull myId/myRepository:latest
```

### 컨테이너 실행
```shell
# docker run [OPTION] IMAGE[:tag] [COMMAND]
docker run -i -t ubuntu:16.04 /bin/bash
## ubuntu:16.04 컨테이너를 실행한 후 /bin/bash를 실행하라는 명령어
# -i : 표준 입출력 사용 옵션
# -t : tty 할당
# --name [이름]: 실행할 컨테이너에 이름을 할당하는 옵션
# -p : 포트 포워딩 옵션 
```

```shell
# myid/docker-app:latest 도커 이미지를 컨테이너로 실행하고 shell을 기동하라는 명령어어
docker run -it myid/docker-app:latest sh
```

### 컨테이너 종료
```shell
exit
```

### 컨테이너 종료하지 않고 나오기
```shell
Ctrl + P + Q
```

### 컨테이너 목록 확인
```shell
docker ps # 실행 중인 컨테이너 목록
docker ps -a # 모든 컨테이너 목록 
```

### 컨테이너 시작
```shell
docker start [컨테이너_이름]
```

### 컨테이너 중지
```shell
docker stop [컨테이너_이름/컨테이너_ID] # stop gracefully
docker kill [컨테이너_이름/컨테이너_ID] # stop immediately
```

### 컨테이너 삭제
```shell
docker rm [컨테이너_이름]
docker rm -f [컨테이너_이름] # 정지 및 삭제
```

###  이미지 목록
```shell
docker images
```

### 이미지 삭제
```shell
docker rmi [이미지_이름]
```

### 이미지 생성

```shell
docker create [도커 image id]
```


### 이미지 docker hub 저장

```shell
docker login
docker push [id/image:tag]
```

## 3. Dockerfile

Dockerfile은 ```docker create``` 명령어를 사용하지 않고 docker image를 생성하기 위해 사용되는 파일이다.

Dockerfile을 통하여 Docker Image가 생성되는 과정은 아래와 같다.

(Base Image를 통하여 임시 컨테이너 실행 -> 신규 명령어 및 파일 스냅샷 추가 -> 새로운 이미지 생성)

Docker image를 생성하기 위한 대략적인 과정, Dockerfile의 토대, 그리고 실행 명령어는 아래와 같다.

+ 1. Base Image 지정
+ 2. Image 생성을 위한 추가적인 명령어 명시

```Dockerfile
FROM baseImage # Base Image를 명시함

RUN command # 추가적으로 필요한 파일을 다운로드 함

CMD ["execute shell"] # 컨테이너 시작 시 실행할 명령어 명시 
```

```shell
docker build -t [사용자 id]/[도커 이미지 name]:[tag] ./ # 이미지 생성
docker run -p [로컬 port 번호]:[컨테이너 port 번호] [사용자 id]/[도커 이미지 name]:[tag] # 컨테이너 실행
```

### 3.1 실습: Dockerfile를 이용하여 Node.js App 실행

|- Dockerfile
|- package.json
|- server.js

위와 같이 로컬 디렉토리에 파일이 존재하면, 아래와 같이 Dockerfile을 작성하여 도커 이미지를 생성할 수 있다.

```Dockerfile
FROM node:10             # 베이스 이미지를 node:10으로 하고,
WORKDIR /usr/src/app     # 실행할 컨테이너의 WORKDIR을 /usr/src/app에 지정하고, 
COPY ./ ./               # 현재 로컬 디렉토리에 있는 파일을 WORKDIR에 복사하고,
RUN npm install          # npm install을 실행하여 관련 파일을 설치하고,
CMD ["node", "server.js"] # 컨테이너 생성 후, node server.js 커맨드를 통해 App을 실행한다.
```

+ COPY를 하지 않으면?
컨테이너에는 node 베이스 이미지만 존재하고, 파일 스냅숏(package.json, server.js)은 존재하지 않게 되므로 App이 실행되지 않는다.

+ Dockerfile 개선
예를 들어, server.js 파일이 변경될 때 마다 ```npm install```을 실행할 필요는 없다. 이를 위해 아래와 같이 수정하면 된다.

```Dockerfile
FROM node:10            
WORKDIR /usr/src/app     
COPY package*.json ./   # package*.json 파일이 수정될 때만 npm install을 실행함            
RUN npm install
COPY ./ ./              # 그 이외에는 cache된 것을 사용함
CMD ["node", "server.js"] 
```

