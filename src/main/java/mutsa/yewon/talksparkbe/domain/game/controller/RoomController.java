package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.entity.QuestionTip;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomCreateResponse;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomDetailsResponse;
import mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse.RoomListResponse;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mutsa.yewon.talksparkbe.global.util.SecurityUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SparkUserRepository sparkUserRepository;
    private final JWTUtil jwtUtil;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<RoomCreateResponse> roomCreate(@RequestBody RoomCreateRequest roomCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        RoomCreateResponse.from(roomService.createRoom(roomCreateRequest))
                );
    }

    @GetMapping
    public ResponseEntity<List<RoomListResponse>> roomSearch(@RequestParam String searchName) {
        return ResponseEntity.ok(roomService.searchRooms(searchName));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDetailsResponse> roomDetails(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomDetails(roomId));
    }

    @GetMapping("/is-host")
    public ResponseEntity<Boolean> isHost(@RequestParam Long roomId,
                                          @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(() -> new RuntimeException("유저 못찾음"));

        return ResponseEntity.ok(roomService.checkHost(roomId, sparkUser));
    }

    @GetMapping("/is-duplicate")
    public ResponseEntity<Boolean> isDuplicate(@RequestParam String roomName) {
        return ResponseEntity.ok(roomService.getIsDuplicateRoomName(roomName));
    }

    @GetMapping("/question-tip")
    public ResponseEntity<String> questionTip(@RequestParam String field) {
        String randomQuestions = String.join("\n", QuestionTip.valueOf(field).getRandomQuestions());
        return ResponseEntity.ok(randomQuestions);
    }

//    @GetMapping("/{roomId}/name")
//    public ResponseEntity<?> roomName(@PathVariable Long roomId) {
//        return ResponseEntity.ok(roomService.getRoomName(roomId));
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<?> roomList() {
//        return ResponseEntity.ok(roomService.listAllRooms());
//    }

}
