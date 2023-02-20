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
public class DeleteMessage {
    private MessageType type;

    private Long messageId;

    private Long personId;

    @Builder.Default
    private Boolean isForce = false;
}
