package core.framework.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * @author ebin
 */
public class ConstraintViolationImpl<T> implements ConstraintViolation<T> {
    private String message;

    public ConstraintViolationImpl(String message) {
        this.message = message;
    }

    public static <T> ConstraintViolation<T> of(String message) {
        return new ConstraintViolationImpl<>(message);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getMessageTemplate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getRootBean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<T> getRootBeanClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getLeafBean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] getExecutableParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getExecutableReturnValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path getPropertyPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getInvalidValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U> U unwrap(Class<U> type) {
        throw new UnsupportedOperationException();
    }
}
