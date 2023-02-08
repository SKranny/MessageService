package messageService.service.message;

import lombok.RequiredArgsConstructor;
import messageService.dto.chat.ChatDTO;
import messageService.model.message.Message;
import messageService.repository.message.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<Message> findLastMessagesByChatIds(Collection<Long> chatIds) {
        return messageRepository.findLastMessagesByChatsId(chatIds);
    }
}
