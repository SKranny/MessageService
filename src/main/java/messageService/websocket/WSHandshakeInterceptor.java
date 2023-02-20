package messageService.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class WSHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("BeforeHandshake start");
        if (Optional.ofNullable(request.getHeaders().get("authorization")).isPresent()) {
            if (Optional.ofNullable(request.getHeaders().get("authorization").get(0)).isPresent()) {
                log.info(String.format("Token is exist [%s]", Objects.requireNonNull(request.getHeaders().get("authorization")).get(0)));
                attributes.put("Authorization", Objects.requireNonNull(request.getHeaders().get("authorization")).get(0).replaceAll("Bearer ", ""));

                return true;
            }
        }
        log.info("Invalid request");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
