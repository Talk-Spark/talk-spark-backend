package mutsa.yewon.talksparkbe.global.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 현재 로그인한 사용자 정보를 주입합니다.
 *
 * 사용 예시:
 * <pre>
 * @GetMapping("/api/cards")
 * public ResponseEntity<?> getCards(@CurrentUser SparkUserDTO user) {
 *     Long userId = user.getSparkUserId();
 *     // ...
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
public @interface CurrentUser {
}
