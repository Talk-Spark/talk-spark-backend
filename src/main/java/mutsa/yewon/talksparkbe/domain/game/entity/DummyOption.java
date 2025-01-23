package mutsa.yewon.talksparkbe.domain.game.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DummyOption {

    HOBBY("hobby", "책 읽고 필사하기", "한강러닝"),

    LOOKALIKE("lookAlike", "곰돌이 푸", "김수현"),

    TMI("tmi","하루에 커피 3잔", "코딩 100일 챌린지"),

    SELFDESCRIPTION("selfDescription","만능 개발자", "새벽 카공 데이트"),

    MBTI("mbti", "INFJ", "ENFP", "ISTJ", "ESFJ", "ENTP");

    private final List<String> dummyOptions;

    private final String option;

    DummyOption(String option, String... dummyOptions) {
        this.dummyOptions = Arrays.asList(dummyOptions);
        this.option = option;
    }
}
