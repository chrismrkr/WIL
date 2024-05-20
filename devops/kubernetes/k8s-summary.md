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

## Minikube로 쿠버네티스 실습 1 : 클러스터 내 Pod 배포하기
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













  







