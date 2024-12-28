package mutsa.yewon.talksparkbe.domain.game.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(access = AccessLevel.PRIVATE)
public class CardBlanksDto {

    private Long sparkUserId;
    private List<String> blanks;

    public static CardBlanksDto of(Long sparkUserId, List<String> blanks) {
        return CardBlanksDto.builder()
                .sparkUserId(sparkUserId)
                .blanks(blanks)
                .build();
    }

}
