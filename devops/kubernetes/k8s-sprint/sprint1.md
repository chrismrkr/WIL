# K8s Sprint 1

## 1. 인프라 환경 구성

쿠버네티스 노드를 설치하기 위한 호스트의 요구사항은 아래와 같습니다.

- 1. 서버 네트워크 및 리소스 할당
- 2. VM 호스트 네트워크 및 리소스 설정

1-1. 서버 네트워크 및 리소스 할당

1-1-1. 서버 네트워크 설정

외부에서 서버로 접근할 수 있는 공유기가 있고, 공유기의 CIDR을 192.168.219.0/24 라고 가정하겠습니다.

서버로 접근할 수 있는 IP를 위 대역에서 할당하며 이를 192.168.219.100 등 으로 설정할 수 있습니다. 

일반적으로 자동으로 설정되며 서버에 접속하고자 한다면 공유기를 통해 192.168.58.100으로 접근할 수 있습니다. 

1-1-2. 리소스 할당

서버에서 필요한 CPU, Memory, 그리고 파일 시스템을 할당합니다.


1-2. 호스트 네트워크 및 리소스 할당

VM에 리눅스 OS를 설치하며 Host를 생성합니다. Hostname은 쿠버네티스 Master Node로 사용할 예정이므로 K8s-master로 합니다.

1-2-1. 호스트 네트워크 할당

서버에서 VM을 설치하여 네트워크 대역을 192.168.56.0/24로 설정합니다.

Host가 사용할 IP를 설정합니다. VM을 설치하여 호스트 내부적으로 사용할 IP 대역을 192.168.56.0/24로 할당했으므로 Host IP는 192.168.56.30으로 할당할 수 있습니다.

서버에서 192.168.56.30 IP를 통해서 호스트로 접근할 수 있습니다. 호스트에서 외부 인터넷에 접근할 수 있는 NAT IP는 자동으로 설정됩니다.

1-2-2. 리소스 할당

VM에서 사용할 적절한 CPU, Memory 등을 할당합니다.


## 2. 쿠버네티스 마스터 노드 설정

2-1. 쿠버네티스 설치

Master 및 Worker 노드에 공통적으로 적용되는 내용입니다.

2-1-1. 기본 설정

리눅스 패키지 업데이트, 타임존 설정, 방화벽 해제, swap 옵션 비활성화를 적용합니다.

쿠버네티스 전용 포트에 접근하기 위해 방화벽을 해제하는 등의 작업을 수행합니다.

2-1-2. 컨테이너 런타임 설치

VM 에서 실행할 컨테이너 런타임을 설치합니다. 컨테이너 런타임을 통해서 프로세스를 컨테이너로 실행할 수 있고, CRI(컨테이너 런타임 인터페이스)를 통해서 쿠버네티스는 컨테이너를 관리할 수 있습니다.

iptables를 세팅하고, 컨테이너 런타임을 설치한다는 것은 repo 설정 및 containerd 설치를 의미합니다.

repo 설정이란 어느 패키지에서 컨테이너 런타임을 설치할 것인지를 지정해주는 것을 의미합니다.

containerd는 컨테이너 런타임 중 하나로 앞서 설정했던 repo를 통해서 설치를 합니다.

그 이후 CRI(Container Runtime Interface)를 활성화하여 쿠버네티스가 컨테이너를 관리할 수 있도록 합니다.


2-1-3. kubeadm 설치

SELinux를 설치하고, kubelet, kubeadm, 그리고 kubectl 패키지를 설치합니다.

- SELinux : 리눅스 커널 자원에 접근하는 프로세스에 대한 정책을 설정함
- kubelet : VM 노드에 설치되는 에이전트로 컨테이너 런타임와 쿠버네티스를 중계하는 역할을 담당함
- kubeadm : 쿠버네티스 클러스터 초기 세팅용 도구(인증서 생성, etcd 구성 등)
- kubectl : 쿠버네티스 API 서버에 명령을 보내는 CLI 커맨드 도구
  - kubectl -> kube-apiserver -> kubelet -> containerd -> pod


2-2. Master node 세팅

2-2-1. kubeadm을 활용한 클러스터 생성

- 클러스터 초기화
- kubectl 사용 설정
- CNI(Container Network Interface) Plugin 설치 (ex. calico)

2-2-2. 기타 편의기능 설치

kubectl 자동 완성 기능, Dashboard, Metrics Server 등을 설치합니다.

```sh
echo '======== [2-1-1] 기본 설정 시작 ========'
yum install -y yum-utils iproute-tc
yum update openssl openssh-server -y

echo '======== 타임존 설정 ========'
timedatectl set-timezone Asia/Seoul
timedatectl set-ntp true
chronyc makestep

echo '======== hosts 설정 =========='
cat << EOF >> /etc/hosts
192.168.56.30 k8s-master
EOF

echo '======== 방화벽 해제 ========'
systemctl stop firewalld && systemctl disable firewalld

echo '======== Swap 비활성화 ========'
swapoff -a && sed -i '/ swap / s/^/#/' /etc/fstab

echo '======== [2-1-1] 기본 설정 종료 ========'

echo '======== [2-1-2] 컨테이너 런타임 설치 ========'
echo '======== [사전작업] 컨테이너 런타임 설치 전 사전작업 시작 ========'
echo '======== [사전작업 1] iptable 세팅 ========'
cat <<EOF |tee /etc/modules-load.d/k8s.conf
overlay
br_netfilter
EOF

modprobe overlay
modprobe br_netfilter

cat <<EOF |tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables  = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward                 = 1
EOF

sysctl --system

echo '======== [2-1-2] 컨테이너 런타임 (containerd 설치) ========'
echo '======== containerd 패키지 설치 (option2) / docker engine 설치 / repo 설정 ========'
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

echo '======== containerd 설치 ========'
yum install -y containerd.io-1.6.21-3.1.el9.aarch64
systemctl daemon-reload
systemctl enable --now containerd

echo '======== 컨테이너 런타임 : cri 활성화 ========'
containerd config default > /etc/containerd/config.toml
sed -i 's/ SystemdCgroup = false/ SystemdCgroup = true/' /etc/containerd/config.toml
systemctl restart containerd

echo '======== [2-1-3] kubeadm 설치 ========'
echo '======== repo 설정 ========'
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.27/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.27/rpm/repodata/repomd.xml.key
exclude=kubelet kubeadm kubectl cri-tools kubernetes-cni
EOF

echo '======== SELinux 설정 ========'
setenforce 0
sed -i 's/^SELINUX=enforcing$/SELINUX=permissive/' /etc/selinux/config

echo '======== kubelet, kubeadm, kubectl 패키지 설치 ========'
yum install -y kubelet-1.27.2-150500.1.1.aarch64 kubeadm-1.27.2-150500.1.1.aarch64 kubectl-1.27.2-150500.1.1.aarch64 --disableexcludes=kubernetes
systemctl enable --now kubelet

echo '======== kubeadm으로 클러스터 생성  ========'
echo '======== 클러스터 초기화 (Pod Network 세팅) ========'
kubeadm init --pod-network-cidr=20.96.0.0/12 --apiserver-advertise-address 192.168.56.30

echo '======== kubectl 사용 설정 ========'
mkdir -p $HOME/.kube
cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
chown $(id -u):$(id -g) $HOME/.kube/config

echo '======== Pod Network 설치 (Container Network Interface calico) ========'
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/calico-3.26.4/calico.yaml
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/calico-3.26.4/calico-custom.yaml

echo '======== Master에 Pod를 생성 할수 있도록 설정 ========'
kubectl taint nodes k8s-master node-role.kubernetes.io/control-plane-

echo '======== [ETC] 쿠버네티스 편의기능 설치 ========'
echo '======== kubectl 자동완성 기능 ========'
yum -y install bash-completion
echo "source <(kubectl completion bash)" >> ~/.bashrc
echo 'alias k=kubectl' >>~/.bashrc
echo 'complete -o default -F __start_kubectl k' >>~/.bashrc
source ~/.bashrc

echo '======== Dashboard 설치 ========'
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/dashboard-2.7.0/dashboard.yaml

echo '======== Metrics Server 설치 ========'
kubectl create -f https://raw.githubusercontent.com/k8s-1pro/install/main/ground/k8s-1.27/metrics-server-0.6.3/metrics-server.yaml


```

## 3. 모니터링 툴: Prometheus(With Grafana), Loki

쿠버네티스 환경 구성을 완료하였으므로 이를 모니터링할 수 있는 Prometheus(With Grafana)를 설치합니다.

Prometheus와 Loki도 쿠버네티스 Pod로 실행합니다.

- Prometheus : 메트릭 및 로그 수집
- Loki : 로그 저장 데이터베이스
- Grafana : 모니터링 대시보드를 제공


3-1. 설치

Github에서 아래 커맨드를 통해 Loki와 Prometheus를 K8s Pod로 실행할 수 있는 yaml 파일을 다운로드 합니다.


```sh
# 로컬 저장소 생성
git init monitoring
git config --global init.defaultBranch main

cd monitoring
git remote add -f origin https://github.com/k8s-1pro/install.git # K8s Sprint 강좌에서 제공된 github 링크를 활용하였습니다.

git config core.sparseCheckout true
echo "ground/k8s-1.27/prometheus-2.44.0" >> .git/info/sparse-checkout
echo "ground/k8s-1.27/loki-stack-2.6.1" >> .git/info/sparse-checkout

git pull origin main
```

그리고, 아래 커맨드를 실행하여 Loki와 prometheus를 Pod로 설치하고 실행합니다.

```sh
kubectl apply --server-side -f ground/k8s-1.27/prometheus-2.44.0/manifests/setup
kubectl wait --for condition=Established --all CustomResourceDefinition --namespace=monitoring
kubectl apply -f ground/k8s-1.27/prometheus-2.44.0/manifests

kubectl get pods -n monitoring
```

```sh
kubectl apply -f ground/k8s-1.27/loki-stack-2.6.1

kubectl get pods -n loki-stack
```

3-2. Grafana 접속

호스트 IP를 192.168.56.30으로 할당했으므로 http://192.168.56.30:30001를 입력하여 접속할 수 있습니다.

(id: admin / pw: admin)

접속할 수 있는 포트는 Prometheus Service Object를 생성하는 yaml 파일을 통해서 확인할 수 있습니다.


## 4. 쿠버네티스 Object

어플리케이션을 쿠버네티스 환경에서 실행하기 위해서는 아래와 같이 다양한 사항을 고려해야 합니다.

- 어플리케이션을 몇 개의 컨테이너로 실행할 것인가?
- 외부에서 어떤 포트로 접근할 수 있도록 할 것인가?
- 디스크 볼륨은 어떻게 마운트할 것인가? 등등

이처럼, 어플리케이션을 쿠버네티스 환경에서 실행하기 위해 필요한 것을 ```k8s Object```라고 합니다.

앞서 실행한 Prometheus도 결국 적절한 k8s Object들을 생성 및 조합하여 실행된 것이라고 볼 수 있습니다.


4-1. Object 살펴보기

Object에 대해서 하나씩 깊게 들어가기 보다는, 먼저 전체적으로 어떤 것이 있는지 살펴보겠습니다.

어플리케이션을 개발하여 운영환경에 배포하기 위해서는 아래 사항을 고려해야 합니다.

- 어플리케이션을 어떤 정책으로 배포할 것인가? (컨테이너 개수, 최대 메모리, 최대 CPU 등)
- 외부에서 어떤 포트로 어플리케이션에 접근할 수 있는가?
- 어플리케이션 설정 파일을 어떻게 관리할 것인가?
- 어플리케이션의 영구 저장 공간(디스크)을 어떻게 마운트할 것인가?
- 어플리케이션 트래픽이 증가하면 이에 대해서 어떤 정책으로 관리할 것인가?

5가지를 우선 고려할 수 있는데, 쿠버네티스에서는 Deployment, Service, (ConfigMap, Secret), (PVC, PV), HPA Object를 활용해서 위 요구사항을 만족시킬 수 있습니다.

위 오브젝트를 생성하기 위해 아래와 같은 yaml 파일을 활용할 수 있습니다.

```yaml
# Namespace

apiVersion: v1
kind: Namespace
metadata:
  name: anotherclass-123
  labels:
    part-of: k8s-anotherclass
    managed-by: dashboard
```

```yaml
#Deployment

apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: anotherclass-123
   
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard

spec:
  selector:
    matchLabels:
      part-of: k8s-anotherclass
      component: backend-server
      name: api-tester
      instance: api-tester-1231
  replicas: 2
  strategy:
    type: RollingUpdate
  template:
    metadata:
      labels:
        part-of: k8s-anotherclass
        component: backend-server
        name: api-tester
        instance: api-tester-1231
        version: 1.0.0
    spec:
      nodeSelector:
        kubernetes.io/hostname: k8s-master
      containers:
        - name: api-tester-1231
          image: 1pro/api-tester:v1.0.0
          ports:
          - name: http
            containerPort: 8080
          envFrom:
            - configMapRef:
                name: api-tester-1231-properties
          startupProbe:
            httpGet:
              path: "/startup"
              port: 8080
            periodSeconds: 5
            failureThreshold: 36
          readinessProbe:
            httpGet:
              path: "/readiness"
              port: 8080
            periodSeconds: 10
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: "/liveness"
              port: 8080
            periodSeconds: 10
            failureThreshold: 3
          resources:
            requests:
              memory: "100Mi"
              cpu: "100m"
            limits:
              memory: "200Mi"
              cpu: "200m"
          volumeMounts:
            - name: files
              mountPath: /usr/src/myapp/files/dev
            - name: secret-datasource
              mountPath: /usr/src/myapp/datasource
      volumes:
        - name: files
          persistentVolumeClaim:
            claimName: api-tester-1231-files
        - name: secret-datasource
          secret:
            secretName: api-tester-1231-postgresql
```

```yaml
# Service

apiVersion: v1
kind: Service
metadata:
  namespace: anotherclass-123
  name: api-tester-1231
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
spec:
  selector:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
  ports:
    - port: 80
      targetPort: http
      nodePort: 31231
  type: NodePort
```

```yaml
# ConfigMap, Secret

apiVersion: v1
kind: ConfigMap
metadata:
  namespace: anotherclass-123
  name: api-tester-1231-properties
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
data:
  spring_profiles_active: "dev"
  application_role: "ALL"
  postgresql_filepath: "/usr/src/myapp/datasource/postgresql-info.yaml"

---

apiVersion: v1
kind: Secret
metadata:
  namespace: anotherclass-123
  name: api-tester-1231-postgresql
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
stringData:
  postgresql-info.yaml: |
    driver-class-name: "org.postgresql.Driver"
    url: "jdbc:postgresql://postgresql:5431"
    username: "dev"
    password: "dev123"

```

```yaml
# PVC, PV

apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  namespace: anotherclass-123
  name: api-tester-1231-files
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: kubectl
spec:
  resources:
    requests:
      storage: 2G
  accessModes:
    - ReadWriteMany
  selector:
    matchLabels:
      part-of: k8s-anotherclass
      component: backend-server
      name: api-tester
      instance: api-tester-1231-files
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: api-tester-1231-files
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231-files
    version: 1.0.0
    managed-by: dashboard
spec:
  capacity:
    storage: 2G
  volumeMode: Filesystem
  accessModes:
    - ReadWriteMany
  local:
    path: "/root/k8s-local-volume/1231"
  nodeAffinity:
    required:
      nodeSelectorTerms:
        - matchExpressions:
            - {key: kubernetes.io/hostname, operator: In, values: [k8s-master]}
```

```yaml
# HPA

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  namespace: anotherclass-123
  name: api-tester-1231-default
  labels:
    part-of: k8s-anotherclass
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: api-tester-1231
  minReplicas: 2
  maxReplicas: 4
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 120
```

위를 훑어보면, yaml 파일을 통해 생성되는 Object가 어떤 기능을 하게 될지 짐작할 수 있습니다.


4-2. labels, selector, naming

위 Object 생성을 위한 yaml 파일을 살펴보면, labels, selector, naming 속성이 공통적으로 있었습니다.

4-2-1. labels

labels는 오브젝트에 일종의 tag를 추가하는 것이며 App 정보를 바로 파악하기 위해 사용합니다.

예를 들어, Deployment Object는 어떤 Service Object와 연동되어 사용될지 결정이 되어야 합니다. 이를 위해 labels가 활용됩니다.

labels 명명 규칙은 보편적으로 아래와 같습니다.

- part-of : 어플리케이션 전체 이름
- component : 어플리케이션에서 어떤 구성요소와 관련이 있는지 (ex. backend, frontend)
- name : 어플리케이션 구성요소의 이름
- instance : 인스턴스 명
- version : 버전
- Managed-by : 어떻게 관리되는지 (ex. kubectl, dashboard)

4-2-2. selector

Deployment는 어떤 Service와 연동이 되어야할지 결정이 필요하고, labels를 참조할 수 있다고 하였습니다.

selectors는 어떤 labels를 참조할지를 결정하기 위한 속성입니다. 그러므로, selector는 labels와 연동되는 속성이라고 볼 수 있습니다.

4-2-3. name

HPA와 PV,PVC 오브젝트를 보면, labels가 아닌 name과 바로 연결되는 경우도 있습니다.

이처럼, (selector, labels)와 유사하게 name으로 직접 연결하기 위해 사용할 수 있는 속성입니다.

동일한 Namespace 내에서 서로 다른 종류의 Object라면 이름을 중복해서 사용할 수 있습니다. 그러나, 동일한 종류의 Object라면 동일한 Namespace 내에서는 중복해서 사용할 수 없습니다.

PV는 네임스페이스가 아닌 클러스터 레벨에서 생성되는 오브젝트이므로 name 생성 중복에 주의해야 합니다.

4-2-4. labels - selector 연동 예시

Deployment 오브젝트를 생성하는 yaml 파일의 일부를 확인해보면, Labels와 Selector가 어떻게 활용되는지 감을 잡을 수 있습니다.

배포를 위해서는 어플리케이션이 몇개의 컨테이너를 띄울지, 그리고 컨테이너는 어떤 속성을 가질지를 결정해야 합니다. 이에 대응 되는 것이 각각 ReplicaSet, Pod 입니다.

Deployment - ReplicaSet - Pod는 Labels와 Selector 속성을 통해 연동됩니다.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: my-namespace
   
  labels:
    app: my-app
spec:
  selector:
    matchLabels:
      app: my-app
  replicas: 2
  strategy:
    type: RollingUpdate
  template:
    metadata:
      labels:
        app: my-app
```

Deployment 오브젝트의 spec.selector.matchLabels 속성을 통해서 적절한 label(template.metadata.labels)을 가진 Pod와 연동되는 것을 확인할 수 있었습니다.

Replicaset은 yaml 파일에서는 명시적으로 드러나지는 않지만, Deployment Controller가 암묵적으로 생성합니다.


4-2. Object : Pod

쿠버네티스 Object를 훑어보았으니 개별 Object에 대해서 좀 더 상세하게 확인해보겠습니다.






















