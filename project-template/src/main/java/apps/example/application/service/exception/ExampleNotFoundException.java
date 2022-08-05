package apps.example.application.service.exception;

import core.framework.exception.AbstractNotFoundApplicationException;

/**
 * @author ebin
 */
public class ExampleNotFoundException extends AbstractNotFoundApplicationException {
    public ExampleNotFoundException(String id) {
        super("example id = " + id + "not found!");
    }
}
