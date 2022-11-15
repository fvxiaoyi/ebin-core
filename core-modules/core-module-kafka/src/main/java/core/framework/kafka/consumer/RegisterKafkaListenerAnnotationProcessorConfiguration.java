package core.framework.kafka.consumer;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ebin
 */
public class RegisterKafkaListenerAnnotationProcessorConfiguration implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition(ExtendKafkaListenerAnnotationBeanPostProcessor.class.getName(),
                new RootBeanDefinition(ExtendKafkaListenerAnnotationBeanPostProcessor.class));
    }
}
