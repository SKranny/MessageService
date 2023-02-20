package messageService.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebSocketSessionPool {
    private final Map<Long, WebSocketSession> webSocketSessionPool = new HashMap<>();

    public void addUserSessionToPool(@NonNull Long personId, @NonNull WebSocketSession session) {
        log.info(String.format("The person with id = %s was registered", personId));
        webSocketSessionPool.put(personId, session);
    }

    public void removeSessionFromPool(@NonNull Long personId) {
        log.info(String.format(String.format("The person with id = %s was deleted from session pool", personId)));
        webSocketSessionPool.remove(personId);
    }

    public WebSocketSession getPersonWebSocketSession(@NonNull Long personId) {
        return webSocketSessionPool.get(personId);
    }

    public Set<WebSocketSession> getPersonWebSocketSession(@NonNull Set<Long> personIds) {
        return personIds.stream().map(webSocketSessionPool::get).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
