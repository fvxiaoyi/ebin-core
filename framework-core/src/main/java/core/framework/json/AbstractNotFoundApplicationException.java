package core.framework.json;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public abstract class AbstractNotFoundApplicationException extends AbstractApplicationException {
    public AbstractNotFoundApplicationException(String message) {
        super(message, HttpStatus.NOT_FOUND.toString());
    }

    public AbstractNotFoundApplicationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public AbstractNotFoundApplicationException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND.toString(), cause);
    }

    public AbstractNotFoundApplicationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
