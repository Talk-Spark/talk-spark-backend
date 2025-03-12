package mutsa.yewon.talksparkbe.domain.game.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Getter
public enum QuestionTip {
    MBTI(
        "일주일에 두 번 이상 약속이 있나요?",
            "혼자 카페에서 시간 보내는 걸 좋아하나요?"
    ),
    HOBBY(
            "활동적인 취미와 조용한 취미 중 무엇을 더 좋아하나요?",
            "혼자 즐기는 취미와 함께하는 취미 중 어떤 걸 선호하나요?"
    ),
    LOOKALIKE(
            "사람, 동물, 캐릭터 중 어떤 쪽을 닮았다는 말을 자주 듣나요?",
            "첫인상과 실제 성격이 많이 다른 편인가요?"
    ),
    SELFDESC(
            "처음 만난 사람들과 쉽게 친해지는 편인가요?",
            "친구들이 자주 부르는 별명이 있나요?"
    ),
    TMI(
            "가방에 항상 가지고 다니는 필수템이 있나요?",
            "아침형 인간인가요 혹은 저녁형 인간인가요?"
    )
    ;

    private final List<String> questions;

    QuestionTip(String... questions) {
        this.questions = Arrays.asList(questions);
    }

    public List<String> getRandomQuestions() {
        List<String> randomQuestions = new ArrayList<>();
        randomQuestions.addAll(questions);
        return randomQuestions;
    }
}
