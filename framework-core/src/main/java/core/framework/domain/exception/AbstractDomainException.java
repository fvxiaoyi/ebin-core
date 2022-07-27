package core.framework.domain.exception;

import core.framework.base.BaseRuntimeException;

/**
 * @author ebin
 */
public abstract class AbstractDomainException extends BaseRuntimeException {
    public AbstractDomainException(String message) {
        super(message);
    }

    public AbstractDomainException(String message, String errorCode) {
        super(message, errorCode);
    }

    public AbstractDomainException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
