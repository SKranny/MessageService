package messageService.dto.mesage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.messages.MessageType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeMessage {
    private MessageType type;

    private Long personId;

    private Long messageId;

    private Boolean isLike;

    private Long chatId;
}
