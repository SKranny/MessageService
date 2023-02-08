package messageService.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
public class MessageException extends RuntimeException {
    private final HttpStatus status;

    public MessageException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        log.info("Message: {}\nStatus: {}", message, status.name());
    }

    public MessageException(Throwable ex, HttpStatus status) {
        super(ex.getMessage());
        this.status = status;
        log.info("Message: {}\nStatus: {}", ex.getMessage(), status.name());
    }

    public MessageException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
}
