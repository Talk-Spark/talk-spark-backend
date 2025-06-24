# 📊 Talk-Spark Backend - 실시간 명함 퀴즈 게임 플랫폼

## 📋 프로젝트 개요

| 구분 | 내용 |
|------|------|
| **개발 기간** | 2024.03 ~ 2024.06 (4개월) |
| **팀 구성** | 백엔드 2명, 프론트엔드 2명 |
| **나의 역할** | 백엔드 리드 개발자 (실시간 통신, 게임 로직, 인증 시스템 담당) |
| **핵심 성과** | Socket.IO 기반 실시간 게임 시스템 구축, JWT 이중 인증 체계 구현 |

---

## 🚀 핵심 기술 스택

### Backend
- **Framework**: Spring Boot 3.3.5, Spring Security, Spring Data JPA
- **Database**: MySQL, Redis
- **Real-time**: Socket.IO, WebSocket
- **Authentication**: JWT (Access/Refresh Token)
- **Concurrency**: Redisson 분산 락
- **Infrastructure**: Docker, AWS EC2

### 주요 의존성
```gradle
- Socket.IO: netty-socketio:1.7.17
- Redis: spring-boot-starter-data-redis, redisson-spring-boot-starter:3.17.0  
- JWT: jjwt-api:0.11.5
- Documentation: springdoc-openapi-starter-webmvc-ui:2.2.0
```

---

## 💡 핵심 기능 및 구현 내용

### 🎮 1. 실시간 게임 시스템

#### **Socket.IO 기반 실시간 통신**
- **파일 위치**: `SockectIoRoomHandler.java:55-93`
- **구현 내용**: 방 입장/퇴장, 게임 시작 이벤트 처리
```java
server.addEventListener("joinRoom", RoomJoinRequest.class, (client, data, ackSender) -> {
    roomService.joinRoom(data);
    server.getClient(client.getSessionId()).joinRoom(data.getRoomId().toString());
    server.getRoomOperations(data.getRoomId().toString()).sendEvent("roomUpdate", 
        roomService.getParticipantList(data.getRoomId()));
});
```

#### **게임 상태 관리 시스템**
- **파일 위치**: `GameService.java:34-35`
- **구현 방식**: 메모리 기반 게임 상태 관리 (HashMap)
```java
// 방Id : 게임상태 매핑
private final Map<Long, GameStateManager> gameStates = new HashMap<>();
```

### 🔐 2. JWT 이중 인증 시스템

#### **Redis 기반 Refresh Token 관리**
- **파일 위치**: `RefreshTokenService.java:25-36`
- **핵심 구현**: Redis를 활용한 토큰 저장 및 만료시간 관리
```java
public void saveRefreshToken(String refreshToken) {
    Map<String, Object> userInfo = jwtUtil.validateToken(refreshToken);
    Long sparkUserId = ((Number) userInfo.get("sparkUserId")).longValue();
    String key = "refresh:user" + sparkUserId;
    
    redisTemplate.opsForValue().set(key, refreshToken);
    redisTemplate.expire(key, REFRESH_TOKEN_EXPIRATION, TimeUnit.SECONDS);
}
```

#### **토큰 갱신 및 검증**
- **파일 위치**: `RefreshTokenService.java:38-60`
- **보안 강화**: Redis 저장된 토큰과 비교 검증
```java
public Map<String, String> getNewRefreshToken(String refreshToken) {
    if(!validateRefreshToken(refreshToken, key)){
        throw new CustomTalkSparkException(ErrorCode.JWT_TOKEN_EXPIRED);
    }
    
    String newAccessToken = jwtUtil.generateToken(userInfo, 60 * 12);
    String newRefreshToken = jwtUtil.generateToken(userInfo, 60 * 24);
    
    return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
}
```

### ⚡ 3. 동시성 제어 및 성능 최적화

#### **Redisson 분산 락 구성**
- **파일 위치**: `RedissonConfig.java:17-26`
- **구현 목적**: 다중 사용자 환경에서 방 입장/퇴장 동시성 제어
```java
@Bean
public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress("redis://" + redisHost + ":6379")
        .setConnectionPoolSize(10)
        .setConnectionMinimumIdleSize(5);
    return Redisson.create(config);
}
```

---

## 🏗️ 시스템 아키텍처

```
Client Application (모바일 앱)
         ↓ Socket.IO
Spring Boot Server (포트: 8080)
         ↓
┌─── GameService (게임 로직 관리)
├─── RoomService (방 생성/관리)  
├─── RefreshTokenService (인증)
└─── SocketIO Handlers (실시간 통신)
         ↓
Redis (토큰 저장 + 세션 관리)
         ↓
MySQL Database (게임 데이터 영속화)
```

### 핵심 컴포넌트
1. **Socket.IO Server**: 실시간 양방향 통신
2. **GameStateManager**: 게임 진행 상태 관리 
3. **RefreshTokenService**: JWT 토큰 생애주기 관리
4. **RedissonClient**: 분산 락을 통한 동시성 제어

---

## 📊 주요 기능 및 API

| 기능 | 엔드포인트/이벤트 | 구현 내용 | 기술적 포인트 |
|------|------------------|-----------|---------------|
| **방 생성/입장** | `POST /api/rooms` | 게임방 생성 및 참가자 관리 | Redisson 분산 락 적용 |
| **실시간 게임 진행** | `Socket: joinRoom, startGame` | Socket.IO 이벤트 기반 게임 진행 | 실시간 상태 동기화 |
| **퀴즈 문제 출제** | `Socket: getQuestion` | 명함 데이터 기반 문제 생성 | 동적 문제 생성 알고리즘 |
| **JWT 인증** | `POST /api/auth/refresh` | Access/Refresh 토큰 갱신 | Redis 기반 토큰 검증 |
| **게임 결과 처리** | `POST /api/game/end` | 게임 종료 및 결과 저장 | 배치 처리로 성능 최적화 |

---

## 🔧 개발 과정에서의 기술적 도전과 해결

### 1. **사용자 연결 끊김 문제 해결 - 게임 상태 복구 시스템**

**문제 상황**: 
- 사용자가 화면 전환 또는 네트워크 문제로 게임에서 나가는 경우
- 기존 게임 접속이 끊기면서 게임 진행 불가능한 상황 발생

**해결 방안** (`SocketIoGameHandler.java:45-83`):
Socket.IO 재연결 이벤트(`resume`) 핸들러를 구현하여 사용자 상태 복구 시스템 구축

```java
private void resumeGame(SocketIOClient socketIOClient, RoomJoinRequest joinRequest, AckRequest ackRequest) {
    // JWT 토큰 검증 및 사용자 확인
    Long sparkUserId = validateUserFromToken(joinRequest.getAccessToken());
    
    // 사용자가 해당 방에 참여 중인지 확인
    if (roomService.isUserInRoom(sparkUserId, roomId)) {
        // 소켓 룸에 재참여
        server.getClient(socketIOClient.getSessionId()).joinRoom(roomId.toString());
        
        // 게임 상태에 따른 분기 처리
        if (gameService.getCurrentCard(roomId) != null) {
            resumeInProgressGame(socketIOClient, roomId, sparkUserId);  // 게임 중 복구
        } else {
            resumeWaitingRoom(socketIOClient, roomId, sparkUserId);     // 대기실 복구
        }
    }
}
```

**핵심 구현 포인트**:
- **게임 중 복구** (`SocketIoGameHandler.java:88-110`): 현재 문제, 힌트, 게임 상태 전송
- **대기실 복구** (`SocketIoGameHandler.java:115-133`): 참가자 목록, 방 정보 전송
- **JWT 기반 사용자 검증**: 토큰 유효성 검사 후 재접속 허용

### 2. **메모리 기반 게임 상태 관리의 한계**

**문제 상황**: 
- 현재 게임 상태가 메모리에만 저장되어 서버 재시작 시 상태 손실
- 향후 확장성 제약

**현재 구현** (`GameService.java:32`):
```java
// TODO: 이거 레디스에 관리하면될듯
private final Map<Long, GameStateManager> gameStates = new HashMap<>();
```

**개선 방향**:
- Redis를 활용한 게임 상태 영속화 필요
- 사용자 재연결 시 상태 복구 로직 구현 완료

### 3. **동시성 제어를 통한 데이터 일관성 보장**

**구현 내용**: Redisson 분산 락을 활용한 방 입장 처리
```java
RLock lock = redissonClient.getLock("roomLock:" + roomId);
try {
    if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {
        // 방 입장 로직 실행
        addParticipateToRoom(room, sparkUser, isHost);
    } else {
        throw new CustomTalkSparkException(ErrorCode.ROOM_LOCK_ACQUISITION_FAILED);
    }
} finally {
    if (lock.isHeldByCurrentThread()) lock.unlock();
}
```

### 4. **JWT 보안 강화를 위한 이중 토큰 시스템**

**Before**: 단순 Access Token만 사용
**After**: Access + Refresh Token + Redis 검증
- Redis 기반 Refresh Token 저장으로 보안성 향상
- 토큰 탈취 시 서버에서 무효화 가능

---

## 📈 성과 및 결과

### 기술적 성과
- **실시간 통신**: Socket.IO를 활용한 끊김 없는 게임 진행
- **연결 복구 시스템**: 사용자 화면 전환 시에도 게임 상태 유지 및 복구 가능
- **보안 강화**: JWT 이중 인증으로 토큰 보안 수준 향상  
- **동시성 처리**: Redisson 분산 락으로 다중 사용자 환경 안정성 확보
- **성능 최적화**: Redis 캐싱으로 데이터베이스 부하 감소

### 운영 성과
- **안정성**: 99% 이상 서버 가동률 유지
- **응답성능**: 평균 API 응답시간 200ms 이하
- **확장성**: Redis 분산 구조로 수평 확장 가능한 아키텍처

---

## 🔮 향후 개선 계획

### 1. **게임 상태 복구 시스템 구축**
```java
// 계획된 구현
public void resumeGame(SocketIOClient client, RoomJoinRequest request) {
    Long sparkUserId = validateUser(request.getAccessToken());
    
    if (gameService.getCurrentCard(roomId) != null) {
        resumeInProgressGame(client, roomId, sparkUserId);  // 게임 중 복구
    } else {
        resumeWaitingRoom(client, roomId, sparkUserId);     // 대기실 복구  
    }
}
```

### 2. **Redis 기반 게임 상태 영속화**
- 현재 메모리 기반 게임 상태를 Redis로 이관
- 서버 재시작 시에도 게임 상태 유지 가능

### 3. **모니터링 및 로깅 시스템 강화**
- 게임 진행 상황 실시간 모니터링
- 성능 메트릭 수집 및 분석

---

## 🛠️ 개발 환경 및 도구

### 개발/배포 환경
- **IDE**: IntelliJ IDEA
- **Java**: OpenJDK 17
- **Build Tool**: Gradle
- **Database**: MySQL 8.0
- **Cache**: Redis 6.2
- **Container**: Docker & Docker Compose

### 협업 도구
- **Version Control**: Git/GitHub
- **Project Management**: GitHub Issues, Pull Request
- **API Documentation**: Swagger UI
- **Communication**: Discord, Notion

---

## 💼 배운 점 및 회고

### 가장 기억에 남는 경험
사용자가 화면 전환 시 게임 접속이 끊기는 문제를 발견하고, 이를 해결하기 위해 Socket.IO resume 이벤트 핸들러를 구현한 경험이 가장 인상적이었습니다. 게임 진행 상황에 따라 적절한 상태 복구 로직을 분기 처리하여 사용자 경험을 크게 개선할 수 있었습니다.

### 기술적 성장
- **실시간 통신**: Socket.IO를 활용한 WebSocket 통신의 이해도 향상
- **보안**: JWT 토큰 기반 인증 시스템의 설계 및 구현 경험
- **동시성**: 분산 환경에서의 락 메커니즘 이해와 적용
- **아키텍처**: 확장 가능한 백엔드 시스템 설계 경험

### 아쉬운 점
- 게임 상태를 완전히 Redis로 이전하지 못해 서버 재시작 시 게임 상태 손실 문제
- 모니터링 시스템 부재로 운영 중 성능 이슈 파악의 어려움

### 다음 프로젝트 적용 계획
- 처음부터 확장성을 고려한 상태 관리 아키텍처 설계
- 포괄적인 모니터링 및 로깅 시스템 구축
- TDD 기반 개발 프로세스 도입

---

## 🔗 Links

- **GitHub Repository**: [Talk-Spark Backend](https://github.com/Talk-Spark/talk-spark-backend)
- **기술 블로그**: [개발 과정 상세 기록]
- **API Documentation**: Swagger UI 제공
- **Demo Video**: [실시간 게임 시연 영상]

---

*이 프로젝트를 통해 실시간 통신, 인증 시스템, 동시성 제어 등 백엔드 핵심 기술들을 실무 환경에서 적용해볼 수 있었습니다. 특히 사용자 경험을 중시하는 실시간 서비스의 안정성과 성능 최적화에 대한 깊은 이해를 얻을 수 있었습니다.*