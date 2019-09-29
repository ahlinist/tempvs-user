package club.tempvs.user.exception.handler;

import club.tempvs.user.exception.UnauthorizedException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> returnBadRequest(IllegalArgumentException e) {
        processException(e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> returnNotFound(NoSuchElementException e) {
        processException(e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> returnInternalError(Exception e) {
        processException(e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> returnUnauthorized(UnauthorizedException e) {
        processException(e);
        return ResponseEntity.status(UNAUTHORIZED).build();
    }

    @ExceptionHandler(HystrixRuntimeException.class)
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public ResponseEntity<String> returnServiceUnavailable(HystrixRuntimeException e) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).build();
    }

    private void processException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTraceString = sw.toString();
        log.error(stackTraceString);
    }
}
