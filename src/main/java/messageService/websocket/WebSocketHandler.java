package messageService.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import messageService.constants.messages.MessageType;
import messageService.dto.mesage.DeleteMessage;
import messageService.dto.mesage.LikeMessage;
import messageService.dto.mesage.SendMessage;
import messageService.service.person.PersonMicroService;
import messageService.service.person.PersonService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import security.dto.TokenData;
import security.utils.JwtService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final JwtService jwtService;

    private final MessageBrokerHandler messageBrokerHandler;

    private  final WebSocketSessionPool webSocketSessionPool;

    private final PersonMicroService personMicroService;

    private final ObjectMapper jacksonHandler;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        TokenData tokenData = jwtService.parseToken((String) session.getAttributes().get("Authorization"));
        webSocketSessionPool.removeSessionFromPool(tokenData.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        TokenData tokenData = jwtService.parseToken((String) session.getAttributes().get("Authorization"));
        webSocketSessionPool.addUserSessionToPool(tokenData.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        mapMessage(MessageType.getMessageByString(new JSONObject(textMessage.getPayload()).getString("type")), textMessage);
    }

    @SneakyThrows
    private void mapMessage(MessageType type, TextMessage textMessage) {
        switch (type) {
            case SEND_MESSAGE:
                messageBrokerHandler.sendMessage(type.name(), jacksonHandler.readValue(textMessage.getPayload(), SendMessage.class));
                break;
            case LIKE_MESSAGE:
                messageBrokerHandler.likeMessage(type.name(), jacksonHandler.readValue(textMessage.getPayload(), LikeMessage.class));
                break;
            case DELETE_MESSAGE:
                messageBrokerHandler.deleteMessage(type.name(), jacksonHandler.readValue(textMessage.getPayload(), DeleteMessage.class));
        }
    }
}
