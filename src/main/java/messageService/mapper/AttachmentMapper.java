package messageService.mapper;

import messageService.dto.mesage.AttachmentDTO;
import messageService.model.message.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {MessageMapper.class}
)
public interface AttachmentMapper {
    @Mapping(source = "message.id", target = "messageId")
    AttachmentDTO toDTO(Attachment attachment);
}
