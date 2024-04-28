# NFS
Network File System

## 서비스 설치
- ```yum -y install rpcbind nfs-utils```
- ```rpm -qc nfs-utils```

## NFS 서버 설정
- /etc/exportfs 파일에서 설정함
  - echo "/var/test-nfs 172.30.0.0/24(rw,no_root_squash)" >> /etc/exports
    - [서버 디렉토리] [접속 허가 클라이언트 호스트](옵션)
    - 옵션 종류
      - root-squash : root 접근 권한 거부
      - no-root-squash: root 접근 권한 허용
  - mkdir /var/test-nfs
  - chmod 666 /var/test-nfs
- NFS 관련 데몬 실행 : ```systemctl start rpcbind nfs-server``` ```systemctl enable rpcbind nfs-server```
- NFS 서버 관련 명령어
  - exportfs : export된 디렉토리 정보, 즉 공유 디렉토리 목록 확인

## NFS 서비스 이용하기
- 클라이언트 호스트에 원격 서버의 디렉토리를 마운트
  - ```mount -t nfs [NFS 서버 ip]:[NFS 서버 디렉토리 경로] [마운트시킬 로컬 디렉토리 경로]```
  - /etc/fstab에서도 설정 가능
    - ```[NFS 서버 ip]:[서버 디렉토리 경로] [로컬 디렉토리 경로] nfs ... ```
- NFS 클라이언트 관련 명령어
  - showmount : NFS 서버에서 export된 정보 확인


