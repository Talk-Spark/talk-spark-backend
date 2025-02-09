package mutsa.yewon.talksparkbe.domain.game.service.dto.httpResponse;

import lombok.Builder;
import lombok.Data;
import mutsa.yewon.talksparkbe.domain.card.entity.Card;
import mutsa.yewon.talksparkbe.domain.game.entity.RoomParticipate;
import mutsa.yewon.talksparkbe.domain.sparkUser.entity.SparkUser;

@Data
public class RoomParticipantResponse {

    private Long sparkUserId;
    private String name;
    private String color;
    private boolean isOwner;

    @Builder
    private RoomParticipantResponse(Long sparkUserId, String name, String color, boolean isOwner) {
        this.sparkUserId = sparkUserId;
        this.name = name;
        this.color = color;
        this.isOwner = isOwner;
    }

    public static RoomParticipantResponse from(SparkUser sparkUser, Card card, boolean isOwner) {
        return RoomParticipantResponse.builder()
                .sparkUserId(sparkUser.getId())
                .name(card.getName())
                .color(card.getCardThema().name())
                .isOwner(isOwner)
                .build();
    }

}
