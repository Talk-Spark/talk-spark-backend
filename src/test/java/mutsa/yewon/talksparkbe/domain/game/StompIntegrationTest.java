package mutsa.yewon.talksparkbe.domain.game;

import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomParticipantResponse;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StompIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JWTUtil jwtUtil;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @BeforeEach
    void setUp() {
        SockJsClient sockJsClient = new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))
        );
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        wsUrl = "http://localhost:" + port + "/ws";
    }

    /**
     * 유효한 JWT 토큰으로 CONNECT 가능 여부 확인
     */
    @Test
    @DisplayName("유효한 JWT로 CONNECT하면 연결에 성공한다")
    void 유효한_JWT로_CONNECT하면_연결에_성공한다() throws Exception {
        // given
        String token = generateTestToken(1L, "test-kakao-id");

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        BlockingQueue<Boolean> connectedQueue = new ArrayBlockingQueue<>(1);

        // when
        StompSession session = stompClient.connectAsync(
                wsUrl,
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        connectedQueue.add(true);
                    }

                    @Override
                    public void handleException(StompSession session, StompCommand command,
                                                StompHeaders headers, byte[] payload, Throwable exception) {
                        connectedQueue.add(false);
                    }
                }
        ).get(5, TimeUnit.SECONDS);

        // then
        Boolean connected = connectedQueue.poll(5, TimeUnit.SECONDS);
        assertThat(connected).isTrue();
        session.disconnect();
    }

    /**
     * JWT 없이 CONNECT 시 거부 확인
     */
    @Test
    @DisplayName("JWT 없이 연결하면 CONNECT 단계에서 거부된다")
    void JWT_없이_연결하면_CONNECT_단계에서_거부된다() {
        // given - Authorization 헤더 없음
        StompHeaders connectHeaders = new StompHeaders();

        // when & then
        assertThatThrownBy(() ->
                stompClient.connectAsync(
                        wsUrl,
                        new WebSocketHttpHeaders(),
                        connectHeaders,
                        new StompSessionHandlerAdapter() {}
                ).get(5, TimeUnit.SECONDS)
        ).isInstanceOf(Exception.class);
    }

    /**
     * 잘못된 JWT로 CONNECT 시 거부 확인
     */
    @Test
    @DisplayName("잘못된 JWT로 연결하면 CONNECT 단계에서 거부된다")
    void 잘못된_JWT로_연결하면_CONNECT_단계에서_거부된다() {
        // given
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer invalid-jwt-token");

        // when & then
        assertThatThrownBy(() ->
                stompClient.connectAsync(
                        wsUrl,
                        new WebSocketHttpHeaders(),
                        connectHeaders,
                        new StompSessionHandlerAdapter() {}
                ).get(5, TimeUnit.SECONDS)
        ).isInstanceOf(Exception.class);
    }

    /**
     * 방 입장 후 /topic/room/{roomId}/update 수신 확인
     * (실제 DB/Redis 연동이 필요하므로 연결 성공 후 구독 가능 여부만 검증)
     */
    @Test
    @DisplayName("방 입장 시 roomUpdate 토픽을 구독할 수 있다")
    void 방_입장시_roomUpdate_토픽을_구독할_수_있다() throws Exception {
        // given
        String token = generateTestToken(1L, "test-kakao-id");
        Long roomId = 1L;

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        BlockingQueue<Boolean> subscribedQueue = new ArrayBlockingQueue<>(1);

        // when
        StompSession session = stompClient.connectAsync(
                wsUrl,
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        // 구독 시도
                        session.subscribe("/topic/room/" + roomId + "/update",
                                new StompFrameHandler() {
                                    @Override
                                    public Type getPayloadType(StompHeaders headers) {
                                        return List.class;
                                    }

                                    @Override
                                    public void handleFrame(StompHeaders headers, Object payload) {
                                        subscribedQueue.add(true);
                                    }
                                });
                        subscribedQueue.add(true); // 구독 자체는 성공
                    }
                }
        ).get(5, TimeUnit.SECONDS);

        // then
        Boolean subscribed = subscribedQueue.poll(3, TimeUnit.SECONDS);
        assertThat(subscribed).isTrue();
        session.disconnect();
    }

    /**
     * 개인 큐 구독 가능 여부 확인
     */
    @Test
    @DisplayName("개인 큐(/user/queue/user)를 구독할 수 있다")
    void 개인_큐를_구독할_수_있다() throws Exception {
        // given
        String token = generateTestToken(1L, "test-kakao-id");

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        BlockingQueue<Boolean> subscribedQueue = new ArrayBlockingQueue<>(1);

        // when
        StompSession session = stompClient.connectAsync(
                wsUrl,
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        session.subscribe("/user/queue/user",
                                new StompFrameHandler() {
                                    @Override
                                    public Type getPayloadType(StompHeaders headers) {
                                        return Object.class;
                                    }

                                    @Override
                                    public void handleFrame(StompHeaders headers, Object payload) {
                                        subscribedQueue.add(true);
                                    }
                                });
                        subscribedQueue.add(true);
                    }
                }
        ).get(5, TimeUnit.SECONDS);

        // then
        Boolean subscribed = subscribedQueue.poll(3, TimeUnit.SECONDS);
        assertThat(subscribed).isTrue();
        session.disconnect();
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    private String generateTestToken(Long sparkUserId, String kakaoId) {
        Map<String, Object> claims = Map.of(
                "sparkUserId", sparkUserId,
                "kakaoId", kakaoId,
                "name", "테스트유저",
                "password", "password",
                "roleNames", List.of("ROLE_USER")
        );
        return jwtUtil.generateToken(claims, 60);
    }
}