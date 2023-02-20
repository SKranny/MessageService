package messageService.dto.mesage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.attachment.AttachmentType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendAttachment {
    private Long attachedMessageId;

    private String content;

    private Long postId;

    private AttachmentType type;
}
