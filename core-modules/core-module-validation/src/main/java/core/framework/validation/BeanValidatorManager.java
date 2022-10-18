package core.framework.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public final class BeanValidatorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanValidatorManager.class);
    private static Map<Class<?>, BeanValidator> validators = new HashMap<>();

    private BeanValidatorManager() {
    }

    public static void addValidator(Class<?> beanClass) {
        validators.computeIfAbsent(beanClass, k -> {
            LOGGER.info("add bean validator : " + beanClass.getName());
            return new BeanValidatorBuilder(beanClass).build();
        });
    }

    public static BeanValidator getValidator(Class<?> beanClass) {
        return validators.get(beanClass);
    }
}
