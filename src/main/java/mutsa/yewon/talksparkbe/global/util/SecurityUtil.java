package mutsa.yewon.talksparkbe.global.util;

import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SecurityUtil {

    public Long getLoggedInUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new SecurityException("Authentication is null");
        }

        SparkUserDTO sparkUser = (SparkUserDTO) authentication.getPrincipal();

        return sparkUser.getSparkUserId();
    }

}
