package core.framework.kafka.consumer;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ebin
 */
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class ExtendKafkaListenerConfigurationSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{RegisterKafkaListenerAnnotationProcessorConfiguration.class.getName()};
    }
}
