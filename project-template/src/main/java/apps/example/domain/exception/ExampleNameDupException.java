package apps.example.domain.exception;

import core.framework.web.exception.AbstractDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ebin
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ExampleNameDupException extends AbstractDomainException {
    public ExampleNameDupException() {
        super("name dup", "NAME_DUP");
    }
}
