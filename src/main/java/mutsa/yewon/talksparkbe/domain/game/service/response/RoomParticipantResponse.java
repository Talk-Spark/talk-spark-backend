package mutsa.yewon.talksparkbe.domain.game.service.response;

import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;

@Data
public class RoomParticipantResponse {

    private String name;
    private boolean isOwner;

    @Builder
    private RoomParticipantResponse(String name, boolean isOwner) {
        this.name = name;
        this.isOwner = isOwner;
    }

    public static RoomParticipantResponse from(RoomParticipate roomParticipate) {
        return RoomParticipantResponse.builder()
                .name(roomParticipate.getSparkUser().getName())
                .isOwner(roomParticipate.isOwner())
                .build();
    }

}
