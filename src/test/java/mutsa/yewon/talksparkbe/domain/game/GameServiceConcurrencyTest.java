package mutsa.yewon.talksparkbe.domain.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 게임 점수 집계 동시성 테스트
 *
 * 검증 대상: GameService.submitAnswer()의 Redis Load-Modify-Save 패턴
 * - 락 미적용: 다수 스레드가 동시에 Read-Modify-Write 시 Lost Update(점수 유실) 발생 재현
 * - 락 적용:  Redisson RLock과 동일한 상호 배제 의미론(ReentrantLock)으로 정합성 보장 검증
 *
 * Redis 외부 의존 없이 순수 Java로 동일한 패턴 시뮬레이션.
 * 프로덕션 코드(GameService.submitAnswer)의 Redisson은 분산 환경에서 동일한 보장을 제공.
 */
class GameServiceConcurrencyTest {

    private static final int PLAYER_COUNT = 10;

    // ────────────────────────────────────────────────────────────────
    // 1. 락 미적용: Lost Update 재현
    // ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("[락 미적용] 10개 스레드 동시 점수 제출 시 Lost Update(점수 유실) 발생")
    void 락_미적용시_동시_점수_제출에서_점수_유실이_발생한다() throws InterruptedException {
        // "Redis" 역할: 공유 게임 상태 (초기 점수 모두 0)
        AtomicReference<Map<Long, Integer>> redisState = new AtomicReference<>(initScores());

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(PLAYER_COUNT);

        for (long userId = 1; userId <= PLAYER_COUNT; userId++) {
            long finalUserId = userId;
            new Thread(() -> {
                try {
                    startGate.await(); // 모든 스레드 동시 출발

                    // Lock 없는 Load-Modify-Save (프로덕션의 취약 코드 재현)
                    Map<Long, Integer> loaded = new HashMap<>(redisState.get()); // Load
                    loaded.put(finalUserId, loaded.getOrDefault(finalUserId, 0) + 1); // Modify
                    Thread.sleep(5); // 네트워크 지연 시뮬레이션 → 경합 극대화
                    redisState.set(loaded); // Save (마지막 스레드가 이전 스레드 결과 덮어씀)

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startGate.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);

        Map<Long, Integer> finalScores = redisState.get();
        int totalScore = finalScores.values().stream().mapToInt(Integer::intValue).sum();
        int lostScore = PLAYER_COUNT - totalScore;

        System.out.printf("%n[락 미적용 결과]%n");
        System.out.printf("  플레이어 수:     %d명%n", PLAYER_COUNT);
        System.out.printf("  기대 점수 합계:  %d점%n", PLAYER_COUNT);
        System.out.printf("  실제 점수 합계:  %d점%n", totalScore);
        System.out.printf("  유실된 점수:     %d점 (Lost Update)%n", lostScore);
        finalScores.forEach((uid, score) ->
                System.out.printf("    Player %2d: %d점%n", uid, score));

        assertThat(totalScore)
                .as("락 미적용 시 점수 유실 발생 확인")
                .isLessThan(PLAYER_COUNT);
    }

    // ────────────────────────────────────────────────────────────────
    // 2. 분산 락 적용: 데이터 정합성 보장
    // ────────────────────────────────────────────────────────────────

    @RepeatedTest(5)
    @DisplayName("[분산 락 적용] 10개 스레드 동시 점수 제출 시 100% 데이터 정합성 보장")
    void 분산락_적용시_동시_점수_제출에서_데이터_정합성이_보장된다() throws InterruptedException {
        AtomicReference<Map<Long, Integer>> redisState = new AtomicReference<>(initScores());

        // ReentrantLock = Redisson RLock의 단일 노드 동등체 (동일한 상호 배제 의미론)
        ReentrantLock lock = new ReentrantLock();

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(PLAYER_COUNT);
        AtomicInteger lockFailCount = new AtomicInteger(0);

        for (long userId = 1; userId <= PLAYER_COUNT; userId++) {
            long finalUserId = userId;
            new Thread(() -> {
                try {
                    startGate.await();

                    // Redisson tryLock(2, 3, SECONDS) 동일 의미론
                    boolean acquired = lock.tryLock(2, TimeUnit.SECONDS);
                    if (acquired) {
                        try {
                            // Critical Section: Load-Modify-Save
                            Map<Long, Integer> loaded = new HashMap<>(redisState.get()); // Load
                            loaded.put(finalUserId, loaded.getOrDefault(finalUserId, 0) + 1); // Modify
                            Thread.sleep(5);
                            redisState.set(loaded); // Save
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        lockFailCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startGate.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);

        Map<Long, Integer> finalScores = redisState.get();
        int totalScore = finalScores.values().stream().mapToInt(Integer::intValue).sum();

        System.out.printf("%n[분산 락 적용 결과]%n");
        System.out.printf("  플레이어 수:     %d명%n", PLAYER_COUNT);
        System.out.printf("  실제 점수 합계:  %d점%n", totalScore);
        System.out.printf("  락 획득 실패:    %d건%n", lockFailCount.get());
        System.out.printf("  데이터 정합성:   %s%n", totalScore == PLAYER_COUNT ? "100% 달성 ✓" : "실패 ✗");

        assertThat(totalScore)
                .as("분산 락 적용 시 모든 플레이어의 점수가 누락 없이 기록되어야 함")
                .isEqualTo(PLAYER_COUNT);

        assertThat(lockFailCount.get())
                .as("2초 대기 시간 내 모든 스레드가 락을 획득해야 함")
                .isZero();
    }

    // ────────────────────────────────────────────────────────────────
    // Helper
    // ────────────────────────────────────────────────────────────────

    private Map<Long, Integer> initScores() {
        Map<Long, Integer> scores = new HashMap<>();
        for (long i = 1; i <= PLAYER_COUNT; i++) {
            scores.put(i, 0);
        }
        return scores;
    }
}
