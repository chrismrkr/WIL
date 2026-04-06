# K8s Sprint 2

CI/CD 서버를 구축하여 소스와 k8s 오브젝트를 배포하기 위한 방법에 대해서 학습합니다.

CI/CD 에서 진행되는 프로세스를 간단히 정리해보면 아래와 같습니다.

- 빌드된 소스를 Dockerfile을 활용하여 Docker Image로 생성하여 Docker Hub 또는 Registry에 저장
- 오브젝트 관련 yaml 파일을 활용하여 쿠버네티스에 배포

이에 대한 구체적인 내용에 대해서 학습하고 정리해보겠습니다.

# 1. CI/CD 서버 구축

첫번째, CI/CD 서버에 아래 스크립트를 실행하여 필요한 모듈을 설치하고 환경 설정합니다.

```sh
echo '======== [1] Rocky Linux 기본 설정 ========'
echo '======== [1-1] 패키지 업데이트 ========'
# 강의와 동일한 실습 환경을 유지하기 위해 Linux Update는 하지 마세요!
# yum -y update # (x)

echo '======== [1-2] 타임존 설정 ========'
timedatectl set-timezone Asia/Seoul

echo '======== [1-3] 방화벽 해제 ========'
systemctl stop firewalld && systemctl disable firewalld


echo '======== [2] Kubectl 설치 ========'
echo '======== [2-1] repo 설정 ========'
cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.27/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.27/rpm/repodata/repomd.xml.key
exclude=kubelet kubeadm kubectl cri-tools kubernetes-cni
EOF

echo '======== [2-2] Kubectl 설치 ========'
yum install -y kubectl-1.27.2-150500.1.1.aarch64 --disableexcludes=kubernetes


echo '======== [3] 도커 설치 ========'
# https://download.docker.com/linux/centos/8/x86_64/stable/Packages/ 저장소 경로
yum install -y yum-utils
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install -y docker-ce-3:23.0.6-1.el9.aarch64 docker-ce-cli-1:23.0.6-1.el9.aarch64 containerd.io-1.6.21-3.1.el9.aarch64
systemctl daemon-reload
systemctl enable --now docker

echo '======== [4] OpenJDK 설치  ========'
yum install -y java-17-openjdk

echo '======== [5] Gradle 설치  ========'
yum -y install wget unzip
wget https://services.gradle.org/distributions/gradle-7.6.1-bin.zip -P ~/
unzip -d /opt/gradle ~/gradle-*.zip
cat <<EOF |tee /etc/profile.d/gradle.sh
export GRADLE_HOME=/opt/gradle/gradle-7.6.1
export PATH=/opt/gradle/gradle-7.6.1/bin:${PATH}
EOF
chmod +x /etc/profile.d/gradle.sh
source /etc/profile.d/gradle.sh

echo '======== [6] Git 설치  ========'
# 기존엔 git-2.43.0-1.el8 버전을 Fix하였으나 Repository에 최신 버전만 업로드 됨으로 수정
yum install -y git

echo '======== [7] Jenkins 설치  ========'
wget https://archives.jenkins.io/redhat-stable/jenkins-2.479.3-1.1.noarch.rpm
rpm -ivh jenkins-2.479.3-1.1.noarch.rpm
systemctl enable jenkins
systemctl start jenkins

# Jenkns 설치 후 OpenSSL 최신 버전으로 업데이트
yum update -y openssh-server openssh-clients openssl openssl-libs
systemctl restart sshd
```

두번째, Jenkins 초기 설정을 진행합니다. 아래 명령어를 입력하여 초기 비밀번호를 확인하여 로그인 합니다.

```sh
cat /var/lib/jenkins/secrets/initialAdminPassword
```

서버에 설치된 JDK과 Gradle를 세팅합니다. Jenkins 포털 페이지의 [Dashboard] > [Jenkins 관리] > Tools 에서 설정할 수 있습니다.

또한, Jenkins 스크립트 내에서 docker 명령어를 사용하므로 아래와 같이 Jenkins 계정에도 docker 실행 권한을 부여합니다.

```sh
# jeknins가 Docker를 사용할 수 있도록 권한 부여
[root@cicd-server ~]# chmod 666 /var/run/docker.sock
[root@cicd-server ~]# usermod -aG docker jenkins

# Jeknins로 사용자 변경 
[root@cicd-server ~]# su - jenkins -s /bin/bash

# 자신의 Dockerhub로 로그인 하기
[jenkins@cicd-server ~]$ docker login
Username: 
Password: 
```

ㄸ

## 1. Jenkins 파이프라인을 활용한 소스 빌드 및 K8s 오브젝트 배포




