package mutsa.yewon.talksparkbe.domain.game.controller;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomJoinRequest;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> roomCreate(@RequestBody RoomCreateRequest roomCreateRequest) {
        return ResponseEntity.ok(roomService.createRoom(roomCreateRequest));
    }

    @GetMapping
    public ResponseEntity<?> roomList() {
        return ResponseEntity.ok(roomService.listAllRooms());
    }

    @PostMapping("/join")
    public ResponseEntity<?> roomJoin(@RequestBody RoomJoinRequest roomJoinRequest) {
        return ResponseEntity.ok(roomService.joinRoom(roomJoinRequest));
    }

}
