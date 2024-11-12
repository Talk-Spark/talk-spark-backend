package mutsa.yewon.talksparkbe.global.security;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("-------shouldNotFilter--------");
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/member")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("----------------------JWT Filtering-------------------");

        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            writeErrorResponse(response, HttpStatus.BAD_REQUEST, "JWT_TOKEN_REQUIRED");
            return;
        }

        try {
            String accessToken = authorization.substring(7);

            // 토큰 검증 단계에서 발생할 수 있는 예외를 디버깅하기 위해 로그 추가
            log.info("Access Token: " + accessToken);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);
            log.info("Token validation completed. Claims: " + claims);

            String kakaoId = (String) claims.get("kakaoId");
            String name = (String) claims.get("name");
            String password = (String) claims.get("password");
            List<String> roleNames = (List<String>) claims.get("roleNames");
            Number sparkUserIdNumber = (Number) claims.get("sparkUserId");
            Long sparkUserId = (sparkUserIdNumber != null) ? sparkUserIdNumber.longValue() : null;

            log.info("Parsed Claims: kakaoId=" + kakaoId + ", name=" + name + ", password=" + password + ", sparkUserId=" + sparkUserId);

            SparkUserDTO sparkUserDTO = new SparkUserDTO(sparkUserId, kakaoId, name, password, roleNames);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(sparkUserDTO, password, sparkUserDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        } catch (CustomTalkSparkException e) {
            // Custom 예외 처리
            log.error("Custom exception occurred: " + e.getMessage(), e);
            handleCustomException(response, e);
        }catch (MalformedJwtException e) {
            // 잘못된 형식의 JWT 예외
            log.error("Malformed JWT error: " + e.getMessage(), e);
            writeErrorResponse(response, HttpStatus.BAD_REQUEST, "MALFORMED_JWT_ERROR");
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 예외
            log.error("Expired JWT error: " + e.getMessage(), e);
            writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT_EXPIRED_ERROR");
        } catch (Exception e) {
            // 그 외의 일반 예외 처리
            log.error("General exception occurred: " + e.getMessage(), e);
            writeErrorResponse(response, HttpStatus.BAD_REQUEST, "GENERAL_JWT_ERROR");
        }
    }

    private void handleCustomException(HttpServletResponse response, CustomTalkSparkException e) throws IOException {
        String errorType;
        switch (e.getErrorCode()) {
            case INVALID_JWT_TOKEN:
                errorType = "INVALID_JWT_TOKEN";
                break;
            case JWT_TOKEN_EXPIRED:
                errorType = "JWT_TOKEN_EXPIRED";
                break;
            case JWT_MALFORMED:
                errorType = "JWT_MALFORMED";
                break;
            default:
                errorType = "UNKNOWN_ERROR";
                break;
        }
        writeErrorResponse(response, e.getErrorCode().getHttpStatus(), errorType);
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String errorMessage) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(Map.of("ERROR", errorMessage));

        response.setStatus(status.value());
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(json);
        }
    }
}
