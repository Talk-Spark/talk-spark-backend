package mutsa.yewon.talksparkbe.domain.game.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DummyOption {

    HOBBY("hobby", "책 읽고 필사하기",
            "한강러닝", "테무쇼핑", "넷플릭스 정주행", "커피 원두 쇼핑",
            "여행 계획 짜기", "유튜브 알고리즘 탐험", "침대 위 인생고민", "새벽 감성 플리 만들기", "편의점 신상 과자찾기"),

    LOOKALIKE("lookAlike", "곰돌이 푸", "짱구 아빠", "강형욱", "돌맹이",
            "호빵맨", "차은우", "카리나", "장원영", "공유", "유재석", "변우석"),

    TMI("tmi","하루에 커피 3잔", "코딩 100일 챌린지",
            "화장실 댄싱머신", "집콕 전문가", "어제 침대에서 폰 얼굴에 떨굼",
            "배터리 5%까지 충전 안 함", "새벽만 되면 감성 풀 충전", "한글 맞춤법 검사기 애용자", "음식 먹기 전 사진 10장 필수"),

    SELFDESCRIPTION("selfDescription","만능 개발자", "새벽 카공 데이트",
            "인생의 목표: 배달 앱 VIP 되기", "한 입만→한 판 다 먹는 사람", "카톡 읽고 답장 미루기 장인", "자기소개? 난 그냥 내 스타일",
            "쉬는 날엔 무조건 집콕 챌린지", "먹을 때 가장 진지해지는 유형", "치킨은 반반, 성격도 반반"),

    MBTI("mbti", "INFJ", "ENFP", "ISTJ", "ESFJ", "ENTP", "INTP", "ENTJ", "ISTP");

    private final List<String> dummyOptions;

    private final String option;

    DummyOption(String option, String... dummyOptions) {
        this.dummyOptions = Arrays.asList(dummyOptions);
        this.option = option;
    }
}
