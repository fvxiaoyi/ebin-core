package core.framework.validate;

import java.util.Optional;

/**
 * @author ebin
 */
public interface BeanValidator {
    Optional<String> validate(Object instance);
}
