# Kubernetes
K8s에 대해 학습 및 실습한 내용을 정리함

## 개요
- 표준화된 컨테이너 운영 환경을 제공하는 기술
- 클라우드 프로바이더에서 사용될 수 있음
- 스케일 인, 스케일 아웃, 로드밸렌싱 등을 지원함

## 아키텍처
K8s 클러스터에는 마스터 노드와 워커 노드들로 구성되어 있고, 워커 노드는 컨테이너를 실행하고 있는 파드가 존재함
- 클러스터 : 1개의 마스터 노드와 N개의 워커 노드로 이루어진 하나의 집합
- 마스터(Master) 노드 : 워커 노드와 통신하여 이들을 제어함. Control Plane이라 함
- 워커(Worker) 노드 : 파드들을 실행하는 가상 인스턴스(CPU, 메모리, 디스크 등이 할당됨)
- 파드(Pod) : N개의 컨테이너를 담은 개체로 K8s에서의 가장 작은 단위

## 개념 설명
### 워커(Worker) 노드
- 마스터 노드에 의해 관리되는 호스트
- 1개 이상의 파드(Pod)를 갖고, 파드는 k8s 클러스터 내에서 독립적인 IP, 볼륨 등을 갖음
- 워커 노드 내의 파드들은 동일한 복제본일 수 있고, 또는 서로 다를 수 있음
  - 파드 내 컨테이너를 Docker로 실행이 필요하다면, 워커 노드 내에 Docker가 설치되어 있어야함
- 워커 노드는 kubelet과 kube-proxy를 갖음
  - kubelet : 마스터 노드와의 통신 인터페이스. 마스터 노드는 kubelet을 통해 Pod를 제어함
  - kube-proxy : 트래픽, 통신, 방화벽 등을 제어함
### 마스터(Master) 노드
- k8s 클러스터 제어 역할 담당 노드
- kube-apiserver, kube-controller-manager, kube-scheduler를 갖음
  - kube-apiserver : API를 노출하여 클러스터 내 모든 구성요소와 통신할 수 있도록 함
  - kube-controller-manager : 클러스터 상태 관리
  - kube-scheduler : 트래픽 등을 고려하여 Pod 스케일링이 필요한지 아닌지를 판단함
 
## k8s가 하는 일과 사용자가 할 일
### k8s가 하는 일
- pod 모니터링 및 스케일링
- 노드 내 pod 생성 및 관리
### 사용자가 할 일
- 클러스터 및 노드 생성
- API 서버, kubelet 등 k8s 관련 소프트웨어 설치
- 파일 시스템, 로드밸렌서 등 클러스터 내 리소스 생성

## Minikube로 쿠버네티스 실습 1 : Pod 명령적 배포
- 로컬환경 내에 Minikube(단일 노드 클러스터)를 설치하고, kubectl 명령어를 통해 해당 클러스터에 명령어를 전달할 수 있음
  - kubectl 설치 in MacOS : brew install kubectl
  - Minikube 설치 in MacOS : brew install minikube > minikube start
  - https://minikube.sigs.k8s.io/docs/start/

- kubectl 명령어를 통해 Pod를 워커 노드에 배포할 수 있음
- Pod는 Deployment 객체를 통해 클러스터에 배포되며, Service 객체를 통해 클러스터 내 다른 Pod와 통신하고 외부에 노출됨
  - Pod 객체
    - 1개 이상의 **컨테이너**를 갖는 객체
    - Pod는 클러스터 내 고유 IP를 갖음
      - 다만, 재시작 또는 교체될 때 마다 변경됨
    - k8s는 pod를 적절한 워커 노드에 배치함
  - Deployment 객체
    - k8s가 Deployment 객체에 명령을 전달하고, Deployment 객체는 **Pod를 생성하여 노드에 배치**함
    - Pod당 하나의 Deployment 객체를 갖는다고 볼 수 있음
    - Deployment 객체를 통해 k8s는 pod를 자동으로 적절한 워커 노드에 할당하고, 배포 중 문제 시 롤백하고, 스케일링을 할 수 있음
  - Service 객체
    - pod를 클러스터 내 다른 pod나 **외부에 노출**시키기 위해 사용함
    - service 객체는 pod 그룹을 나누고 이곳에 공유 IP 를 할당함
    - 공유 IP를 통해 외부에서 Pod에 접근할 수 있음

- deployment 객체로 pod 생성
  - ```kubectl create deployment [pod 이름] --image='[docker-hub id]/[image-name]:[tag]'```
  - docker hub에 이미지가 있어야 함. 왜냐하면, 클러스터 내에는 도커 이미지가 존재하지 않기 때문임
  - ```kubectl get pods``` 명령어로 생성된 pod 확인 가능
  - ```kubectl delete [이름]``` 명령어로 pod 삭제 가능

- service 객체로 pod 노출
  - ```kubectl expose deployment [pod 이름] --port=[포트번호] -type [type]```
  - type 종류
    - NodePort : 워커 노드 IP로 expose
    - LoadBalancer : 클러스터 내 로드밸랜서 IP로 노출
    - ClusterIP : 외부로 노출하지 않음
  - ```kubectl get services``` 명령어로 확인 가능

## Minikube로 쿠버네티스 실습 2 : Pod 선언적 배포
- 환경설정 파일을 통해서 Pod를 배포할 수 있음
- **Example 1**
  - auth-api 도커 이미지를 Pod 내 컨테이너로 배포
  - users-api 도커 이미지를 Pod 내 컨테이너로 배포
  - 단, 두 컨테이너는 동일한 Pod에 속함
  - auth-api는 외부에 노출되지 않고, users-api는 외부에 노출함

```yaml
# users-auth-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: users-auth-deployment
  labels:
    app: users-auth 
spec:
  replicas: 1 # 복제할 pod 수 지정
  selector:
    matchLabels:
      app: users-auth # deployment 객체에서 관리할 파드의 label 지정
  template:
    metadata:
      labels:
        app: users-auth
    spec:
      containers:
      - name: users-api
        image: my-docker-repo/users-api:latest
      - name: auth-api
        image: my-docker-repo/auth-api:latest
```

```yaml
# users-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: users-service
  labels:
    app: users-auth
spec:
  selector:
    app: users-auth
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
```

```
kubectl apply -f=users-auth-deployment.yaml
kubectl apply -f=users-service.yaml
minikube service users-service
```

- 결과
  - 동일 Pod 내에서는 localhost로 통신이 가능하므로 users-api, auth-api는 localhost로 통신하면 됨
  - users-service 실행 시 노출된 IP로 외부에서 user-api에 접근할 수 있음
***

- **Example 2**
  - users-api, auth-api 도커 이미지를 서로 다른 pod에 컨테이너로 배포
  - users-api 컨테이너는 클러스터 내의 auth-api를 호출할 수 있어야 함
  - auth-api는 외부에 노출하지 않고, users-api만 외부에 노출함
```yaml
# users-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: users-deployment
  labels:
    app: users-api
spec:
  replicas: 1  # 복제할 pod 수 지정
  selector:
    matchLabels:
      app: users-api # deployment 객체에서 관리할 파드의 label 지정
  template:
    metadata:
      labels:
        app: users-api
    spec:
      containers:
      - name: users-api
        image: my-docker-repo/users-api:latest
        env:
          - name: AUTH_ADDRESS
          - value: "auth-service.default:80"
```

```yaml
# auth-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-deployment
  labels:
    app: auth-api 
spec:
  replicas: 1  # 복제할 pod 수 지정
  selector:
    matchLabels:
      app: auth-api # deployment 객체에서 관리할 파드의 label 지정
  template:
    metadata:
      labels:
        app: auth-api
    spec:
      containers:
      - name: auth-api
        image: my-docker-repo/auth-api:latest
```

```yaml
# users-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: users-service
spec:
  selector:
    app: users-api
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
```

```yaml
# auth-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth-api
  type: ClusterIP
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
```

```
kubectl apply -f=users-deployment.yaml -f=auth-deployment.yaml
kubectl apply -f=users-service.yaml -f=auth-service.yaml
minikube service users-service
minikube service auth-service
```

- 결과
  - 서로 다른 deployment 객체를 통해 독립적인 pod에 users-api와 auth-api 컨테이너를 배포함
  - users-api 컨테이너는 env.AUTH_ADDRESS 변수를 이용해 auth-api를 호출할 수 있음
    - auth-api ClusterIP를 직접 사용할 수 있음
    - k8s 클러스터 내 DNS 서비스를 이용할 수 있음
      - 해당 방식을 선택함
    - 자동 할당된 환경변수를 이용할 수 있음(AUTH_SERVICE_SERVICE_HOST)
  - users-service는 LoadBalancer를 통해 IP를 외부에 노출하였고, auth-service는 ClusterIP를 통해 클러스터 내에서만 IP가 공유되도록 함


## Minikube로 쿠버네티스 실습 3 : Volume
- 쿠버네티스 클러스터에서 컨테이너 실행 시, 볼륨 마운트도 가능함
- **Example1. emptyDir Volume**
  - 생명주기가 pod와 동일함
  - 만약 pod가 삭제/재시작하면 volume도 삭제/재시작됨
  - 여러 pod가 하나의 volume을 공유해야하는 상황에서는 적절하지 않음

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: app
  template: 
    metadata:
      labels:
        app: app
    spec: 
      containers:
        - name: app
          image: my-docker-hub/app:lastest
          env:
            - name: APP_FOLDER
              value: 'app'
          volumeMounts:
            - mountPath: /app/story
              name: app-volume 
      volumes:
        - name: app-volume
          emptyDir: {}
```

- 결과
  - env.APP_FOLDER 환경변수를 통해 컨테이너 내 로컬 디렉토리 경로를 Application Level에서 설정할 수 있음
    - ex. path.join(__dirname, process.env.APP_FOLDER, 'text.txt');
  - volumeMounts.mountPath로 pod 내 마운트할 디렉토리를 설정함
  - volumeMounts.name과 volumes.name을 매칭하여 emptyDir Volume을 매핑함

***
- **Example2. host Path**
  - 여러 pod가 호스트 머신의 동일한 경로의 디렉토리를 마운트함(노드 종속적)
  - 여러 호스트간 공유가 어렵다는 단점이 있음
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: app
  template: 
    metadata:
      labels:
        app: app
    spec: 
      containers:
        - name: app
          image: my-docker-hub/app:lastest
          env:
            - name: APP_FOLDER
              value: 'app'
          volumeMounts:
            - mountPath: /app/story
              name: app-volume 
      volumes:
        - name: app-volume
          hostPath:
            path: /data
            type: DirectoryOrCreate
```
- 결과
  - 호스트의 /data 디렉토리를 여러 파드에서 마운트함

***
- **Example3. Persistent Volume**
  - pod 라이프 사이클과 완전히 독립됨
  - 영구적이며 노드에 비의존적
  - 호스트 내의 Persistent Volume Claim을 통해 PV와 액세스함
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: app
  template: 
    metadata:
      labels:
        app: app
    spec: 
      containers:
        - name: app
          image: my-docker-hub/app:lastest
          env:
            - name: APP_FOLDER
              value: 'app'
          volumeMounts:
            - mountPath: /app/story
              name: app-volume 
      volumes:
        - name: app-volume
          persistentVolumeClaim:
            claimName: host-pvc
```

```yaml
# host-pvc.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: host-pvc
spec:
  volumeName: host-pv
  accessModes:
    - ReadWriteOnce
  storageClassName: standard
  resources:
    requests:
      storage: 1Gi # capacity: 1Gi
```

```yaml
# host-pv.yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: host-pv
spec:
  capacity:
    storage: 1Gi
  volumeMode: Filesystem
  storageClassName: standard
  accessModes: 
    - ReadWriteOnce # 하나의 노드 내에서 RW 
    # - ReadOnlyMany # 여러 노드 사이에서 R
    # - ReadWrtieMany # 여러 노드 사이에서 RW
  hostPath: 
    path: /data
    type: DirectoryOrCreate
```

## Minikube로 쿠버네티스 실습 4 : Network

- **Example 1**
  - users-api, auth-api, tasks-api 컨테이너는 모두 독립적인 pod에 존재함
  - users-api와 tasks-api는 auth api를 호출할 수 있어야함
  - auth-api는 외부에 노출되지 않고, users-api, tasks-api는 외부에 노출되어야함

```yaml
# users-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: users-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: users
  template:
    metadata:
      labels:
        app: users
    spec:
      containers:
        - name: users
          image: my-docker-hub/users-api:latest
          env:
            - name: AUTH_ADDRESS
              value: "auth-service.default" # 클러스터 내부 도메인명
```

```yaml
# auth-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: auth
  template:
    metadata:
      labels:
        app: auth
    spec:
      containers:
        - name: auth
          image: my-docker-hub/auth-api:latest
```

```yaml
# tasks-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tasks-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: tasks
  template:
    metadata:
      labels:
        app: tasks
    spec:
      containers:
        - name: tasks
          image: my-docker-hub/tasks-api:latest
          env:
            - name: AUTH_ADDRESS
              value: "auth-service.default" # 클러스터 내부 도메인명
```

```yaml
# services.yaml
apiVersion: v1
kind: Service
metadata:
  name: users-service
spec:
  selector:
    app: users
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: tasks-service
spec:
  selector:
    app: tasks
  type: LoadBalancer
  ports:
    - protocol: TCP
      port: 8000
      targetPort: 8000
---
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth
  type: ClusterIP
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
```

```
kubectl apply -f=users-deployment.yaml -f=auth-deployment.yaml -f=tasks-deployment.yaml
kubectl apply -f=services.yaml
```

+ metadata.name, metadata.labels.app : Deployment, Service, ReplicaSet 자체에 할당되는 이름과 Label을 설정
+ spec.template.metadata.labels.app : Deployment 및 ReplicaSet 객체 내에서 생성되는 Pod에 대한 Label을 설정
+ spec.selector.matchLabels : Deployment 내의 어떤 Label을 가진 Pod를 관리할지 선택하기 위한 값임

## kOps를 활용한 AWS에 K8s 클러스터 배포 방법
### 1. 환경 설정
- 클러스터 관리용 호스트에 kOps를 설치함
- AWS cli 설치 및 /usr/local/bin 디렉토리로 이동
- AWS 콘솔에서 IAM 계정 생성 및 Access Key 생성 : access Key 및 private access key 저장 필요
- 클러스터 관리용 호스트에 AWS IAM 계정 설정
  - ```aws configure``` 입력 후 access key 및 private access key 등록
- kOps 상태 저장을 위한 S3 Bucket 생성
- 도메인 생성
  - namecheap과 같은 사이트를 통해 도메인을 발급함
- AWS Route53을 활용한 DNS 호스트 Zone 생성
- kubectl 설치 및 /usr/local/bin 이동
- ssh key 생성
  - ```ssh-keygen -f .ssh/id_rsa```
    - Private Key : kOps에서 클러스터에 접속하기 위해 사용함
    - Public Key : 클러스터에서 kOps 접속을 받기 위해 사용함 
### 2. 클러스터 생성
- 아래 커맨드를 입력하여 kOps를 통해 클러스터 설정을 생성함(worker node 2, master node 1)
- Route 53에서 생성한 DNS가 적용되고, S3에 설정을 생성하는 명령어임
- ```kops create cluster --name=[Route 53 DNS] --state=s3://[S3 Bucket name] --zones=ap-northeast-2a --node-count=2 --node-size=t3.micro --master-size=t3.micro --dns-zone=[Route 53 DNS] --ssh-public-key ~/.ssh/id_rsa.pub```


### 3. 컨테이너 배포 및 서비스 실행




## Kubernetes Basics

Kubernetes 기초 개념에 대해서 소개함

### Deployment
Pod 배포 및 관리를 위해 사용되는 객체. Replication Controller, Replica Set보다 더 많이 활용됨.

예시는 아래와 같음.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        ports:
        - name: nodejs-port
          containerPort: 3000
```

### 주요 명령어
+ ```kubectl get deployments```: 현재 Deployment 객체 정보를 표시함
+ ```kubectl get pods --show-labels```: 현재 Pod 정보를 label과 함께 표시함
+ ```kubectl rollout status <current-deployment-object-name>```: Deployment 객체 정보를 표시함
+ ```kubectl set image <current-deployment-object-name> <image>=<image>:<version>```: deployment에서 배포한 pod 이미지를 변경함
+ ```kubectl edit <current-deployment-object>```: Deployment 객체를 수정함
+ ```kubectl rollout history <current-deployment-object>```: Deployment 객체 rollout Histor를 표시
+ ```kubectl rollout undo <current-deployment-object>```: 이전 버전으로 rollback
+ ```kubectl rollout undo <current-deployment-object> --to-revision=<n>: 특정 버전으로 rollback

### Service
Pod Endpoint를 외부에 노출하여 사용자가 접근할 수 있도록 만드는 객체

+ ClusterIP: 클러스터 내부에서만 접근할 수 있는 IP를 Pod에 부여. 즉, Virtual IP 생성
+ NodePort: Node와 동일한 IP를 Pod에 부여하여 외부에서 접근할 수 있도록 함. NodeIP 및 Port 노출
+ LoadBalancer: Cloud Provider(ELB)가 제공하고, External Traffic을 노드로 전달할 수 있는 IP를 제공함

DNS도 사용할 수 있음. 예시는 아래와 같음

```yaml
apiVersion: v1
kind: Service
metadata:
  name: helloworld-service
spec:
  ports:
  - port: 31001
    nodePort: 31001
    targetPort: nodejs-port
    protocol: TCP
  selector:
    app: helloworld
  type: NodePort
```

### Labels
객체에 부여될 수 있는 key:value Tag. 객체는 여러 개의 Label을 가질 수 있음

예를 들어, Deployment 객체에서 특정 Pod를 선택하여 관리하기 위해 Pod Label을 사용함. 예시는 앞선 Minikube Example에서 확인할 수 있음

**Node에도 Label을 부여할 수 있음. 이를 통해 Pod가 특정 Node에서만 동작하도록 만들 수 있음.**

즉, 특정 컨테이너를 특정 노드에만 배포되도록 하고자 할 때 유용하게 사용될 수 있음

```
kubectl label nodes <node-name> hardware=helloworld-only-node
```
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        ports:
        - name: nodejs-port
          containerPort: 3000
nodeSelector:
  hardware: helloworld-only-node
```

위의 예시에 따르면, Deployment 객체 생성 시 ```helloworld-only-node``` Label을 가진 노드에만 Pod가 배포됨.

### Health Check

kubenetes에서 실행 중인 Pod에 주기적으로 Health Check 보내며, pod가 정상 응답하는지 확인함.(Liveness Probe)

만약 Health Check에 실패한다면, 자동으로 새로운 Pod를 재시작함.

예시는 아래와 같음.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        ports:
        - name: nodejs-port
          containerPort: 3000
        livenessProbe:
          httpGet:
            path: /
            port: nodejs-port // port 대신 pod 이름을 지정할 수 있음
          initialDelaySeconds: 15
          timeoutSeconds: 30
```

### Readiness Probe

Pod가 정상적으로 기동되어 준비된 상태인지 확인함.

운영환경에서 Liveness Probe & Readiness Probe를 적극적으로 사용함.

예시는 아래와 같음.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-readiness
spec:
  replicas: 3
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        ports:
        - name: nodejs-port
          containerPort: 3000
        livenessProbe:
          httpGet:
            path: /
            port: nodejs-port
          initialDelaySeconds: 15
          timeoutSeconds: 30
        readinessProbe:
          httpGet:
            path: /
            port: nodejs-port
          initialDelaySeconds: 15
          timeoutSeconds: 30
```

### Pod State & Condition

#### Pod State
+ running : Pod가 정상적으로 실행 중
+ pending : Pod가 컨테이너를 정상적으로 받았으나 아직 실행 중이지 않은 상태
+ succeeded : Pod 내 컨테이너들이 정상적으로 종료된 상태(재시작 하지 않음)
+ Failed : Pod 내 모든 컨테이너가 종료되었고, 컨테이너 중 적어도 1대가 에러를 응답함
+ Unknown : Pod 상태가 결정될 수 없음

#### Pod Condition
+ PodScheduled : pod가 node에 배포되도록 schedule 됨
+ Ready : pod가 request/response할 준비가 완료됨
+ Unscheduled : 리소스 부족 등의 이유로 인한 pod schedule 실패
+ ContainersReady : Pod 내 모든 컨테이너가 Ready 상태

### Pod LifeCycle
Pod는 아래의 LifeCycle을 갖음.

```[init container] -> [post start] -> [liveness & readiness probe] -> [pre stop]```

컨테이너 시작 직후, 또는 종료 직전에 hook을 통해 특정 프로세스를 실행할 수 있음.

예시는 아래와 같음.

```yaml
kind: Deployment
apiVersion: apps/v1
metadata:
  name: lifecycle
spec:
  replicas: 1
  selector:
    matchLabels:
      app: lifecycle
  template:
    metadata:
      labels:
        app: lifecycle
    spec:
      initContainers:
      - name:           init
        image:          busybox
        command:       ['sh', '-c', 'sleep 10']
      containers:
      - name: lifecycle-container
        image: busybox
        command: ['sh', '-c', 'echo $(date +%s): Running >> /timing && echo "The app is running!" && /bin/sleep 120']
        readinessProbe:
          exec:
            command: ['sh', '-c', 'echo $(date +%s): readinessProbe >> /timing']
          initialDelaySeconds: 35
        livenessProbe:
          exec:
            command: ['sh', '-c', 'echo $(date +%s): livenessProbe >> /timing']
          initialDelaySeconds: 35
          timeoutSeconds: 30
        lifecycle:
          postStart:
            exec:
              command: ['sh', '-c', 'echo $(date +%s): postStart >> /timing && sleep 10 && echo $(date +%s): end postStart >> /timing']
          preStop:
            exec:
              command: ['sh', '-c', 'echo $(date +%s): preStop >> /timing && sleep 10']

```

### Secrets
Credentials, Keys, Passwords, Secret 등의 목적으로 이용하기 위해 Kubernetes에서 제공하는 객체임

Secret 객체를 환경변수 또는 파일로 활용할 수 있음

kubectl을 이용하여 secret을 생성하는 방법은 아래와 같음
```
echo -n "root" > ./username.txt
echo -n "password" > ./password.txt
kubectl create secret generic <secret-name> --from-file=./username.txt --from-file=./password.txt 
```

SSH key 또는 SSL 인증서용 secret도 생성할 수 있음

```
kubectl create secret generic ssl-certificate --from-file=ssh-privatekey=~/.ssh/id_rsa --ssl-cert=ssl-cert=mysslcert.crt
```

Secret을 환경변수로 사용하는 예시는 아래와 같음

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: wordpress-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: wordpress
  template:
    metadata:
      labels:
        app: wordpress
    spec:
      containers:
      - name: wordpress
        image: wordpress:6-php8.0
        ports:
        - name: http-port
          containerPort: 80
        env:
          - name: WORDPRESS_DB_PASSWORD
            valueFrom:
              secretKeyRef:
                name: wordpress-secrets
                key: db-password
          - name: WORDPRESS_DB_HOST
            value: 127.0.0.1
          - name: WORDPRESS_DB_USER
            value: root
          - name: WORDPRESS_DB_NAME
            value: wordpress
      - name: mysql
        image: mysql:8
        ports:
        - name: mysql-port
          containerPort: 3306
        env:
          - name: MYSQL_DATABASE
            value: wordpress
          - name: MYSQL_ROOT_PASSWORD
            valueFrom:
              secretKeyRef:
                name: wordpress-secrets
                key: db-password
```

secret을 파일 형태로 제공하는 예시는 아래와 같음
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        ports:
        - name: nodejs-port
          containerPort: 3000
        volumeMounts:
        - name: cred-volume
          mountPath: /etc/creds // /etc/creds/db-secrets/username, /etc/creds/db-secrets/password
          readOnly: true
      volumes:
      - name: cred-volume
        secret:
          secretName: db-secrets
```


## Kubernetes Advanced Topics

Kubernetes 관련 심화 개념에 대해서 정리함

### Service Discovery

동일한 Node 내의 Pod들은 localhost 내에 존재하므로 port 번호를 통해 서로 통신할 수 있음.

하지만, 동일한 Cluster 및 Namespace에 소속되나 서로 다른 Node 내에 있는 Pod 끼리는 Master Node의 kube-dns를 통해 서로 서비스를 찾을 수 있음.

Pod는 통신하고자 하는 Service의 metadata.name(Service 이름)을 이용하여 통신할 수 있음. 

모든 Pod의 /etc/resolv.conf 내에 DNS 관련 내용이 저장되어 있음

예시는 아래와 같음

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: helloworld-db
  template:
    metadata:
      labels:
        app: helloworld-db
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        command: ["node", "index-db.js"]
        ports:
        - name: nodejs-port
          containerPort: 3000
        env:
          - name: MYSQL_HOST
            value: database-service // database 관련 service 이름(metadata.name)
          - name: MYSQL_USER
            value: root
          - name: MYSQL_PASSWORD
            valueFrom:
              secretKeyRef:
                name: helloworld-secrets
                key: rootPassword
          - name: MYSQL_DATABASE
            valueFrom:
              secretKeyRef:
                name: helloworld-secrets
                key: database
```

```yaml
apiVersion: v1
kind: Service
metadata:
  name: database-service
spec:
  ports:
  - port: 3306
    protocol: TCP
  selector:
    app: database
  type: NodePort
```

### ConfigMap

Key-Value를 저장하여 클러스터 내 다른 객체에서 참조하기 위해 사용함. 그러므로, App을 다시 빌드하지 않고 Config만 변경할 수 있음.

ConfigMap은 Secrets 객체와 동일하게 환경변수, 파일 등으로 관리되고 마운트될 수 있음. 그러나, 암호화되지 않음.

추가로 key-value가 아닌 파일 전체를 저장할 수도 있음. Nginx 설정 파일을 ConfigMap으로 관리하는 예시는 아래와 같음.

```yaml
# reverseproxy.conf
server {
    listen       80;
    server_name  localhost;

    location / {
        proxy_bind 127.0.0.1;
        proxy_pass http://127.0.0.1:3000;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```

```kubectl create configmap nginx-config --from-file=reverseproxy.conf``` 커맨드로 ConfigMap을 생성한 후, 아래와 같이 Pod 객체에 적용

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: helloworld-nginx
  labels:
    app: helloworld-nginx
spec:
  containers:
  - name: nginx
    image: nginx:1.11
    ports:
    - containerPort: 80
    volumeMounts:
    - name: config-volume
      mountPath: /etc/nginx/conf.d
  - name: k8s-demo
    image: wardviaene/k8s-demo
    ports:
    - containerPort: 3000
  volumes:
    - name: config-volume
      configMap:
        name: nginx-config
        items:
        - key: reverseproxy.conf
          path: reverseproxy.conf
```

### Ingresss

외부의 Connection을 Cluster 내부로 허용하는 객체.

LoadBalancer, NodePort의 대안으로 사용하며 예시는 아래와 같음.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: helloworld-rules
spec:
  rules:
  - host: helloworld-v1.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: helloworld-v1
            port:
              number: 80
  - host: helloworld-v2.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: helloworld-v2
            port:
              number: 80
```

### External DNS

External DNS는 인터넷망에 도메인-IP를 추가하여 외부에서 클러스터로 접속할 수 있도록 함.

External DNS와 Ingress를 활용하여 LoadBalancer 비용을 줄일 수 있으며 방법은 아래와 같음.

+ 1. IAM 정책 생성 : AWS Route53에서 External-DNS를 추가할 수 있도록 권한 변경
+ 2. Ingress Service 생성
+ 3. LoadBalancer Service 생성(L4 역할)
+ 4. kOps 설정
+ 5. External DNS 및 ingress rule 적용

### Volumes

Pod 외부에 데이터를 저장하기 위해 사용함.

데이터베이스, 파일 등을 외부(Local Volume, EBS Storage, NFS 등)에 저장하기 위해 사용됨.

AWS EBS를 생성하여 이를 Volume으로 마운트하는 예시는 아래와 같음.

```
# Volume 생성
aws ec2 create-volume --size 10 --region <your-region> --availability-zone <your-zone> --volume-type gp2 --tag-specifications 'ResourceType=volume, Tags=[{Key= KubernetesCluster, Value=kubernetes.domain.tld}]'
```

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: helloworld-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: helloworld
  template:
    metadata:
      labels:
        app: helloworld
    spec:
      containers:
      - name: k8s-demo
        image: wardviaene/k8s-demo
        ports:
        - name: nodejs-port
          containerPort: 3000
        volumeMounts:
        - mountPath: /myvol
          name: myvolume
      volumes:
      - name: myvolume
        awsElasticBlockStore:
          volumeID: # insert AWS EBS volumeID here
```

### Volume Provision

yaml 파일을 통해서 Volume을 자동으로 생성할 수 있음. 방법은 아래와 같음

+ 1. Volume Claim 생성
```yaml
# storage.yaml
kind: StorageClass
apiVersion: storage.k8s.io/v1
metadata:
  name: standard
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp2
  zone: eu-west-1a
```
+ 2. 용량 및 이름 설정
```yaml
kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: db-storage
  annotations:
    volume.beta.kubernetes.io/storage-class: "standard"
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 8Gi
```
+ 3. 자동 생성된 Volume Mount
```yaml
apiVersion: v1
kind: ReplicationController
metadata:
  name: wordpress-db
spec:
  replicas: 1
  selector:
    app: wordpress-db
  template:
    metadata:
      name: wordpress-db
      labels:
        app: wordpress-db
    spec:
      containers:
        # ... 생략
        volumeMounts:
        - mountPath: "/var/lib/mysql"
          name: mysql-storage
      volumes:
        - name: mysql-storage
          persistentVolumeClaim:
            claimName: db-storage # Volume Claims에서 지정한 이름(metadata.name) 사용
```

### Pod Presets

런타임에 Pod로 정보를 주입하기 위해 사용되는 객체

예를 들어, 다수의 Pod를 운영 중인 상황에서 특정 Label을 가진 Pod에만 환경변수 등을 추가하기 위해서 사용됨. 예시는 아래와 같음

```yaml
apiVersion: settings.k8s.io/v1alpha1
kind: PodPreset
metadata:
  name: share-credential
spec:
  selector:
    matchLabels:
      app: myapp
  env:
    - name: MY_SECRET
      value: "123456"
  volumeMounts:
    - mountPath: /share
      name: share-volume
  volumes:
    - name: share-volume
      emptyDir: {}
```

### StatefulSets

상태를 가진 App을 배포하고 관리하기 위해 사용하는 객체임. 주로 DB, 분산 스토리지, 캐시 서버 등에 사용됨.

특징은 아래와 같음.

+ 1. Pod의 고유성: Pod는 고유 이름과 고정 ID를 갖음
+ 2. 순차적 생성 및 종료: 순차적인 시작과 종료를 보장함
+ 3. 고정된 네트워크 ID: Pod는 고유한 DNS 이름을 갖음
+ 4. Persistent Volume: Pod는 고유한 스토리지를 갖음

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: my-app
spec:
  serviceName: "my-app"
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app-container
        image: my-app-image
        volumeMounts:
        - name: my-storage
          mountPath: /data
  volumeClaimTemplates:
  - metadata:
      name: my-storage
    spec:
      accessModes: [ "ReadWriteOnce" ]
      resources:
        requests:
          storage: 1Gi
```

### Daemon Sets

특정 클러스터 내 모든 노드당 1개의 Pod를 실행하기 위해 사용됨. 로깅, 모니터링, 네트워크 플러그인에 사용되며 예시는 아래와 같음.

```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: node-exporter
spec:
  selector:
    matchLabels:
      name: node-exporter
  template:
    metadata:
      labels:
        name: node-exporter
    spec:
      containers:
      - name: node-exporter
        image: prom/node-exporter
        ports:
        - containerPort: 9100
```

### Resource Usage Monitoring

클러스터 모니터링 및 성능 분석을 위해 사용됨. Pod Auto-scaling을 위한 필수 조건임.

Heapster을 통해 REST Endpoint를 제공하고, Heapster는 다양한 Backend와 결합될 수 있음.

모니터링을 위해 ```kubectl top pods```, Metric Server, Grafana 등 다양한 tool과 연동할 수 있음

### Autoscaling

Heapster를 통해 자동으로 Pod를 Scale할 수 있는 객체임.

CPU 사용률을 기반으로 자동으로 Scale하고, 주기적으로 Pod에 Query하여 리소스 사용률을 파악하고 조건에 맞는 경우에 Scale함.

0.2초에 한번씩 쿼리하여 CPU 사용률이 50%를 넘었는지 확인하는 예시는 아래와 같음.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: hpa-example
spec:
  replicas: 3
  selector:
    matchLabels:
      app: hpa-example
  template:
    metadata:
      labels:
        app: hpa-example
    spec:
      containers:
      - name: hpa-example
        image: gcr.io/google_containers/hpa-example
        ports:
        - name: http-port
          containerPort: 80
        resources:
          requests:
            cpu: 200m
---
apiVersion: v1
kind: Service
metadata:
  name: hpa-example
spec:
  ports:
  - port: 31001
    nodePort: 31001
    targetPort: http-port
    protocol: TCP
  selector:
    app: hpa-example
  type: NodePort
---
apiVersion: autoscaling/v1
kind: HorizontalPodAutoscaler
metadata:
  name: hpa-example-autoscaler
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: hpa-example
  minReplicas: 1
  maxReplicas: 10
  targetCPUUtilizationPercentage: 50
```

### Node Affinity & anti-Affinity

Node에 특정 Pod를 배치할 것인지 아닌지를 결정함. NodeSelector보다 더욱 복잡한 방법으로 가능함.







