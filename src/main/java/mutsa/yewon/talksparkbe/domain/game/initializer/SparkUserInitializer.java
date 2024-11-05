//package mutsa.yewon.talksparkbe.domain.game.initializer;
//
//import lombok.RequiredArgsConstructor;
//import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
//import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SparkUserInitializer implements CommandLineRunner {
//
//    private final SparkUserRepository sparkUserRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        SparkUser sparkUser1 = SparkUser.builder()
//                .name("minu")
//                .kakaoId("111111")
//                .build();
//        sparkUserRepository.save(sparkUser1);
//
//        SparkUser sparkUser2 = SparkUser.builder()
//                .name("seungbeom")
//                .kakaoId("111112")
//                .build();
//        sparkUserRepository.save(sparkUser2);
//
//        SparkUser sparkUser3 = SparkUser.builder()
//                .name("yoonjeong")
//                .kakaoId("111113")
//                .build();
//        sparkUserRepository.save(sparkUser3);
//    }
//
//}
