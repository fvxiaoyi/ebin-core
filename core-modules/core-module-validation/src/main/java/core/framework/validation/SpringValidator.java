package core.framework.validation;

import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * @author ebin
 */
public class SpringValidator extends SpringValidatorAdapter {
    public SpringValidator() {
        super(new ValidatorImpl());
    }
}
