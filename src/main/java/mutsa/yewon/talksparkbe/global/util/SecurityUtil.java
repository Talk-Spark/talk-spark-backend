package mutsa.yewon.talksparkbe.global.util;

import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.sparkUser.dto.SparkUserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Log4j2
public class SecurityUtil {

    public static Long getLoggedInUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SparkUserDTO sparkUser = (SparkUserDTO) authentication.getPrincipal();

        return sparkUser.getSparkUserId();
    }

}
