package core.framework.validate;

import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * @author ebin
 */
public class SpringValidator extends SpringValidatorAdapter {
    public SpringValidator() {
        super(new ValidatorImpl());
    }
}
