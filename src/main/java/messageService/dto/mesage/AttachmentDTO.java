package messageService.dto.mesage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.attachment.AttachmentType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
    private Long id;

    private Long messageId;

    private AttachmentType type;

    private String content;

    private Long postId;

    private List<MessageDTO> attachedMessages;
}
