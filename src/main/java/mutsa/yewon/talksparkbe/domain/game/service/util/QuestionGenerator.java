package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.UserCardQuestions;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class QuestionGenerator {

    public List<UserCardQuestions> execute(List<Card> cards, int difficulty) {
        int numOfQuestions = 0;
        if (difficulty <= 2) numOfQuestions = difficulty + 1;
        else if (difficulty == 3) numOfQuestions = 5;
        List<UserCardQuestions> userCardQuestionsList = new ArrayList<>();

        // 빈칸 필드를 생성할 키 리스트 (명함의 필드명)
        List<String> keys = List.of("mbti", "hobby", "lookAlike", "selfDescription", "tmi");

        Map<String, Set<String>> listOfOptions = new HashMap<>();

        Map<String, Integer> fieldCount = new HashMap<>();
        int numOfPeople = cards.size();

        // 각 필드마다, 이 필드를 채워놓은 놈이 사람수만큼은 되어야 문제로 출제 가능
        for (String k : keys) {

            Set<String> options = new HashSet<>();

            for (Card c : cards){
                String fieldValue = getFieldValue(c, k);

                if (fieldValue != null){
                    options.add(fieldValue);
                }
            }
            listOfOptions.put(k, options);
            fieldCount.put(k, options.size()); // 각 필드를 채워놓은 놈의 숫자 맵
        }

        for (Card c : cards) {
            List<String> fields = new ArrayList<>(List.of("mbti", "hobby", "lookAlike", "selfDescription", "tmi"));
            List<CardQuestion> questions = new ArrayList<>();

            if(numOfPeople <= 4){
                fields.removeIf(field -> fieldCount.get(field) < numOfPeople);
            }
            else{
                fields.removeIf(field -> getFieldValue(c, field) == null || fieldCount.get(field) < 4);
            }
            System.out.println("fields = " + fields);
            Collections.shuffle(fields);
            System.out.println("fields = " + fields);

            for (int i=0;i<numOfQuestions;i++) {
                questions.add(generateQuestion(c, fields.get(i), listOfOptions.get(fields.get(i)), numOfPeople));
            }
            System.out.println("generateQuestion이 끝남");

            userCardQuestionsList.add(new UserCardQuestions(c.getSparkUser().getId(), questions)); // 어떤 사람의 카드에 대한 질문들 생성 완료
        }
        System.out.println("모든 for문 끝");

        userCardQuestionsList.sort(Comparator.comparing(UserCardQuestions::getSparkUserId)); // 유저아이디 오름차순. 그래서 같은 유저아이디의 문제가 쫙 나옴.

        log.info(userCardQuestionsList);

        return userCardQuestionsList;
    }

    private CardQuestion generateQuestion(Card c, String fieldName, Set<String> cardData, int numOfPeople) {
        String correctAnswer = getFieldValue(c, fieldName);
        List<String> options = generateOptions(fieldName, correctAnswer, cardData, numOfPeople);
        System.out.println("generateOptions 끝남");
        return new CardQuestion(c.getId(), c.getSparkUser().getId(), fieldName, correctAnswer, options);
    }

    // 카드의 특정 필드 값을 반환 (Reflection 활용)
    private String getFieldValue(Card card, String fieldName) {
        try {
            return switch (fieldName) {
                case "name" -> card.getName();
                case "age" -> card.getAge() != null ? card.getAge().toString() : null;
                case "mbti" -> card.getMbti();
                case "hobby" -> card.getHobby();
                case "lookAlike" -> card.getLookAlike();
                case "selfDescription" -> card.getSlogan();
                case "tmi" -> card.getTmi();
                default -> null;
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 보기 생성: 다른 참가자의 해당 필드 값 + 정답
    private List<String> generateOptions(String fieldName, String correctAnswer, Set<String> cardData, int numOfPeople) {
        int maxOptionNum = Math.min(4, numOfPeople);

        if(maxOptionNum < 4){
            return new ArrayList<>(cardData);
        }

        List<String> canBeOptions = new ArrayList<>(cardData);



        canBeOptions.remove(correctAnswer);

        List<String> options = getOptionsByCombi(canBeOptions, 3);



        options.add(correctAnswer);
        Collections.shuffle(options); // 보기 순서 섞기

        return options;
    }

    private List<String> getOptionsByCombi(List<String> candidates, int count){
        if (count > candidates.size()) {
            throw new RuntimeException("요청된 갯수는 Set의 크기보다 클 수 없습니다.");
        }


        List<String> options = new ArrayList<>();

        Random random = new Random();

        while(options.size() < count){
            int index = random.nextInt(candidates.size());

            options.add(candidates.get(index));
        }

        return options;
    }

}
