package club.tempvs.user.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(CONFLICT)
public class VerificationAlreadyExistsException extends RuntimeException {

}
