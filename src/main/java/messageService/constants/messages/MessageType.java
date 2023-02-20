package messageService.constants.messages;

import messageService.exception.MessageException;

import java.util.Arrays;

public enum MessageType {
    SEND_MESSAGE, LIKE_MESSAGE, DELETE_MESSAGE;

    public static MessageType getMessageByString(String name) {
        return Arrays.stream(MessageType.values()).filter(t -> t.name().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new MessageException("Error! Unknown message type"));
    }
}
