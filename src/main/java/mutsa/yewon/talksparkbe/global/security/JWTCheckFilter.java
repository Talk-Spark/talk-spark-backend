package mutsa.yewon.talksparkbe.global.security;

import com.google.gson.Gson;
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
            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            String kakaoId = (String) claims.get("kakaoId");
            String name = (String) claims.get("name");
            String password = (String) claims.get("password");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            SparkUserDTO sparkUserDTO = new SparkUserDTO(kakaoId, name, password, roleNames);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(sparkUserDTO, password, sparkUserDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (CustomTalkSparkException e) {
            handleCustomException(response, e);
        } catch (Exception e) {
            writeErrorResponse(response, HttpStatus.BAD_REQUEST, "JWT_SIGNATURE_ERROR");
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
