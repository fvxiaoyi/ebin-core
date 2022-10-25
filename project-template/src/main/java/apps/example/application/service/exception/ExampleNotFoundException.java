package apps.example.application.service.exception;


import core.framework.web.exception.NotFoundApplicationException;

/**
 * @author ebin
 */
public class ExampleNotFoundException extends NotFoundApplicationException {
    public ExampleNotFoundException(String id) {
        super("example id = " + id + "not found!");
    }
}
