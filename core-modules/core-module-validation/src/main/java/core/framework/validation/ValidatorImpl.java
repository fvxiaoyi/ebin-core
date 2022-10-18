package core.framework.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Collections;
import java.util.Set;

/**
 * @author ebin
 */
public class ValidatorImpl implements Validator {

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        Class<T> rootBeanClass = (Class<T>) object.getClass();
        BeanValidator validator = BeanValidatorManager.getValidator(rootBeanClass);
        if (validator != null) {
            return validator.validate(object).map(m -> {
                ConstraintViolation<T> of = ConstraintViolationImpl.of(m);
                return Set.of(of);
            }).orElse(Collections.emptySet());
        }
        return Collections.emptySet();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExecutableValidator forExecutables() {
        throw new UnsupportedOperationException();
    }
}
