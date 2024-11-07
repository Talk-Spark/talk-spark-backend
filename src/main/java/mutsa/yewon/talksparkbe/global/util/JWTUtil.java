package mutsa.yewon.talksparkbe.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JWTUtil {


    private static String key = "1234567890123456789012345678901234567890"; // 키 값으 최소 30자 이상

    public static String generateToken(Map<String, Object> claims, int min) {

        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setHeader(Map.of("typ", "JWT"))
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();

        return jwt;
    }

    public static Map<String, Object> validateToken(String token) {
        log.info("validation token");
        Map<String, Object> claims = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


        }catch (MalformedJwtException e) {
            throw new CustomTalkSparkException(ErrorCode.JWT_MALFORMED);
        }catch (ExpiredJwtException e) {
            throw new CustomTalkSparkException(ErrorCode.JWT_TOKEN_EXPIRED);
        }catch (InvalidClaimException e) {
            throw new CustomTalkSparkException(ErrorCode.INVALID_JWT_TOKEN);
        }catch (IOException e){
            throw new IllegalStateException("getBytes Error");
        }
        return claims;
    }
}
