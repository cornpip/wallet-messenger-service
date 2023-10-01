# Wallet-Messenger-Service
대용량 채팅 트래픽을 처리할 수 있는 전자 지갑 메신저 서비스


## 기획/설계
[__ERD__](https://drive.google.com/file/d/1nBDsU-hqtFnShWmwQIzq7DGtEUvM4OBp/view?usp=sharing)

[__API JSON(Swagger Editor 로 확인)__](https://drive.google.com/file/d/1Hgo6lQtKiWRtMZcfOaxvvgNLZPNysiMy/view?usp=sharing)

[__API Swagger-ui(localhost 로 확인)__](http://localhost:8080/swagger-ui/index.html#/)

[__Wire Frame__](https://www.figma.com/file/vAopCC9aoIwLBkEUXbYimA/Wallet-Messenger-Service-WireFrame?type=design&mode=design&t=2ix1w3yDTcntv2pB-1)

## 클라이언트
[__Messenger-Service-Log__](https://github.com/cornpip/messenger-service-log)

## Message WebSocket Server 분리

[__Message WebSocket Server__](https://github.com/cornpip/message_kafka)

## Message Architecture
[__Message Architecture__](https://github.com/cornpip/message_architecture)

## 기능
+ user
  + crud
  + 로그인(토큰 쿠키)
+ contact(친구 목록)
  + crud
+ channel(채팅방)
  + crud
  + 유저가 속한 채널 목록 조회
  + 채널에 다른 유저 초대/추방
  + 특정 채널에 가입
  + 키워드로 채널 검색
+ channel-content(채팅방에서 텍스트,파일 등)
  + 해당 채널 컨텐츠 전체 조회
  + 해당 채널 컨텐츠 slice 조회
+ wallet(전자 지갑)
  + 송금
  + 예약 송금
+ personal-wallet(개인 지갑)
  + crud
  + 내 지갑에 입금
  + 거래 내역 조회
  + 예약 내역 조회
  + 내가 속한 모든 group-wallet 조회
+ group-wallet(그룹 지갑)
  + crud
  + 거래 내역 조회
  + 예약 내역 조회
  + group-wallet 전체 참여자 조회
  + 해당 채널에 모든 group-wallet 조회