package messageService.dto.mesage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.messages.MessageType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendMessage {
    @Builder.Default
    private MessageType type = MessageType.SEND_MESSAGE;

    private Long authorId;

    private Long chatId;

    private String content;

    private List<SendAttachment> messageAttachments;
}
