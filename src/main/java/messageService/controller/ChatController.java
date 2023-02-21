package messageService.controller;

import lombok.RequiredArgsConstructor;
import messageService.dto.chat.*;
import messageService.service.chat.ChatService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import security.TokenAuthentication;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public Set<ChatDTO> getMyChats(
            @Valid @Min(0) @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "offset", required = false, defaultValue = "20") Integer offset,
            TokenAuthentication authentication) {
        return chatService.getMyChats(authentication.getTokenData(), page, offset);
    }

    @PostMapping
    public ChatDTO createNewChat(@Valid @RequestBody CreateChatRequest req, TokenAuthentication authentication) {
        return chatService.getOrCreateChat(req, authentication.getTokenData());
    }

    @PutMapping("/{id}/photo")
    public String updateChatPhoto(@NotNull @RequestBody MultipartFile file,
                                  @PathVariable("id") Long chatId,
                                  TokenAuthentication authentication) {
        return chatService.updatePhoto(chatId, file, authentication.getTokenData());
    }

    @DeleteMapping("/{id}/photo")
    public void deleteChatPhoto(@PathVariable("id") Long chatId, TokenAuthentication authentication) {
        chatService.deleteChatPhoto(chatId, authentication.getTokenData());
    }

    @GetMapping("/detail/{id}")
    public ChatDetailDTO getChatInfo(@PathVariable("id") Long chatId, TokenAuthentication authentication) {
        return chatService.getChatDetail(chatId, authentication.getTokenData());
    }

    @PutMapping
    public void updateChatInfo(@Valid @RequestBody UpdateChatInfoRequest req, TokenAuthentication authentication) {
        chatService.updateChatInfo(req, authentication.getTokenData());
    }

    @PutMapping("/add_consumer")
    public void addConsumerToChat(@Valid @RequestBody UpdateTheChatLineupRequest req, TokenAuthentication authentication) {
        chatService.addConsumerToChat(req, authentication.getTokenData());
    }

    @DeleteMapping("/{id}")
    public void deleteChat(
            @PathVariable Long id,
            @RequestParam(name = "force", defaultValue = "false", required = false) boolean isForceDelete,
            TokenAuthentication authentication) {
        chatService.deleteChat(id, isForceDelete, authentication.getTokenData());
    }
}
