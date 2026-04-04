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

위 오브젝트를 생성하기 위해 아래와 같은 yaml 파일을 활용할 수 있습니다. 쿠버네티스 Dashboard에서 아래 yaml 파일을 업로드하여 실행할 수 있습니다.

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

생성되는 오브젝트와 그것의 필드를 살펴보면, yaml 파일을 통해 생성되는 Object가 어떤 기능을 하게 될지 짐작할 수 있습니다.


4-2. labels, selector, naming

위 Object 생성을 위한 yaml 파일을 살펴보면, labels, selector, naming 속성이 공통적으로 있었습니다.

4-2-1. labels

labels는 오브젝트에 일종의 tag를 추가하는 것이며 App 정보를 바로 파악하기 위해 사용합니다.

예를 들어, Service Object는 외부 트래픽을 Pod로 전달하기 위한 오브젝트로 어떤 Pod와 연동되어 사용될지 결정이 되어야 합니다.

이를 위해 Service Object는 Selector를 이용해서 Pod의 labels를 참조합니다.

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


4-3. Object : Pod

Object를 전체적으로 훑었으므로 하나씩 상세히 알아보겠습니다. 첫번째는 Pod 입니다.

Deployment 오브젝트가 생성될 때 Pod 오브젝트도 함께 생성되며 어플리케이션이 실제로 기동되는 오브젝트를 의미합니다.

Deployment 오브젝트를 생성하는 yaml 파일에서 Pod를 생성하는 부분은 아래와 같습니다.

```yaml
# Deployment 오브젝트 내 Pod에 해당되는 설정

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
```

Deployment의 name이 ```deployment-123```이라면, Replicaset은 ```deployment-123-xxx```, Pod는 ```deployment-123-xxx-yyy```와 같이 자동으로 이름이 지정됩니다.

startupProbe, readinessProbe, livenessProbe 3가지 속성의 역할은 다음과 같습니다.

4-3-1. startupProbe

어플리케이션이 정상적으로 실행되었는지를 확인하는 API를 설정하는 속성입니다.

위 예시에서는 GET /ready API를 5초 주기로 최대 36번까지 호출하고, 그 안에 성공 응답이 오면 어플리케이션이 정상적으로 기동되었음을 의미합니다.

성공 응답을 받으면 startupProbe는 종료됩니다.

이 시점에 DB 연결, Spring 컨텍스트 초기화, Jar 실행 등이 이루어집니다.

4-3-2. livenessProbe

어플리케이션이 실행 중인지를 확인하는 API를 설정하는 속성입니다.

startupProbe가 완료된 이후에 실행되며, 위 예시에서는 GET /liveness를 10초 주기로 3번 호출해서 그 안에 응답을 받아야 성공이라는 것을 의미합니다.

만약 실패한다면, 쿠버네티스를 장애 상황으로 간주하여 Pod를 재시동합니다.

4-3-3. readinessProbe

어플리케이션이 외부로부터 요청을 받을 준비가 되었는지를 확인하는 API를 설정하는 속성입니다.

startupProbe가 완료된 이후에 동작하며, 위 예시에서는 GET /readiness를 10초 주기로 3번 호출하여 그 안에 성공 응답을 받아야 정상 상태임을 의미합니다.

만약 실패한다면, 외부서의 요청이 Pod로 도달하지 않는 다는 것을 의미하므로 Service - Deployment 오브젝트 사이 재연결을 시도합니다.

네임스페이스 내부에서는 정상이지만, 외부에서 호출했을 때 정상인지를 확인하기 위해서는 아래의 커맨드를 활용할 수 있습니다.

```sh
kubectl exec -n [namespace] -it [pod-name] -- curl http://localhost:8080/hello
curl -X GET http://192.168.56.30:31231/hello
```

4-3-4. livenessProbe, readinessProbe 전략

livenessProbe 주기는 readiness Probe 주기보다 길게 하는 것이 바람직합니다.

Pod 내 어플리케이션이 잠깐 일시적으로 장애가 발생하는 상황에 livenessProbe의 주기가 짧고 Threshold가 낮게 설정되어 있다고 가정해보겠습니다.

어플리케이션이 자체적으로 정상으로 돌아올 수 있었지만, livenessProbe 주기 짧고 임계치가 낮다면 쿠버네티스는 Pod를 재기동할 수 있습니다.

이를 방지하기 위해서는 liveness 주기를 길게 하는 것도 방법입니다. 하지만, 정말로 장애 상황이 발생한다면, Pod가 늦게 재기동 된다는 단점이 있습니다.

이를 해결하기 위해서 readiness 주기는 짧게하여 장애가 감지된다면, 먼저 빠르게 외부로 부터의 요청을 끊어내고, 짧은 주기로 readinessProbe를 진행하며 성공하면 다시 연결을 하도록 합니다.


4-4. Object : ConfigMap, Secret

두가지 모두 Pod에서 기동된 어플리케이션의 환경변수를 설정하기 위해서 사용되기도 하지만 그것의 동작 방식에는 차이가 있습니다.

그렇기 때문에 동작 방식의 차이를 명확히 이해하고, 기술적 특징에 맞추어 적절히 사용해야 합니다.

ConfigMap과 Secret 모두 Pod에서 사용할 수 있습니다. Pod의 yaml 설정에서 ConfigMap과 Secret에 해당되는 부분은 아래와 같습니다.

```yaml
  template:
    metadata:
      labels:
        part-of: k8s-anotherclass
        component: backend-server
        name: api-tester
        instance: api-tester-1231
        version: 1.0.0
    spec:
      containers:
          envFrom:
            - configMapRef:
                name: api-tester-1231-properties
          volumeMounts:
            - name: secret-datasource
              mountPath: /usr/src/myapp/datasource
      volumes:
        - name: secret-datasource
          secret:
            secretName: api-tester-1231-postgresql

```

configMap은 spec.containers.envFrom 속성을 활용해서 지정할 수 있습니다.

secret은 spec.voumnes.secret 속성을 활용해서 지정할 수 있으며 spec.containers.volumeMounts 속성을 이용해서 디스크 어느 위치에 마운트할지 결정할 수 있습니다.

configMap과 secret 오브젝트를 생성하는 yaml 파일은 아래와 같습니다. configMap과 secret 오브젝트 모두 쿠버네티스 대시보드에서 생성할 수 있습니다.

```yaml
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

위 값을 kubectl을 활용해서 아래와 같이 조회할 수 있습니다.

```sh
// Configmap 확인
kubectl describe -n [namespace] configmaps [configmap-name]
kubectl get -n [namespace] configmaps [configmap-name] -o yaml
kubectl get -n [namespace] configmaps [configmap-name] -o jsonpath='{.data}'

// Secret 확인
kubectl get -n [namespace] secret [secret-name] -o yaml
kubectl get -n [namespace] secret [secret-name] -o jsonpath='{.data}'

// Secret data에서 postgresql-info가 Key인 Value값만 조회 하기
kubectl get -n [namespace] secret [secret-name] -o jsonpath='{.data.postgresql-info\.yaml}'

// Secret data에서 postgresql-info가 Key인 Value값을 Base64 디코딩해서 보기
kubectl get -n [namespace] secret [secret-name] -o jsonpath='{.data.postgresql-info\.yaml}' | base64 -d
```


4-4-1. ConfigMap

configMap의 내용은 인코딩 되지 않으며 Pod의 환경변수 env로 주입이 됩니다.

쿠버네티스 Dashboard를 통해서도 확인할 수 있으며 kubectl을 활용해서 아래 커맨드를 입력해서 확인할 수 있습니다.

```sh
kubectl exec -n [namespace] -it [pod-name] -- env
```

일반적으로 환경변수 env에 저장된 값을 활용하여 어떤 application.properties 파일을 참조할지가 결정되기도 합니다.

4-4-2. Secret

Secret은 ConfigMap과 달리 Base64로 인코딩되어 그 내용이 secret 오브젝트 내에 저장됩니다.

그리고, Pod의 spec.containers.volumeMounts 속성을 통해서 Secret의 내용이 Pod의 어느 위치에 저장이 될지 결정됩니다. 

Base64로 인코딩 되었던 내용은 마운트된 위치에 저장됩니다.

Secret은 Volume Mount 되어 있으므로 쿠버네티스가 Secret 오브젝트를 주기적으로 확인하여 내용 변경이 있다면 그것을 Pod로 동기화를 합니다.

쿠버네티스 Dashboard에서도 확인 가능하고, kubectl 명령어를 통해서도 secret을 확인할 수 있습니다.

```sh
kubectl exec -n [namespace] -it [pod-name] -- cat /usr/src/myapp/datasource/postgresql-info.yaml
kubectl exec -n [namespace] -it [pod-name] -- jps -v
```

4-4-3. Secret 활용성

Secret은 이름과 다르게 별도의 암호화가 되는 오브젝트가 아닌 것을 알 수 있었습니다.

그럼에도 Secret을 사용해야 하는 이유는 Base64 암호화-복호화에 있습니다. Secret에는 docker-registry, tls 인증서 등을 저장할 수 있는데 바이너리 파일인 경우에는 문자열로 저장이 불가능합니다.

그러므로, Base64를 활용해서 Pod에 그 내용을 전달하기 위해 활용이 가능합니다.

Secret은 내용에 대한 암호화를 하지 않으며, 암호화가 필요한 경우에는 별도의 암호화 방법을 고려해야 합니다.

4-5. Object : PVC, PV

어플리케이션을 Pod로 실행한 후, 특정 데이터는 Pod 종료와 함께 휘발되지 않고 영구적으로 저장되는 것이 필요할 수 있습니다.

이때 사용하는 것이 PVC, PV 오브젝트 입니다. Pod는 volumes 속성을 활용해서 PVC 오브젝트를 설정하고, PVC 오브젝트는 Selector 속성을 활용해서 PV 오브젝트의 label을 찾아 연동합니다.

예시는 아래와 같습니다.

```yaml
# Pod 설정 일부 : name이 api-tester-1231-files인 PVC를 /usr/src/myapp/files에 마운트함
          volumeMounts:
            - name: files
              mountPath: /usr/src/myapp/files/dev
      volumes:
        - name: files
          persistentVolumeClaim:
            claimName: api-tester-1231-files
```

```yaml
# PVC / PV 설정 일부 :
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  namespace: anotherclass-123
  name: api-tester-1231-files
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

PV를 마운트한 pod의 /usr/src/myapp/files/dev 디렉토리에 특정 파일을 생성한 후, 아래의 명령으를 통해서 PV에 저장되었는지 확인할 수 있습니다.

```sh
kubectl exec -n <namespace> -it <pod-name> -- ls /usr/src/myapp/files/dev
ls /root/k8s-local-volume/1231
``

물론, PV, PVC를 사용하지 않고 Pod의 volumes.hostPath 설정을 활용해서도 노드의 로컬 경로에 마운트를 할 수 있습니다.

Grafana의 Loki가 이 방법을 사용하여 노드의 /var/log/pods 경로에 로그를 저장합니다.



4-6. Object : Deployment

Deployment Object의 startegy 속성은 어떤 방식으로 ReplicaSet을 배포할지를 결정합니다. Recreate, RollingUpdate 2가지 방식이 있습니다.

Pod의 Template 속성 중 하나라도 변경이 되면 ReplicaSet은 다시 배포됩니다.

4-6-1. Recreate

기존 ReplicaSet을 삭제하고 새로운 ReplicaSet을 생성합니다. ReplicaSet이 새롭게 생성되는 동안 서비스가 중단되는 단점이 있습니다.

4-6-2. RollingUpdate

적절한 비율로 기존 Pod를 중지하면서 동시에 새로운 Pod를 배포하는 전략입니다. 예시는 아래와 같습니다.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: anotherclass-123
  name: api-tester-1231
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 25%
      maxSurge: 25%
```

maxUnavailable 속성은 업데이트 중에 기존에 서비스되던 Pod를 얼마나 유지할지를 결정합니다.

예를 들어, maxUnavailable이 25%이면, 기존에 실행되던 Pod 중 25%는 중지되는 것을 의미합니다. 

만약 0% 라면, 새로운 Pod가 1개 배포 완료되고 난 이후에 기존 Pod가 1개 중지되는 방식으로 동작을 합니다. 그러므로, 배포 시점에 자원 사용률이 기존보다 2배 더 증가합니다.

maxSurge 속성은 새 Pod를 동시에 얼마나 배포할지를 결정합니다. 이를 확인할 수 있는 방법은 아래와 같습니다.


4-7. Object : Service

Pod가 외부로 부터 트래픽을 받을 수 있도록 만드는 오브젝트입니다.

```yaml
# Service 설정 일부

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

위 labels를 갖는 Pod를 선택한 후, 노드의 31231 포트로 전달되는 트래픽은 Pod의 80 포트로 전달한다는 것을 의미합니다.

아래와 같이 테스트해볼 수 있습니다. 쿠버네티스 내부 DNS를 활용하여 서비스 이름으로 API를 호출할 수 있습니다.

또한, 같은 노드 내에서 다른 네임스페이스에 속하는 Pod를 호출하고자 하여도 아래와 같이 서비스 디스커버리를 활용할 수 있습니다.

```sh
curl http://192.168.56.30:31231/hello
kubectl exec -n <namespace> -it <pod-name> -- curl http://api-tester-123:80/hello
kubectl -exec -n <another-namespace> -it <another-pod> -- curl http://api-tester-123.<namespace>:80/hello
```


4-8. Object : HPA(Horizontal Pod Autoscaler)

컨테이너의 평균 CPU 사용량이 일정 수준을 넘으면 자동으로 Scale-Out 하는 것을 돕는 오브젝트입니다. 

```yaml
# HPA 설정 일부

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
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
    scaleDown:
      stabilizationWindowSeconds: 600
```

spec.scaleTargetRef 속성읉 통해서 어떤 Pod를 모니터링할지 결정합니다. 

spec.behavior.scaleUp.stabilizationWindowSeconds 동안 평균 CPU 사용률이 spec.metrics.resource.target.averagleUtilization 이상이면 컨테이너를 추가 실행합니다.

그리고, 평균 CPU 사용량이 다시 감소하더라도 spec.behavior.scaleDown.stabilizationWindowSeconds 만큼 증설된 컨테이너를 유지하고 종료합니다.



