package mutsa.yewon.talksparkbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TalkSparkBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalkSparkBeApplication.class, args);
    }

}
