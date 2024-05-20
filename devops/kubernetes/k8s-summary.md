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
    - 1개 이상의 컨테이너를 갖는 객체
    - Pod는 클러스터 내 고유 IP를 갖음
      - 다만, 재시작 또는 교체될 때 마다 변경됨
    - k8s는 pod를 적절한 워커 노드에 배치함
  - Deployment 객체
    - k8s가 Deployment 객체에 명령을 전달하고, Deployment 객체는 Pod를 생성하여 노드에 배치함
    - Pod당 하나의 Deployment 객체를 갖는다고 볼 수 있음
    - Deployment 객체를 통해 k8s는 pod를 자동으로 적절한 워커 노드에 할당하고, 배포 중 문제 시 롤백하고, 스케일링을 할 수 있음
  - Service 객체
    - pod를 클러스터 내 다른 pod나 외부에 노출시키기 위해 사용함
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























  







