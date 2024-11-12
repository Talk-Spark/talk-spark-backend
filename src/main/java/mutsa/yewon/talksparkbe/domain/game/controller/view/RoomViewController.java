package mutsa.yewon.talksparkbe.domain.game.controller.view;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// TODO: 타임리프로 방 관련 뷰 통합 테스트를 위한 임시 뷰 컨트롤러입니다. 추후 삭제해야 합니다.
@Controller
@RequiredArgsConstructor
@RequestMapping("/views/rooms")
public class RoomViewController {

    private final RoomService roomService;

    @GetMapping
    public String showRoomList(Model model) {
        model.addAttribute("rooms", roomService.listAllRooms());
        return "roomList";
    }

    @GetMapping("/create")
    public String showCreateRoomForm(Model model) {
        model.addAttribute("roomCreateRequest", new RoomCreateRequest());
        return "createRoom";
    }

    @GetMapping("/{roomId}")
    public String getRoomDetails(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "roomDetail";
    }

}
