package core.framework.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundApplicationException extends AbstractApplicationException {
    public NotFoundApplicationException(String message) {
        super(message, HttpStatus.NOT_FOUND.toString());
    }

    public NotFoundApplicationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public NotFoundApplicationException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND.toString(), cause);
    }

    public NotFoundApplicationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
