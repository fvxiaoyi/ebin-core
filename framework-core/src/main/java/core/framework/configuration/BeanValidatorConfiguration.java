package core.framework.configuration;

import core.framework.validate.BeanValidateRegistrator;
import core.framework.validate.SpringValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ebin
 */
@Configuration
public class BeanValidatorConfiguration {
    @Bean
    public BeanValidateRegistrator beanValidateRegistrator() {
        return new BeanValidateRegistrator();
    }

    @Bean
    public SpringValidator springValidator() {
        return new SpringValidator();
    }

}
