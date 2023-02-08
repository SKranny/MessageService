package messageService.dto.chat;

import lombok.Data;

import java.util.List;

@Data
public class UpdateTheChatLineupRequest {
    private Long chatId;

    private List<Long> consumersId;
}
