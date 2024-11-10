package mutsa.yewon.talksparkbe.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTUtil {


    @Value("${secret.key}")
    private String jwtKey;

    public String generateToken(Map<String, Object> claims, int min) {

        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(jwtKey.getBytes("UTF-8"));

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

    public Map<String, Object> validateToken(String token) {
        log.info("validation token");
        Map<String, Object> claims = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtKey.getBytes("UTF-8"));

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
