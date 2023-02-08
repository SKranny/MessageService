package messageService.controller.advice;

import messageService.exception.MessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorMessage> authExceptionHandler(MessageException authException, HttpServletRequest req) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .error(authException.getMessage())
                .timeStamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(errorMessage, authException.getStatus());
    }
}
