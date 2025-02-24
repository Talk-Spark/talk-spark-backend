package mutsa.yewon.talksparkbe.domain.game.service.util;

import lombok.extern.log4j.Log4j2;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.entity.DummyOption;
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
        List<DummyOption> keys = List.of(DummyOption.MBTI, DummyOption.HOBBY, DummyOption.LOOKALIKE,
                DummyOption.TMI, DummyOption.SELFDESCRIPTION);

        Map<String, Set<String>> listOfOptions = new HashMap<>();

        Map<String, Integer> fieldCount = new HashMap<>();
        int numOfPeople = cards.size();

        for (DummyOption k : keys) {

            Set<String> options = new HashSet<>();

            for (Card c : cards){

                log.info("현재 명함" + c);

                String fieldValue = getFieldValue(c, k.getOption());

                log.info("명함 항목" + fieldValue);

                if (fieldValue != null){
                    options.add(fieldValue);
                }
            }

            for(String dummy : k.getDummyOptions()){
                if(options.size() >= 4){
                    break;
                }
                options.add(dummy);
            }

            listOfOptions.put(k.getOption(), options);
            fieldCount.put(k.getOption(), options.size()); // 각 필드를 채워놓은 놈의 숫자 맵 -> 무조건 인원수!

            log.info("각 명함 항목들의 데이터로 보기를 만들어냅니다. " + listOfOptions);
            log.info("각 항목 당 답을 한 인원 수 " + fieldCount);

        }

        for (Card c : cards) {
            List<String> fields = new ArrayList<>(List.of("mbti", "hobby", "lookAlike", "selfDescription", "tmi"));
            List<CardQuestion> questions = new ArrayList<>();


            /*
                인원 수가 4명 이하인 경우
            */

            if(numOfPeople <= 4){
                fields.removeIf(field -> fieldCount.get(field) < numOfPeople);
            }
            else{
                fields.removeIf(field -> getFieldValue(c, field) == null || fieldCount.get(field) < 4);
            }
            Collections.shuffle(fields);

            for (int i=0;i<numOfQuestions;i++) {
                questions.add(generateQuestion(c, fields.get(i), listOfOptions.get(fields.get(i)), numOfPeople));
            }

            userCardQuestionsList.add(new UserCardQuestions(c.getSparkUser().getId(), questions)); // 어떤 사람의 카드에 대한 질문들 생성 완료
        }

        userCardQuestionsList.sort(Comparator.comparing(UserCardQuestions::getSparkUserId)); // 유저아이디 오름차순. 그래서 같은 유저아이디의 문제가 쫙 나옴.

        log.info(userCardQuestionsList);

        return userCardQuestionsList;
    }

    private CardQuestion generateQuestion(Card c, String fieldName, Set<String> cardData, int numOfPeople) {

        log.info("CardData" + cardData);

        String correctAnswer = getFieldValue(c, fieldName);
        Set<String> options = generateOptions(correctAnswer, cardData, numOfPeople);
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
    private Set<String> generateOptions(String correctAnswer, Set<String> cardData, int numOfPeople) {

        if(cardData.size() <= 4){
            return cardData;
        }

        Set<String> options = createQuestionOptions(correctAnswer, cardData);

        log.info("완성된 보기 리스트 : " + options);

        return options;
    }

    private Set<String> createQuestionOptions(String correctAnswer, Set<String> candidates){

        int count = 3;

        List<String> options = new ArrayList<>(candidates);

        options.remove(correctAnswer);

        Collections.shuffle(options);

        Set<String> questionOptions = new HashSet<>(options.subList(0, count));

        questionOptions.add(correctAnswer);

        return questionOptions;

    }

}
