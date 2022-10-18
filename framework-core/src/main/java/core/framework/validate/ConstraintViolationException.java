package core.framework.validate;

import core.framework.json.BaseRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConstraintViolationException extends BaseRuntimeException {
    public ConstraintViolationException(String message) {
        super(message, "BEAN_VALIDATED_ERROR");
    }
}
