package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.HostCheckRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;
import mutsa.yewon.talksparkbe.domain.sparkUser.repository.SparkUserRepository;
import mutsa.yewon.talksparkbe.global.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final SparkUserRepository sparkUserRepository;
    private final JWTUtil jwtUtil;

    // TODO: 인증헤더로 유저 얻어오는거 SecurityUtil 메서드 만들어서 그걸로 바꾸기
    @PostMapping
    public ResponseEntity<?> roomCreate(@RequestBody RoomCreateRequest roomCreateRequest,
                                        @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(() -> new RuntimeException("유저 못찾음"));

        roomCreateRequest.setHostId(sparkUser.getId());

        return ResponseEntity.ok(roomService.createRoom(roomCreateRequest));
    }

    @GetMapping
    public ResponseEntity<?> roomSearch(@RequestParam String searchName) {
        return ResponseEntity.ok(roomService.searchRooms(searchName));
    }

    @PostMapping("/host")
    public ResponseEntity<?> isHost(@RequestBody HostCheckRequest hostCheckRequest,
                                    @RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        Map<String, Object> claims = jwtUtil.validateToken(jwt);
        String kakaoId = (String) claims.get("kakaoId");
        SparkUser sparkUser = sparkUserRepository.findByKakaoId(kakaoId).orElseThrow(() -> new RuntimeException("유저 못찾음"));

        return ResponseEntity.ok(roomService.checkHost(hostCheckRequest.getRoomId(), sparkUser));
    }

    @GetMapping("/all")
    public ResponseEntity<?> roomList() {
        return ResponseEntity.ok(roomService.listAllRooms());
    }

}
