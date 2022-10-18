package core.framework.validation.configuration;

import core.framework.validation.BeanValidateRegistrator;
import core.framework.validation.SpringValidator;
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
