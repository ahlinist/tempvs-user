package club.tempvs.user.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception to represent the 401 Http status.
 */
@ResponseStatus(UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
