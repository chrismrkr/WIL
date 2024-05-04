# DHCP
클라이언트 호스트가 사용할 IP 주소, 게이트웨이 주소, 네임서버 주소 등을 동적으로 할당하는 기술

## 동작 원리
- DHCP 클라이언트는 DHCP 서버를 찾기 위해 DHCP Discover 브로드캐스트
- DHCP 서버는 클라이언트에 ip 주소, 서브넷, 게이트웨이, DNS 정보 전달
- 클라이언트는 제안 받은 IP 주소 등 정보를 수락 후 브로드캐스트(제안 수락 브로드캐스트)
- DHCP 서버는 해당 클라이언트가 어떤 IP를 사용하는지를 기록

## 설치 및 환경설정 파일 확인
- yum -y install dhcp
- rpm -qc dhcp
- man dhcpd.conf

## 특정 호스트에 고정 IP 할당하기
```
host [호스트명] {
  hardware ethernet aa:aa:32:54:c1:a4;
  fixed-address 192.168.12.22;
}
```

## 동적 IP 할당
- 클라이언트에 할당할 IP 주소 대역을 서브넷과 서브넷 정보와 함께 지정
```
subnet 192.168.10.0 netmask 255.255.255.0 {
  range 192.168.10.0 192.168.10.200;
  option domain-name "...";
  option domain-name-server "...";
  option routers [ip];
  option broadcase-address [ip];
  default-lease-time  [time(sec)];
  max-lease-time  [time(sec)];
}
```
