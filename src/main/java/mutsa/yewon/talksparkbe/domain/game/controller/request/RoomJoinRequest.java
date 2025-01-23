package mutsa.yewon.talksparkbe.domain.game.controller.request;

import lombok.Data;

@Data
public class RoomJoinRequest {

    private Long roomId;

    private String accessToken;

    private Boolean isHost;

}
