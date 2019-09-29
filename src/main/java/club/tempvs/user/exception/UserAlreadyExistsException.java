package club.tempvs.user.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

}
