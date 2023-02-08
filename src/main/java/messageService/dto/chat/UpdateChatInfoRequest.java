package messageService.dto.chat;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class UpdateChatInfoRequest {
    @NotNull
    private Long chatId;

    private String name;

    private String description;

    private Long adminId;
}
