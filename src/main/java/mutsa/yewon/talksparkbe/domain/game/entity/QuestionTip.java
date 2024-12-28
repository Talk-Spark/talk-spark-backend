package mutsa.yewon.talksparkbe.domain.game.entity;

import lombok.Getter;

@Getter
public enum QuestionTip {

    MAJOR("전공 인원이 많은 편인가요?\n" +
            "팀 프로젝트가 자주 있나요?"),
    MBTI("MBTI 결과를 믿는 편인가요?\n" +
            "친구들이 MBTI 결과에 공감하나요?"),
    HOBBY("실내 활동인가요, 야외 활동인가요?\n" +
            "장비나 도구가 필요한가요?"),
    LOOKALIKE("닮은 꼴이 사람인가요?"),
    SELFDESC("성격이 활발한 편인가요?\n" +
            "본인을 색깔로 표현하면 무엇인가요?"),
    TMI("최근 구매한 물건은 무엇인가요?\n" +
            "오늘 가장 많이 한 생각은 무엇인가요?")
    ;

    public final String tipContent;

    private QuestionTip(String tipContent) {
        this.tipContent = tipContent;
    }

}
