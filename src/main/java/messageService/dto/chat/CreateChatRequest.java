package messageService.dto.chat;

import lombok.Data;
import messageService.constants.chat.ChatType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class CreateChatRequest {
    @NotBlank
    private String name;

    private String photo;

    @NotNull
    private ChatType type;

    @NotEmpty
    private List<Long> consumersIds;

    private String description;
}
