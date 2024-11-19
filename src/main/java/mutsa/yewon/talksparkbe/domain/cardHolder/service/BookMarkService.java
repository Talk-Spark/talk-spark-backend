package mutsa.yewon.talksparkbe.domain.cardHolder.service;

import mutsa.yewon.talksparkbe.domain.cardHolder.dto.BookMarkCreateDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface BookMarkService {
    Long createBookMark(BookMarkCreateDTO bookMarkCreateDTO);

}
