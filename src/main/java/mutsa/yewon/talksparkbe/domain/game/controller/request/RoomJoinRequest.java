package mutsa.yewon.talksparkbe.domain.game.controller.request;

import lombok.Data;

@Data
public class RoomJoinRequest {

    private Long roomId;

    private Long sparkUserId;

}
