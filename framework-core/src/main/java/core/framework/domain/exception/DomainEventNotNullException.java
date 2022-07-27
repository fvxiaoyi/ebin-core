package core.framework.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DomainEventNotNullException extends AbstractDomainException {
    public DomainEventNotNullException() {
        super("Domain event must not be null!", HttpStatus.CONFLICT.toString());
    }
}
