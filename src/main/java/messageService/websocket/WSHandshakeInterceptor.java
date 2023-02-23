package messageService.websocket;

import lombok.extern.slf4j.Slf4j;
import messageService.exception.MessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class WSHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("BeforeHandshake start");
        try {
            URI uri = request.getURI();
            String token = uri.getQuery().split("=")[1];
            attributes.put("Authorization", token);
            log.info("Invalid request");
            return true;
        } catch (Exception ex) {
            throw new MessageException("No", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
