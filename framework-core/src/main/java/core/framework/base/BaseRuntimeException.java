package core.framework.base;

/**
 * @author ebin
 */
public class BaseRuntimeException extends RuntimeException {
    private final String errorCode;

    public BaseRuntimeException(String message) {
        super(message);
        errorCode = "UNASSIGNED";
    }

    public BaseRuntimeException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseRuntimeException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}