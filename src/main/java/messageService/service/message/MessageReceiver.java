package messageService.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import messageService.dto.mesage.DeleteMessage;
import messageService.dto.mesage.LikeMessage;
import messageService.dto.mesage.MessageDTO;
import messageService.dto.mesage.SendMessage;
import messageService.service.chat.ChatService;
import messageService.websocket.WebSocketSessionPool;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReceiver {
    private final ObjectMapper jacksonHandler;

    private final ChatService chatService;

    private final WebSocketSessionPool socketSessionPool;

    private final MessageService messageService;

    @SneakyThrows
    @KafkaListener(topics = "SEND_MESSAGE")
    public void handleMessage(SendMessage sendMessage) {
        log.info("New message: " + jacksonHandler.writeValueAsString(sendMessage));
        MessageDTO message = messageService.saveMessage(sendMessage);
        sendMessageToChanel(message);
    }

    private void sendMessageToChanel(MessageDTO message) {
        Set<WebSocketSession> sessions = socketSessionPool.getPersonWebSocketSession(chatService.getConsumersId(message.getChatId()));
        sessions.forEach(session -> sendMessageToSession(session, buildTextMessage(message)));
    }

    private void sendMessageToChanel(LikeMessage message) {
        Set<WebSocketSession> sessions = socketSessionPool.getPersonWebSocketSession(chatService.getConsumersId(message.getChatId()));
        sessions.forEach(session -> sendMessageToSession(session, buildTextMessage(message)));
    }

    @SneakyThrows
    private void sendMessageToSession(WebSocketSession session, TextMessage textMessage) {
        session.sendMessage(textMessage);
    }

    @SneakyThrows
    private TextMessage buildTextMessage(@Payload MessageDTO messageDTO) {
        return new TextMessage(jacksonHandler.writeValueAsString(messageDTO));
    }

    @SneakyThrows
    private TextMessage buildTextMessage(@Payload LikeMessage message) {
        return new TextMessage(jacksonHandler.writeValueAsString(message));
    }

    @SneakyThrows
    @KafkaListener(topics = "LIKE_MESSAGE")
    public void handleLikeMessage(LikeMessage likeMessage) {
        log.info("New like message: " + jacksonHandler.writeValueAsString(likeMessage));
        MessageDTO message = messageService.likeMessage(likeMessage);
        likeMessage.setChatId(message.getChatId());
        sendMessageToChanel(likeMessage);
    }

    @SneakyThrows
    @KafkaListener(topics = "DELETE_MESSAGE")
    public void handleDeleteMessage(DeleteMessage deleteMessage) {
        log.info("Delete message: " + jacksonHandler.writeValueAsString(deleteMessage));
        Long chatId = messageService.deleteMessage(deleteMessage);
        sendMessageToChanel(deleteMessage, chatId);
    }

    private void sendMessageToChanel(DeleteMessage message, Long chatId) {
        Set<WebSocketSession> sessions = socketSessionPool.getPersonWebSocketSession(chatService.getConsumersId(chatId));
        sessions.forEach(session -> sendMessageToSession(session, buildTextMessage(message)));
    }

    @SneakyThrows
    private TextMessage buildTextMessage(@Payload DeleteMessage message) {
        return new TextMessage(jacksonHandler.writeValueAsString(message));
    }
}
