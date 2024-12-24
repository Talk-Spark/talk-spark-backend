package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;

@Data
public class RoomParticipantResponse {

    private Long sparkUserId;
    private String name;
    private boolean isOwner;

    @Builder
    private RoomParticipantResponse(Long sparkUserId, String name, boolean isOwner) {
        this.sparkUserId = sparkUserId;
        this.name = name;
        this.isOwner = isOwner;
    }

    public static RoomParticipantResponse from(RoomParticipate roomParticipate) {
        return RoomParticipantResponse.builder()
                .sparkUserId(roomParticipate.getSparkUser().getId())
                .name(roomParticipate.getSparkUser().getName())
                .isOwner(roomParticipate.isOwner())
                .build();
    }

}
