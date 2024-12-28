package mutsa.yewon.talksparkbe.domain.game.service.util;

import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.UserCardQuestions;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QuestionGenerator {
    private final Random random = new Random();

    public List<UserCardQuestions> execute(List<Card> cards, int difficulty) {
        int numOfQuestions = 0;
        if (difficulty <= 2) numOfQuestions = difficulty + 1;
        else if (difficulty == 3) numOfQuestions = 5;
        List<UserCardQuestions> userCardQuestionsList = new ArrayList<>();

        // 빈칸 필드를 생성할 키 리스트 (명함의 필드명)
        List<String> keys = List.of("major", "mbti", "hobby", "lookAlike", "slogan", "tmi");
        Map<String, Integer> fieldCount = new HashMap<>();
        int numOfPeople = cards.size();

        // 각 필드마다, 이 필드를 채워놓은 놈이 사람수만큼은 되어야 문제로 출제 가능
        for (String k : keys) {
            int count = 0;
            for (Card c : cards)
                if (getFieldValue(c, k) != null) count++;
            fieldCount.put(k, count); // 각 필드를 채워놓은 놈의 숫자 맵
        }

        for (Card c : cards) {
            List<String> fields = new ArrayList<>(List.of("major", "mbti", "hobby", "lookAlike", "selfDescription", "tmi"));
            List<CardQuestion> questions = new ArrayList<>();

            while (questions.size() < numOfQuestions) {
                int idx = random.nextInt(6); // 0~5

                // 뽑힌 그 필드에 대해서 출제가능성 검토.
                // 일단 이 카드에서 null 아니어야 하고, fieldCount 보고 채워놓은 놈이 사람수만큼 되어야 함. 그리고 이번 반복에서 뽑힌 적 없어야 함.
                // 이렇게 모든 카드마다 난이도만큼의 문제를 생성하기
                String fieldName = fields.get(idx);
                if (!fieldName.equals("x") && getFieldValue(c, fieldName) != null && fieldCount.get(fieldName) >= numOfPeople) {
                    questions.add(generateQuestion(c, fieldName, cards, numOfPeople));
                    fields.set(idx, "x");
                }
            }
            userCardQuestionsList.add(new UserCardQuestions(c.getSparkUser().getId(), questions)); // 어떤 사람의 카드에 대한 질문들 생성 완료
        }

        userCardQuestionsList.sort(Comparator.comparing(UserCardQuestions::getSparkUserId)); // 유저아이디 오름차순. 그래서 같은 유저아이디의 문제가 쫙 나옴.
        return userCardQuestionsList;
    }

    private CardQuestion generateQuestion(Card c, String fieldName, List<Card> cards, int numOfPeople) {
        String correctAnswer = getFieldValue(c, fieldName);
        List<String> options = generateOptions(fieldName, correctAnswer, cards, numOfPeople);
        return new CardQuestion(c.getId(), c.getSparkUser().getId(), fieldName, correctAnswer, options);
    }

    // 카드의 특정 필드 값을 반환 (Reflection 활용)
    private String getFieldValue(Card card, String fieldName) {
        try {
            return switch (fieldName) {
                case "name" -> card.getName();
                case "age" -> card.getAge() != null ? card.getAge().toString() : null;
                case "major" -> card.getMajor();
                case "mbti" -> card.getMbti();
                case "hobby" -> card.getHobby();
                case "lookAlike" -> card.getLookAlike();
                case "slogan" -> card.getSlogan();
                case "tmi" -> card.getTmi();
                default -> null;
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 보기 생성: 다른 참가자의 해당 필드 값 + 정답
    private List<String> generateOptions(String fieldName, String correctAnswer, List<Card> cards, int numOfPeople) {
        int maxOptionNum = Math.min(4, numOfPeople);
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        while (options.size() < maxOptionNum) {
            List<String> canBeOptions = cards.stream().map(c -> getFieldValue(c, fieldName)).toList();
            String candidate = canBeOptions.get(random.nextInt(canBeOptions.size()));
            if (!options.contains(candidate)) options.add(candidate);
        }

        Collections.shuffle(options); // 보기 순서 섞기
        return options;
    }

}
