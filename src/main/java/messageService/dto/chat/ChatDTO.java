package messageService.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.chat.ChatType;
import messageService.dto.mesage.MessageDTO;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    private Long id;

    private String name;

    private String photo;

    private ChatType type;

    private MessageDTO lastMessage;

    private LocalDateTime createDateTime;
}
