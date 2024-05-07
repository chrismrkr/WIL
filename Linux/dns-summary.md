# DNS

## 설치
- ```yum -y install bind```
- DNS 프로그램 이름 bind / 데몬 이름 : named

## /etc/named.conf 환경 설정 파일
- DNS 서버 주요 환경 설정 파일
- options
  - allow-query : 질의할 수 있는 호스트 지정
  - allow-transfer : 파일 내용을 복사할 대상 정의
  - forward :
      - only : 도메인 주소에 대한 query를 다른 서버로 넘김. 서버 응답이 없는 경우 버림.
      - first : 다른 서버에서 응답이 없을 경우 자신이 응답
  - forwarders : forward할 서버 지정(ex. forwarders: host1; host2;)
- logging : 로깅 방식 설정
- acl : 특정 호스트 IP, 대역 등을 하나의 별칭으로 하여 options(allow-query, forwarders 등)에서 사용할 수 있도록 함
  - ex. acl "ihd" {192.160.0.3; 192.168.146/24; };
  - options { forwarders : ihd; }; 
- zone : 도메인을 관리하기 위한 데이터 파일 지정
  
## /var/named/
- zone 파일 설정 : 도메인, IP, 리소스 간 매핑 정보를 포함한 파일
- zone 파일은 (도메인 -> ip), rev 파일은 (ip -> 도메인)
- named-checkconf [파일경로] : /etc/named.conf 문법 점검
- ;를 이용하여 주석 가능
```
;@는 /etc/named에 설정된 도메인명임
;SOA는 Start Of Authority의 약자로 DNS 핵심 정보를 지정함
;$TTL은 캐시 보관 시간임

$TTL 1D
@  IN SOA ns.ihd.or.kr. kait.ihd.or.kr (
  20240030306  ;Serial
  7200         ;Refresh
) ; 도메인은 ihd.or.kr, 관리자 계정은 kait@ihd.or.kr임
IN NS ns.ihd.or.kr.
IN A 192.168.12.22
IN MX 10 ihd.or.kr.
www IN A 192.168.12.22
www1 IN CNAME www
```

- 아래 방법으로 zone 파일 지정도 가능함

```
zone "[도메인명]" IN {
  type [master|slave|hint]; # hint : 루트 도메인, mater : 1차 네임 서버, slave : 2차 네임 서버
  file "[zone 파일명]";
};
```

- SOA 레코드 : zone 소유자, 메일주소, 유효성 검사 주기 등을 설정함
  - Nameserver
  - Contact_email_address
  - Serial_number
- 주요 레코드 타입
  - A : ipv4 주소
  - CNAME : 도메인 이름 별칭
  - MX : 도메인 이름에 대한 메일 교환 서버
  - NS : 호스트에 대한 공식 네임 서버
