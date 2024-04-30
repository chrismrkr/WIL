# Squid
서버 데이터를 캐시하여 응답속도를 높이기 위해 사용하는 서비스

## 관련 패키지 설치 및 확인
- rpm -qc squid
- yum -y install squid
- man /etc/squid/squid.conf
- /etc/squid/squid.conf.default에서 확인 가능

## 주요 설정
- http_port [포트번호] : 요청 수신할 포트 설정
- cache_dir ufs [캐시 파일 저장 디렉토리] [크기(MB)] [1-level 디렉토리 수] [2-level 디렉토리 수]
  - ex. cache_dir ufs /var/spool/squid 1000 16 256
    - /var/spool/squid 디렉터리에 1000MB 용량을 할당하고 1레벨 디렉토리는 16개, 2레벨 디렉토리는 256개
- acl : 리소스에 대한 별칭을 설정함
  - acl [별칭] src [ip 주소 대역]
  - acl [별칭] dst [ip 주소 대역]
  - acl [별칭] port [포트 번호]
  - acl [별칭] srcdomain [도메인 이름]
- http_access [allow | deny] [별칭] : 별칭에 대한 접속 권한 설정
- cache_mem [크기] : 캐시 크기 설정
- cache_log [로그 파일 경로] : 로그 파일 지정
