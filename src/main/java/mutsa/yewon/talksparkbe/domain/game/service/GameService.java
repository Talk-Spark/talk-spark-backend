package mutsa.yewon.talksparkbe.domain.game.service;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.cardHolder.dto.TeamCardHolderCreateDTO;
import mutsa.yewon.talksparkbe.domain.cardHolder.service.StoredCardService;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameDto;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.EndGameResponseDto;
import mutsa.yewon.talksparkbe.domain.game.controller.dto.GameCardInfo;
import mutsa.yewon.talksparkbe.domain.game.entity.Room;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.game.repository.GameRedisRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomParticipateRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRedisRepository;
import mutsa.yewon.talksparkbe.domain.game.repository.RoomRepository;
import mutsa.yewon.talksparkbe.domain.game.service.dto.*;
import mutsa.yewon.talksparkbe.domain.game.service.util.GameStateManager;
import mutsa.yewon.talksparkbe.domain.game.service.util.QuestionGenerator;
import mutsa.yewon.talksparkbe.domain.game.service.util.RoomParticipantInfo;
import mutsa.yewon.talksparkbe.domain.guestBook.service.GuestBookService;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.exception.CustomTalkSparkException;
import mutsa.yewon.talksparkbe.global.exception.ErrorCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRedisRepository gameRedisRepository;
    private final RoomRedisRepository roomRedisRepository;
    private final RedissonClient redissonClient;
    private final RoomRepository roomRepository;
    private final RoomParticipateRepository roomParticipateRepository;
    private final QuestionGenerator questionGenerator;
    private final StoredCardService storedCardService;
    private final SparkUserRepository sparkUserRepository;
    private final RoomService roomService;
    private final GuestBookService guestBookService;

    @Transactional
    public void startGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));

        List<RoomParticipantInfo> participants = roomRedisRepository.getParticipants(roomId);

        for (RoomParticipantInfo info : participants) {
            SparkUser sparkUser = sparkUserRepository.findById(info.sparkUserId())
                    .orElseThrow(() -> new RuntimeException("유저 못찾음"));
            RoomParticipate roomParticipate = RoomParticipate.builder()
                    .isOwner(info.owner())
                    .sparkUser(sparkUser)
                    .room(room)
                    .build();
            roomParticipateRepository.save(roomParticipate);
        }

        roomRedisRepository.clearParticipants(roomId);

        List<Card> playerCards = getPlayerCards(room);
        List<GameCardInfo> gameCards = playerCards.stream().map(GameCardInfo::from).toList();
        List<UserCardQuestions> questions = questionGenerator.execute(playerCards, room.getDifficulty());

        GameStateManager gameStateManager = new GameStateManager(gameCards, questions);
        gameRedisRepository.saveGameState(roomId, gameStateManager);
    }

    public CardQuestion getQuestion(Long roomId) {
        return gameRedisRepository.getGameState(roomId).getCurrentQuestion();
    }

    @Transactional
    public EndGameResponseDto endGame(EndGameDto endGameDto) {
        Long roomId = endGameDto.getRoomId();
        GameStateManager gameStateManager = gameRedisRepository.getGameState(roomId);

        if (gameStateManager == null) {
            throw new CustomTalkSparkException(ErrorCode.GAME_NOT_FOUND);
        }

        if (roomRepository.updateRoomFinished(roomId) > 0) {
            insertCardCopies(roomId, gameStateManager);
            guestBookService.createGuestBookData(roomId);
        }

        EndGameResponseDto response = EndGameResponseDto.of(
                gameStateManager.getScores(),
                gameStateManager.getAllPlayerCards()
        );

        gameRedisRepository.deleteGameState(roomId);
        return response;
    }

    @Transactional(readOnly = true)
    public CardResponseCustomDTO getCurrentCard(Long roomId) {
        return CardResponseCustomDTO.fromCard(gameRedisRepository.getGameState(roomId).getCurrentCard());
    }

    public CardBlanksDto getCurrentCardBlanks(Long roomId) {
        return gameRedisRepository.getGameState(roomId).getCurrentCardBlanks();
    }

    public void submitAnswer(Long roomId, Long sparkUserId, String answer) {
        String key = "lock:game:" + roomId;
        RLock lock = redissonClient.getLock(key);

        try {
            if (lock.tryLock(2, 3, TimeUnit.SECONDS)) {
                GameStateManager gameStateManager = gameRedisRepository.getGameState(roomId);
                gameStateManager.recordScore(sparkUserId, answer);
                gameRedisRepository.saveGameState(roomId, gameStateManager);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomTalkSparkException(ErrorCode.ROOM_JOIN_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    public boolean allPeopleSubmitted(Long roomId) {
        return gameRedisRepository.getGameState(roomId).allAnswered();
    }

    public List<AnswerDto> getSingleQuestionScoreBoard(Long roomId) {
        return gameRedisRepository.getGameState(roomId).getScoreBoard();
    }

    public void loadNextQuestion(Long roomId) {
        GameStateManager gsm = gameRedisRepository.getGameState(roomId);
        gsm.prepareNextQuestion();
        gameRedisRepository.saveGameState(roomId, gsm);
    }

    public SwitchSubject isSwitchingSubject(Long roomId) {
        return gameRedisRepository.getGameState(roomId).isSwitchingSubject();
    }

    public void switchCurrentPlayerId(Long roomId) {
        GameStateManager gsm = gameRedisRepository.getGameState(roomId);
        gsm.switchCurrentPlayerId();
        gameRedisRepository.saveGameState(roomId, gsm);
    }

    public Map<Long, Integer> getScores(Long roomId) {
        GameStateManager gsm = gameRedisRepository.getGameState(roomId);
        return gsm == null ? Collections.emptyMap() : gsm.getScores();
    }

    public List<CardResponseCustomDTO> getAllRelatedCards(Long roomId) {
        return gameRedisRepository.getGameState(roomId).getAllPlayerCards();
    }

    public boolean isGameInProgress(Long roomId) {
        return gameRedisRepository.existsGameState(roomId);
    }

    public void removeGameState(Long roomId) {
        gameRedisRepository.deleteGameState(roomId);
    }

    public String getQuestionTip(Long roomId, String field) {
        GameStateManager gsm = gameRedisRepository.getGameState(roomId);
        String tip = gsm.getQuestionTip(roomId, field);
        gameRedisRepository.saveGameState(roomId, gsm);
        return tip;
    }

    public void updateBlanks(Long roomId) {
        GameStateManager gsm = gameRedisRepository.getGameState(roomId);
        String fieldName = gsm.getCurrentQuestion().getFieldName();
        Long cardOwnerId = gsm.getCurrentQuestion().getCardOwnerId();
        gsm.getBlanks().stream()
                .filter(cbd -> cbd.getSparkUserId().equals(cardOwnerId)).findFirst()
                .ifPresent(cbd -> cbd.getBlanks().remove(fieldName));
        gameRedisRepository.saveGameState(roomId, gsm);
    }

    private List<Card> getPlayerCards(Room room) {
        return room.getRoomParticipates().stream()
                .map(RoomParticipate::getSparkUser)
                .map(SparkUser::getCards)
                .map(cardList -> cardList.get(0))
                .toList();
    }

    private void insertCardCopies(Long roomId, GameStateManager gameStateManager) {
        Map<Long, GameCardInfo> playerInfo = gameStateManager.getPlayerManager().getPlayerCards();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomTalkSparkException(ErrorCode.ROOM_NOT_FOUND));
        List<Long> participantIds = new ArrayList<>(playerInfo.keySet());

        Map<Long, List<Long>> participantCardMap = new HashMap<>();
        for (Long participantId : participantIds) {
            List<Long> otherCards = playerInfo.values().stream()
                    .filter(card -> !card.getUserId().equals(participantId))
                    .map(GameCardInfo::getCardId)
                    .toList();
            participantCardMap.put(participantId, otherCards);
        }

        for (Map.Entry<Long, List<Long>> entry : participantCardMap.entrySet()) {
            storedCardService.storeTeamCard(
                    TeamCardHolderCreateDTO.of(entry.getKey(), room.getRoomName(), entry.getValue())
            );
        }
    }
}
