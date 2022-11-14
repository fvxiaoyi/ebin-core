package core.framework.kafka;

import org.springframework.kafka.config.AbstractKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.kafka.support.converter.MessageConverter;

/**
 * @author ebin
 */
public class DispatcherKafkaListenerEndpoint extends AbstractKafkaListenerEndpoint<byte[], byte[]> {

    @Override
    protected MessagingMessageListenerAdapter<byte[], byte[]> createMessageListener(MessageListenerContainer container, MessageConverter messageConverter) {
        DispatcherMessagingMessageListenerAdapter adapter = new DispatcherMessagingMessageListenerAdapter();
        return adapter;
    }
}
