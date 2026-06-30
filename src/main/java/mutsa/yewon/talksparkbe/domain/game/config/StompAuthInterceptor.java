package mutsa.yewon.talksparkbe.domain.game.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new MessageDeliveryException("Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
        }

        String token = authorization.substring(7);
        try {
            Map<String, Object> claims = jwtUtil.validateToken(token);

            Number sparkUserIdNumber = (Number) claims.get("sparkUserId");
            Long sparkUserId = sparkUserIdNumber != null ? sparkUserIdNumber.longValue() : null;
            String kakaoId = (String) claims.get("kakaoId");

            if (sparkUserId == null) {
                throw new MessageDeliveryException("토큰에서 sparkUserId를 추출할 수 없습니다.");
            }

            Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
            if (sessionAttrs == null) {
                sessionAttrs = new HashMap<>();
            }
            sessionAttrs.put("sparkUserId", sparkUserId);
            sessionAttrs.put("kakaoId", kakaoId);
            sessionAttrs.put("accessToken", token);
            accessor.setSessionAttributes(sessionAttrs);

            final String userName = sparkUserId.toString();
            accessor.setUser(new Principal() {
                @Override
                public String getName() {
                    return userName;
                }
            });

            log.info("STOMP CONNECT 인증 성공: sparkUserId={}", sparkUserId);

        } catch (MessageDeliveryException e) {
            throw e;
        } catch (Exception e) {
            log.error("STOMP CONNECT 인증 실패: {}", e.getMessage());
            throw new MessageDeliveryException("JWT 인증 실패: " + e.getMessage());
        }

        return message;
    }
}