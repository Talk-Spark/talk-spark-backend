package mutsa.yewon.talksparkbe.domain.game.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.TeamCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.service.StoredCardService;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameDto;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameResponseDto;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomParticipateRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.game.service.dto.*;
import mutsa.yewon.talksparkbe.domain.game.service.util.GameState;
import mutsa.yewon.talksparkbe.domain.game.service.util.QuestionGenerator;
import mutsa.yewon.talksparkbe.domain.game.service.util.RoomState;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
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
    private final RoomParticipateRepository roomParticipateRepository;
    private final QuestionGenerator questionGenerator;
    private final StoredCardService storedCardService;
    private final RoomState roomState;
    private final SparkUserRepository sparkUserRepository;
    private final RoomService roomService;

    @Transactional
    public void startGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        List<RoomParticipate> participants = roomState.getParticipantsByRoomId(roomId);

        for (RoomParticipate rp : participants) {
            Long SparkUserId = roomState.findUserIdByRoomIdAndParticipant(room.getRoomId(), rp);
            SparkUser sparkUser = sparkUserRepository.findById(SparkUserId).orElseThrow(() -> new RuntimeException("유저 못찾음"));
            RoomParticipate roomParticipate = RoomParticipate.builder()
                    .isOwner(rp.isOwner())
                    .sparkUser(sparkUser)
                    .room(room)
                    .build();
            roomParticipateRepository.save(roomParticipate);
        }

        roomState.clearParticipantsByRoomId(roomId);

        List<Card> selectedCards = room.getRoomParticipates().stream()
                .map(RoomParticipate::getSparkUser)
                .map(SparkUser::getCards)
                .map(cardList -> cardList.get(0)) // 갖고 있는 카드들 중 각각 가장 첫번째 카드 선택
                .toList(); // 참가자들의 명함 한장씩을 선택함
        List<Card> playerCards = getPlayerCards(room); // 참가자들의 명함 한장씩을 선택함

        List<UserCardQuestions> questions = questionGenerator.execute(playerCards, room.getDifficulty()); // 선택된 명함들을 가지고 난이도를 기반으로 문제 만들기

        GameState gameState = new GameState(playerCards, questions); // 게임 상태를 초기화
        gameStates.put(roomId, gameState); // 특정 방 번호에 게임 상태 할당



        // 각 참가자들마다의 빈칸정보를 만들기
        createBlanks(roomId, questions);
    }

    public CardQuestion getQuestion(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        if (gameState == null || !gameState.hasNextQuestion()) return null;
        return gameState.getCurrentQuestion();
    }

    @Transactional
    public EndGameResponseDto endGame(EndGameDto endGameDto){

        Long roomId = endGameDto.getRoomId();

        GameState gameState = gameStates.get(roomId);

        if (gameState == null){
            throw new CustomTalkSparkException(ErrorCode.GAME_NOT_FOUND);
        }

        Long playerId = endGameDto.getPlayerId();

        insertCardCopies(roomId, playerId);

        roomService.changeFinished(roomId);

        removeGameState(roomId);

        return EndGameResponseDto.of(gameState.getScores(),
                gameState.getPlayerInfo().values().stream().map(CardResponseCustomDTO::fromCard).toList());
    }

    @Transactional(readOnly = true)
    public CardResponseCustomDTO getCurrentCard(Long roomId) {
        return CardResponseCustomDTO.fromCard(gameStates.get(roomId).getCurrentCard());
    }

    public CardBlanksDto getCurrentCardBlanks(Long roomId) {
        return gameStates.get(roomId).getCurrentCardBlanks();
    }

    public void submitAnswer(Long roomId, Long sparkUserId, String answer) {
        GameState gameState = Optional.ofNullable(gameStates.get(roomId)).orElseThrow();
        gameState.recordScore(sparkUserId, answer);
    }

    public boolean allPeopleSubmitted(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState.getCurrentQuestionAnswerNum().equals(gameState.getPlayerInfo().size());
    }

    public List<CorrectAnswerDto> getSingleQuestionScoreBoard(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState.getCurrentQuestionCorrect();
    }

    public void loadNextQuestion(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        gameState.loadNextQuestion();
    }

    public SwitchSubject isSwitchingSubject(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState.isSwitchingSubject();
    }

    public Map<Long, Integer> getScores(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState == null ? Collections.emptyMap() : gameState.getScores();
    }

    public List<CardResponseCustomDTO> getAllRelatedCards(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        return gameState.getPlayerInfo().values().stream().map(CardResponseCustomDTO::fromCard).toList();
    }

    private void insertCardCopies(Long roomId, Long sparkUserId) {
        GameState gameState = gameStates.get(roomId);

        Map<Long, Card> playerInfo = gameState.getPlayerInfo();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));
        List<Long> participantIds = playerInfo.keySet().stream().toList();
        List<Long> addingCardIds = new ArrayList<>();
        List<Card> cards = playerInfo.values().stream().toList();

        Map<Long, List<Long>> participantCardMap = new HashMap<>();

        for (Long participantId : participantIds) {
            List<Long> otherCards = playerInfo.values()
                    .stream()
                    .filter(card -> !card.getSparkUser().getId().equals(participantId))
                    .map(Card::getId)
                    .toList();
            participantCardMap.put(participantId, otherCards);
        }

        for (Map.Entry<Long, List<Long>> entry : participantCardMap.entrySet()) {
            storedCardService.storeTeamCard(TeamCardHolderCreateDTO.of(entry.getKey(), room.getRoomName(), entry.getValue()));
        }

//        for (Long participantId : participantIds) {
//            for (Card c : cards) {
//                if (!c.getSparkUser().getId().equals(participantId)) addingCardIds.add(c.getId());
//            }
//        }

//        List<Card> cardsToStore = playerInfo.entrySet()
//                .stream()
//                .filter(entry -> !entry.getKey().equals(sparkUserId))
//                .map(Map.Entry::getValue)
//                .toList();
//
//        storedCardService.storeTeamCard(TeamCardHolderCreateDTO.of(sparkUserId, room.getRoomName(), addingCardIds));
    }

    public void updateBlanks(Long roomId) {
        GameState gameState = gameStates.get(roomId);
        String fieldName = gameState.getCurrentQuestion().getFieldName();
        Long cardOwnerId = gameState.getCurrentQuestion().getCardOwnerId();
        gameState.getCardBlanksDtos().stream()
                .filter(cbd -> cbd.getSparkUserId().equals(cardOwnerId)).findFirst()
                .ifPresent(cbd -> cbd.getBlanks().remove(fieldName));
    }

    private List<Card> getPlayerCards(Room room) {
        return room.getRoomParticipates().stream()
                .map(RoomParticipate::getSparkUser)
                .map(SparkUser::getCards)
                .map(cardList -> cardList.get(0)) // 갖고 있는 카드들 중 각각 가장 첫번째 카드 선택
                .toList();
    }

    private void createBlanks(Long roomId, List<UserCardQuestions> questions) {
        for (UserCardQuestions ucq : questions) {
            Long sparkUserId = ucq.getSparkUserId();
            List<String> blanks = ucq.getQuestions().stream().map(CardQuestion::getFieldName).toList();
            gameStates.get(roomId).getCardBlanksDtos().add(CardBlanksDto.of(sparkUserId, blanks));
        }
    }

    private void removeGameState(Long roomId) {
        gameStates.remove(roomId);
    }
}
