


1. 인프라 환경 구성

쿠버네티스 노드를 설치하기 위해서는 적절한 서버 인프라 환경을 구성해야 합니다.

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


2. 쿠버네티스 마스터 노드 설정

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





