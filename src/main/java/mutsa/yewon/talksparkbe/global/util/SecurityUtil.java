package mutsa.yewon.talksparkbe.global.util;

import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SparkUserDTO sparkUser = (SparkUserDTO) authentication.getPrincipal();

        return sparkUser.getSparkUserId();
    }

}
