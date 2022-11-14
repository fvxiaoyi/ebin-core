package core.framework.kafka;

import org.springframework.core.MethodParameter;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * @author ebin
 */
public class ExtendKafkaNullAwarePayloadArgumentResolver extends PayloadMethodArgumentResolver {

    public ExtendKafkaNullAwarePayloadArgumentResolver(MessageConverter messageConverter, Validator validator) {
        super(messageConverter, validator);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception { // NOSONAR
        Object resolved = super.resolveArgument(parameter, message);
        /*
         * Replace KafkaNull list elements with null.
         */
        if (resolved instanceof List) {
            List<?> list = (List<?>) resolved;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof KafkaNull) {
                    list.set(i, null);
                }
            }
        }
        return resolved;
    }

    @Override
    protected boolean isEmptyPayload(Object payload) {
        return payload == null || payload instanceof KafkaNull;
    }

}