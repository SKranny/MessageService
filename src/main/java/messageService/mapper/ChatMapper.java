package messageService.mapper;

import messageService.dto.chat.ChatDTO;
import messageService.model.chat.Chat;
import org.mapstruct.Mapper;

import java.time.ZoneId;


@Mapper(
        componentModel = "spring",
        uses = {PersonMapper.class},
        imports = ZoneId.class
)
public interface ChatMapper {

    ChatDTO toDTO(Chat chat);

}
