package messageService.dto.chat;

import dto.userDto.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.constants.chat.ChatType;
import messageService.dto.mesage.MessageDTO;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

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

    private String description;

    private PersonDTO admin;

    private Set<PersonDTO> consumers;

    private ZonedDateTime createDateTime;
}
