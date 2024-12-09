package mutsa.yewon.talksparkbe.domain.game.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.dto.CardResponseDTO;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.game.service.dto.CardQuestion;
import mutsa.yewon.talksparkbe.domain.game.service.dto.UserCardQuestions;
import mutsa.yewon.talksparkbe.domain.game.service.util.GameState;
import mutsa.yewon.talksparkbe.domain.game.service.util.QuestionGenerator;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    // TODO: 이거 레디스에 관리하면될듯
    // 방Id : 게임상태
    @Getter
    private final Map<Long, GameState> gameStates = new HashMap<>();

    private final RoomRepository roomRepository;
    private final QuestionGenerator questionGenerator;

    @Transactional(readOnly = true)
    public void startGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        List<Card> selectedCards = room.getRoomParticipates().stream()
                .map(RoomParticipate::getSparkUser)
                .map(SparkUser::getCards)
                .filter(cardList -> !cardList.isEmpty())  // 빈 리스트 제외
                .map(cardList -> cardList.get(0)) // 갖고 있는 카드들 중 각각 가장 첫번째 카드 선택
                .toList(); // 참가자들의 명함 한장씩을 선택함

        List<UserCardQuestions> questions = prepareQuestions(room, selectedCards); // 특정 방 번호에서 선택된 명함들을 가지고 문제 만들기
        GameState gameState = new GameState(selectedCards, questions, room.getRoomParticipates().size()); // 게임 상태를 초기화
        gameStates.put(roomId, gameState); // 특정 방 번호에 게임 상태 할당
    }

    public CardQuestion getNextQuestion(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        if (gameState == null || !gameState.hasNextQuestion()) {
            return null; // 게임 종료
        }
        return gameState.getNextQuestion();
    }

    public String submitAnswer(Long roomId, Long sparkUserId, String answer) {
        GameState gameState = gameStates.get(roomId);
        if (gameState != null) {
            gameState.recordAnswer(sparkUserId, answer);
        }
        if (gameState.getCurAnswerNum() >= gameState.getRoomPeople()) return "keep";
        else return "next";
    }

    public boolean isGameFinished(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState == null || !gameState.hasNextQuestion();
    }

    public Map<Long, Integer> getScores(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState == null ? Collections.emptyMap() : gameState.getScores();
    }

    @Transactional(readOnly = true)
    public CardResponseDTO getCurrentCard(Long roomId) {
        return CardResponseDTO.fromCard(gameStates.get(roomId).getCurrentCard());
    }

    @Transactional(readOnly = true)
    public List<UserCardQuestions> prepareQuestions(Room room, List<Card> selectedCards) {
        int difficulty = room.getDifficulty();
        return questionGenerator.execute(selectedCards, difficulty);
    }

}
