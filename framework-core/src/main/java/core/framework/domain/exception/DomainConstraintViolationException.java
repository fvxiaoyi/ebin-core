package core.framework.domain.exception;

import core.framework.exception.AbstractDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DomainConstraintViolationException extends AbstractDomainException {
    public DomainConstraintViolationException(String message) {
        super(message, HttpStatus.CONFLICT.toString());
    }
}