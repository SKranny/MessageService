package messageService.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.chat.ChatType;
import messageService.dto.customers.ShortPerson;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDetailDTO {
    private Long id;

    private String name;

    private String photo;

    private ChatType type;

    private String description;

    private ShortPerson admin;

    private List<ShortPerson> consumers;

    private LocalDateTime createDateTime;
}
