package core.framework.kafka.consumer;

import org.springframework.kafka.config.AbstractKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.kafka.support.converter.MessageConverter;

import javax.validation.Validator;

/**
 * @author ebin
 */
public class DispatcherKafkaListenerEndpoint extends AbstractKafkaListenerEndpoint<byte[], byte[]> {
    private final Validator validator;

    public DispatcherKafkaListenerEndpoint(Validator validator) {
        this.validator = validator;
    }

    @Override
    protected MessagingMessageListenerAdapter<byte[], byte[]> createMessageListener(MessageListenerContainer container, MessageConverter messageConverter) {
        return new DispatcherMessagingMessageListenerAdapter(validator);
    }
}
