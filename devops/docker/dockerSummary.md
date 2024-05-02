# Docker

## 1. Docker 기본

### 1.1 도커를 사용하는 이유

패키지 버전, 서버 OS 등에 따라 설치하는 과정에서 에러를 만날 수 있지만

도커를 사용하면 이를 피할 수 있고 이에 따라 프로그램 테스트 및 배포를 쉽게 한다.

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

### 컨테이너 로그 확인
```shell
docker logs -f [도커 컨테이너 이름 | id]
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

WORKDIR /usr/src/app # cd와 동일함. 컨테이너 이미지의 작업 환경을 변경함

COPY [로컬 디렉토리 경로] [이미지 디렉토리 경로] # 로컬 디렉토리 경로에 있는 파일을 이미지 디렉토리 경로로 복사함

RUN command # 컨테이너 이미지에서 커맨드 실행함

EXPOSE [포트번호]  # 컨테이너를 실행할 포트를 지정

CMD ["execute shell"] # 컨테이너 시작 시 실행할 명령어 지정
```

```shell
docker build -t [사용자 id]/[도커 이미지 name]:[tag] -f [Dockerfile 이름(생략 시, Dockerfile을 찾음)]./ # 이미지 생성
docker run -p [로컬 port 번호]:[컨테이너 port 번호] [사용자 id]/[도커 이미지 name]:[tag] # 컨테이너 실행
```

### 3.1 실습: Dockerfile를 이용하여 Node.js App 실행

|- Dockerfile

|- package.json

|- server.js

위와 같이 로컬 디렉토리에 파일이 존재하면, 아래와 같이 Dockerfile을 작성하여 도커 이미지를 생성할 수 있다.

```Dockerfile
FROM node:10             # 베이스 이미지를 node:10으로 하고,
WORKDIR /usr/src/app     # 실행할 컨테이너의 WORKDIR을 /usr/src/app에 지정하고(cd와 동일한 명령어), 
COPY ./ ./               # 현재 로컬 디렉토리에 있는 파일을 WORKDIR에 복사하고,
RUN npm install          # npm install을 실행하여 관련 파일을 설치하고(도커 이미지 컨테이너 shell 실행),
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

### 3.2 Docker Volume

파일(ex. server.js)이 변경될 때 마다, 도커 이미지를 새롭게 빌드하고 컨테이너를 실행하는 절차는 다소 비효율적이다.

이를 해소하기 위해 로컬 파일을 마운트하여 파일이 변경되더라도 새롭게 이미지를 빌드하지 않아도 반영되도록 만들 수 있다.

```shell
docker run -d -p [로컬_PORT:컨테이너_PORT] -v /usr/src/app/node_modules -v %(pwd):/usr/src/app [이미지 아이디]

# node_modules는 로컬 호스트 디렉토리에 존재하지 않으므로 로컬을 참조하지 않고,
# 나머지는 현재 로컬 호스트 디렉토리(pwd)에 있는 것을 /usr/src/app에 마운트하여 사용한다.
```

## 4. Docker Network

도커 네트워크는 여러 컨테이너 간 통신을 위해 사용된다.

### 4.1 Bridge를 통한 도커 네트워크 구성

도커 컨테이너들은 Bridge를 통해 도커 네트워크를 형성하여 통신할 수 있다.

네트워크 토폴로지는 이더넷과 유사하다.

브릿지 네트워크를 새롭게 생성한 후, 해당 네트워크에서 컨테이너를 기동할 수 있다.

```shell
docker create network [new-network]
docker run --network [new-network] --name [컨테이너명] [컨테이너 이미지명]
```

물론, 도커 엔진이 설치되면 docker0 네트워크 브릿지가 기본으로 생성되고, 네트워크 설정 없이 컨테이너를 기동하면 docker0 네트워크에 속하게 된다.

docker0 브릿지 게이트웨이 주소는 172.17.0.1이고, 해당 네트워크에서 컨테이너를 기동될 때 마다 순차적으로 ip를 할당한다.

```shell
root@ip-XXX-XXX-XXX-XXX:~# docker network ls
NETWORK ID     NAME                             DRIVER    SCOPE
8d39ffd164a0   bridge                           bridge    local
49cb691121e7   host                             host      local
e853c2881850   none                             null      local
...

root@ip-XXX-XXX-XXX-XXX:~# docker inspect network bridge
[
    {
        ...
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.17.0.0/16",
                    "Gateway": "172.17.0.1"
                }
            ]
        },
        ...
        "Options": {
            ...
            "com.docker.network.bridge.name": "docker0",
            ...
        },
    }
]
```
```shell
root@ip-XXX-XXX-XXX-XXX:/# docker run -d --name my-redis redis

root@ip-XXX-XXX-XXX-XXX:/# docker inspect network bridge
[
    {
        ...
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.17.0.0/16",
                    "Gateway": "172.17.0.1"
                }
            ]
        },

        ...
        "Containers": {
            "af5b54880190e1360a901d0486b1412d914ddd099ae940babef19bdc6848524e": {
                "Name": "my-redis",
                ...
                "IPv4Address": "172.17.0.2/16",
            }
        },
        "Options": {
            "com.docker.network.bridge.default_bridge": "true",
            "com.docker.network.bridge.enable_icc": "true",
            "com.docker.network.bridge.enable_ip_masquerade": "true",
            "com.docker.network.bridge.host_binding_ipv4": "0.0.0.0",
            "com.docker.network.bridge.name": "docker0",
            "com.docker.network.driver.mtu": "1500"
        },
    }
]
```

my-redis 컨테이너를 실행했으므로 현재 docker0 브릿지 네트워크 내에는 1개의 컨테이너가 연결되어있다.

동일한 도커 네트워크 안에서의 컨테이너 끼리는 브릿지를 통해서 통신이 가능하다.

동일한 브릿지 네트워크 내에서 컨테이너 끼리 아래 방법으로 통신한다.

- 컨테이너 서비스 이름
- [도커 네트워크 게이트웨이 ip] : [도커 컨테이너 포트번호]

도커 컨테이너 ip로도 가능하지만 컨테이너를 기동할 때 마다 변경되므로 추천하지 않는다.

예를 들어, DB 컨테이너 이름 및 포트가 my-mysql과 3306이고, DB 컨테이너와 Spring Boot WAS를 연동하여 실행할 때 아래와 같이 properties를 설정하면 된다.

```properties
spring.datasource.url=jdbc:mysql://my-mysql:3306/test
```


만약 외부와의 통신이 필요하다면, 컨테이너 ip(eth)와 실제 호스트 ip:port를 매핑해야 한다. 

이를 위해서 포트 포워딩이 사용된다. 포트 포워딩 시 호스트에서 컨테이너 통신 관련 방화벽 정책이 생성된다.

```shell
# 포트 포워딩
docker run -p [호스트에 포워딩할 포트]:[컨테이너 포트] [컨테이너 이미지 명]
```

```0.0.0.0:8080->8080/tcp, :::8080->8080/tcp```과 같이 포트 포워딩이 되며 이는 호스트 및 호스트 외부에서도 접근이 가능하다는 것을 의미한다.

만약, 호스트 내에서만 해당 컨테이너에 접속이 가능하고 외부에서는 불가능하게 만드려면 0.0.0.0이 아닌 127.0.0.1로 변경하면 된다.


### 4.2 호스트 네트워크 직접 연결

브릿지를 활용하면 게이트웨이를 통해서 외부 호스트와 통신하고, 또한 게이트웨이를 통해서 컨테이너 간 통신을 할 수 있었다.

그러나, 브릿지를 통해 네트워크를 분리하지 않고 호스트 네트워크에 직접 컨테이너를 기동할 수도 있다.

```
docker run --network host ...
```

### 4.3 기존 컨테이너에 연결

원래 기동 중인 컨테이너의 네트워크 namespace를 공유하여 컨테이너를 연결하는 방식이다.

```
docker run --network container:[컨테이너명 | 컨테이너 ID] ...
```




## 5. Docker Compose

하나 이상의 컨테이너를 서로 네트워크적으로 연결하여 한번에 기동하고자 할 때 사용한다.

docker-compose.yml 파일을 설정한 후, 명령어를 통해 컨테이너를 실행할 수 있다.(Dockerfile과 함께 사용된다.)

아래와 같은 형태로 .yml 파일을 작성한다.

```yml
# docker-compose.yml
version: "3"
services:
  react:
    build:
      context: . # 참조 디렉토리 설정
      dockerfile: Dockerfile # 참조 Dockerfile 설정
    ports:
      - "5000:3000" # 로컬 포트 - 컨테이너 포트 매핑
    volumes:
      - ./:/usr/src/app # volume 옵션 사용: ./를 /usr/src/app에 마운트함
```

```shell
docker-compose up --build # 이미지 빌드 후, docker-compose로 컨테이너 실행
docker-compose up # 이미지가 있으면 빌드하지 않고, docker compose로 컨테이너 실행
docker-compose down # docker-compose 컨테이너 중지
```

## 6. Git Action을 활용한 소스 배포

Github Repository에 존재하는 소스를 자동으로 테스트 및 AWS ElasticBeanStalk에 배포하는 방법에 대해 설명한다.

현재 디렉토리의 docker-compose.yaml, deploy_example.yaml 파일을 참고한다.

### 6.1 IAM 역할 생성

IAM이란 Identity And Management의 약자로 AWS 리소스에 대한 접근을 제어하는 서비스를 의미한다.

IAM 서비스에서 AWS 리소스에 대한 사용자와 역할을 생성할 수 있다.

IAM 역할은 AWS 정책들을 가질 수 있고, 정책이란 리소스 접근 권한을 의미한다.


### 6.2 AWS ElasticBeanstalk 생성

소스를 배포하여 컨테이너들을 기동하기 위한 서버를 생성하는 것과 유사하다. 

IAM 역할을 설정하여 elasticbeanstalk 서버 리소스 접근에 대한 권한을 부여한다.

예를 들어, IAM 역할이 RDS(관계형 데이터베이스) 쓰기, 읽기, 실행 권한 정책을 갖고, 이를 Elasticbeanstalk에 연결하여 리소스 접근 권한을 부여할 수 있다.

반대로, elasticbeanstalk에 역할을 설정하지 않으면 리소스 접근이 불가능하다.


### 6.3 IAM 사용자 생성

IAM 사용자란 AWS 리소스 접근 제어에 대한 특정 권한을 부여받는 사용자를 의미한다.

IAM 사용자에 정책을 설정하여 권한을 부여할 수 있다.

IAM 사용자를 생성할 때, ACCESS_KEY와 SECRET_ACCESS_KEY가 생성되고, 이를 활용하여 elastic beanstalk(리소스) 접근에 필요한 역할 자격을 얻을 수 있다.

### 6.4 Git Action 연동

방법은 아래와 같다. ./github/workflows/deploy.yaml 파일을 이용하여 자동으로 AWS Elasticbeanstalk에 배포되도록 한다.

+ 1. Github Repository 생성 후, 소스 배포(git push)
+ 2. AWS 및 Docker 배포 관련 KEY 설정 : AWS_ACCESS_KEY, AWS_SECRET_KEY, DOCKER_USERNAME, DOCKER_PASSWORD 
+ 3. AWS RDS(관계형 데이터베이스) 생성


### 6.5 보안 그룹 생성

보안 그룹은 방화벽과 관련된 개념이다.

보안 규칙(방화벽 등)을 설정된 보안 그룹을 만든 후, 이를 Elasticbeanstalk 구성 옵션에서 지정하여 사용할 수 있다.

### 6.6 ElasticBeanstalk 환경변수(환경속성) 추가

구성 옵션에서 RDS와 관련된 환경변수 추가가 필요하다. docker-compose.yaml 파일에서 참고하여 설정할 수 있다.

### 6.7 git push

./github/workflows/deploy.yaml 파일이 존재하고, git push가 발생하면 자동으로 빌드하여 Elastic Beanstalk에 배포된다.














