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
import mutsa.yewon.talksparkbe.domain.game.service.util.GameStateManager;
import mutsa.yewon.talksparkbe.domain.game.service.util.QuestionGenerator;
import mutsa.yewon.talksparkbe.domain.game.service.util.RoomState;
import mutsa.yewon.talksparkbe.domain.guestBook.service.GuestBookService;
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
    private final Map<Long, GameStateManager> gameStates = new HashMap<>();

    private final RoomRepository roomRepository;
    private final RoomParticipateRepository roomParticipateRepository;
    private final QuestionGenerator questionGenerator;
    private final StoredCardService storedCardService;
    private final RoomState roomState;
    private final SparkUserRepository sparkUserRepository;
    private final RoomService roomService;
    private final GuestBookService guestBookService;

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

//        List<Card> selectedCards = room.getRoomParticipates().stream()
//                .map(RoomParticipate::getSparkUser)
//                .map(SparkUser::getCards)
//                .map(cardList -> cardList.get(0)) // 갖고 있는 카드들 중 각각 가장 첫번째 카드 선택
//                .toList(); // 참가자들의 명함 한장씩을 선택함
        List<Card> playerCards = getPlayerCards(room); // 참가자들의 명함 한장씩을 선택함

        List<UserCardQuestions> questions = questionGenerator.execute(playerCards, room.getDifficulty()); // 선택된 명함들을 가지고 난이도를 기반으로 문제 만들기

        GameStateManager gameStateManager = new GameStateManager(playerCards, questions); // 게임 상태를 초기화
        gameStates.put(roomId, gameStateManager); // 특정 방 번호에 게임 상태 할당

    }

    public CardQuestion getQuestion(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        return gameStateManager.getCurrentQuestion();
    }

    @Transactional
    public EndGameResponseDto endGame(EndGameDto endGameDto){

        Long roomId = endGameDto.getRoomId();
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        GameStateManager gameStateManager = gameStates.get(roomId);

        if (gameStateManager == null){
            throw new CustomTalkSparkException(ErrorCode.GAME_NOT_FOUND);
        }
//        if (gameState == null){
//            throw new CustomTalkSparkException(ErrorCode.GAME_NOT_FOUND);
//        }

        if (!room.isFinished()) {
            Long playerId = endGameDto.getPlayerId();
            insertCardCopies(roomId, playerId);

            roomService.changeFinished(roomId);
            guestBookService.createGuestBookData(roomId);

        return EndGameResponseDto.of(gameStateManager.getScores(),
                gameStateManager.getAllPlayerCards());
//            removeGameState(roomId);
        }

        return EndGameResponseDto.of(gameStateManager.getScores(),
                gameStateManager.getAllPlayerCards());
    }

    @Transactional(readOnly = true)
    public CardResponseCustomDTO getCurrentCard(Long roomId) {
        return CardResponseCustomDTO.fromCard(gameStates.get(roomId).getCurrentCard());
    }

    public CardBlanksDto getCurrentCardBlanks(Long roomId) {
        return gameStates.get(roomId).getCurrentCardBlanks();
    }

    public void submitAnswer(Long roomId, Long sparkUserId, String answer) {
        GameStateManager gameStateManager = Optional.ofNullable(gameStates.get(roomId)).orElseThrow();
        gameStateManager.recordScore(sparkUserId, answer);
    }

    public boolean allPeopleSubmitted(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        return gameStateManager.allAnswered();
    }

    public List<AnswerDto> getSingleQuestionScoreBoard(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        return gameStateManager.getScoreBoard();
    }

    public void loadNextQuestion(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        gameStateManager.prepareNextQuestion();
    }

    public SwitchSubject isSwitchingSubject(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        return gameStateManager.isSwitchingSubject();
    }

    public Map<Long, Integer> getScores(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        return gameStateManager == null ? Collections.emptyMap() : gameStateManager.getScores();
    }

    public List<CardResponseCustomDTO> getAllRelatedCards(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        return gameStateManager.getAllPlayerCards();
    }

    private void insertCardCopies(Long roomId, Long sparkUserId) {
        GameStateManager gameStateManager = gameStates.get(roomId);

        Map<Long, Card> playerInfo = gameStateManager.getPlayerManager().getPlayerCards();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));
        List<Long> participantIds = playerInfo.keySet().stream().toList();
//        List<Long> addingCardIds = new ArrayList<>();
//        List<Card> cards = playerInfo.values().stream().toList();

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

    }

    public void updateBlanks(Long roomId) {
        GameStateManager gameStateManager = gameStates.get(roomId);
        String fieldName = gameStateManager.getCurrentQuestion().getFieldName();
        Long cardOwnerId = gameStateManager.getCurrentQuestion().getCardOwnerId();
        gameStateManager.getBlanks().stream()
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


    public void removeGameState(Long roomId) {
        gameStates.remove(roomId);
    }
}
