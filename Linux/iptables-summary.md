# iptables
패킷 필터링 정책을 통해 특정 패킷 분석, 허용 및 차단

## 형식
```
iptables [-t 테이블 이름] [action] [체인이름] [match 규칙] [-j 타겟]
```

## 테이블
- filter : 패킷 필터링 기능 담당
- nat : IP 주소 및 포트를 변환하고 관리함. 하나의 공인 IP를 여러 호스트가 사용하고자 할 때 주로 사용함
- mangle : 패킷 변경을 위해 사용
- raw : 연결 추적을 위해 사용
- security

## 체인
- filter
  - INPUT : 유입되는 패킷을 필터링하는 체인
  - FORWARD : 라우터로 사용되는 호스트를 통과하는 패킷
  - OUTPUT : 출발지로 하여 나가는 패킷 필터링
- nat
  - PREROUTING : 도착지 주소 변경
  - POSTROUTING : 패킷의 출발지 주소 변경
  - INPUT : 로컬로 들어오는 패킷 주소 변경
  - OUTPUT : 로컬에서 생성되어 밖으로 나가는 패킷 주소 변경

## action
- man iptables의 COMMAND 참고

## match 규칙
- man iptables의 PARAMETERS 참고

## 타겟
- ACCEPT : 패킷 허가
- REJECT : 패킷을 거부하며 상대에게 응답 메세지 전송
- DROP : 패킷을 버림
- LOG : 패킷을 syslog에 전달(/var/log/message)
- RETURN : 체인 내에서 패킷 처리 계속 진행










