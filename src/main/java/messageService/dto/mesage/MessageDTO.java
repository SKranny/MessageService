package messageService.dto.mesage;

import dto.userDto.PersonDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import messageService.dto.customers.ShortPerson;
import messageService.model.person.Person;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long id;

    private Long chatId;

    private String text;

    private ShortPerson author;

    private List<AttachmentDTO> attachments;

    private LocalDateTime createDateTime;

}
