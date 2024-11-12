package mutsa.yewon.talksparkbe.domain.game.controller.view;

import lombok.RequiredArgsConstructor;
import mutsa.yewon.talksparkbe.domain.game.controller.request.RoomCreateRequest;
import mutsa.yewon.talksparkbe.domain.game.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public String createRoom(@ModelAttribute RoomCreateRequest roomCreateRequest) {
        Long roomId = roomService.createRoom(roomCreateRequest);
        return "redirect:/views/rooms/" + roomId;
    }

    @GetMapping("/{roomId}")
    public String getRoomDetails(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "roomDetail";
    }

}
