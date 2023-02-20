package messageService.controller;

import lombok.RequiredArgsConstructor;
import messageService.dto.mesage.MessageDTO;
import messageService.service.message.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import security.TokenAuthentication;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{chatId}/messages")
    public Page<MessageDTO> getMessages(
            @PathVariable Long chatId,
            @Valid @Min(0) @RequestParam(required = false, defaultValue = "0") Integer page,
            @Valid @Min(0) @RequestParam(required = false, defaultValue = "20") Integer offset,
            TokenAuthentication authentication) {
        return messageService.getMessagesByFilter(chatId, authentication.getTokenData().getId(), PageRequest.of(page, offset));
    }
}
