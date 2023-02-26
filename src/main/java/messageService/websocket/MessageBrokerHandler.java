package messageService.websocket;

import lombok.RequiredArgsConstructor;
import messageService.dto.mesage.DeleteMessage;
import messageService.dto.mesage.LikeMessage;
import messageService.dto.mesage.SendMessage;
import messageService.dto.mesage.WriteMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageBrokerHandler {
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void sendMessage(String topic, SendMessage message) {
        kafkaTemplate.send(topic, message);
    }

    public void likeMessage(String topic, LikeMessage message) {
        kafkaTemplate.send(topic, message);
    }

    public void deleteMessage(String topic, DeleteMessage message) {
        kafkaTemplate.send(topic, message);
    }

    public void personWriteMessage(String topic, WriteMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
