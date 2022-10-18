package core.framework.json;

/**
 * @author ebin
 */
public abstract class AbstractApplicationException extends BaseRuntimeException {
    public AbstractApplicationException(String message) {
        super(message);
    }

    public AbstractApplicationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public AbstractApplicationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
