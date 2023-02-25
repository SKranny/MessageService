package messageService.mapper;

import messageService.dto.chat.ChatDTO;
import messageService.dto.chat.ChatDetailDTO;
import messageService.model.chat.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;


@Mapper(
        componentModel = "spring",
        uses = {PersonMapper.class},
        imports = ZoneId.class
)
public interface ChatMapper {

    ChatDTO toDTO(Chat chat);

    ChatDetailDTO toDetail(Chat chat);
}
