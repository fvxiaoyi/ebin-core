package core.framework.kafka;

import core.framework.json.JSONMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.adapter.BatchMessagingMessageListenerAdapter;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.messaging.Message;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ebin
 */
public class DispatchedMessagingMessageListenerAdapter
        extends BatchMessagingMessageListenerAdapter<byte[], byte[]>
        implements BatchAcknowledgingConsumerAwareMessageListener<byte[], byte[]> {
    private static final Log LOGGER = LogFactory.getLog(DispatchedMessagingMessageListenerAdapter.class);

    public DispatchedMessagingMessageListenerAdapter() {
        super(null, null);
        setMessageConverter(new ByteArrayJsonMessageConverter(JSONMapper.OBJECT_MAPPER));
        setBatchMessageConverter(new BatchMessagingMessageConverter(getMessageConverter()));
    }


    @Override
    public void onMessage(List<ConsumerRecord<byte[], byte[]>> kafkaRecords, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        try {
            Map<String, List<ConsumerRecord<byte[], byte[]>>> messages = new HashMap<>();     // record in one topic maintains order
            for (ConsumerRecord<byte[], byte[]> record : kafkaRecords) {
                messages.computeIfAbsent(record.topic(), key -> new ArrayList<>()).add(record);
            }
            for (Map.Entry<String, List<ConsumerRecord<byte[], byte[]>>> entry : messages.entrySet()) {
                String topic = entry.getKey();
                List<ConsumerRecord<byte[], byte[]>> records = entry.getValue();
                MessageHandlerAdapter<?> messageHandlerAdapter = MessageHandlerAdapterHolder.get(topic);
                handle(messageHandlerAdapter, records, acknowledgment, consumer);
            }
        } finally {
            acknowledgment.acknowledge();
        }
    }

    <T> void handle(MessageHandlerAdapter<T> messageHandlerAdapter,
                    List<ConsumerRecord<byte[], byte[]>> records,
                    Acknowledgment acknowledgment,
                    Consumer<?, ?> consumer) {
        Message<T> message;
        try {
            if (messageHandlerAdapter.isBatch()) {
                message = toBatchMessage(records, acknowledgment, consumer, messageHandlerAdapter.getMessageType());
                messageHandlerAdapter.handle(message);
            } else {
                for (ConsumerRecord<byte[], byte[]> record : records) {
                    message = toMessage(record, acknowledgment, consumer, messageHandlerAdapter.getMessageType());
                    messageHandlerAdapter.handle(message);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    <T> Message<T> toBatchMessage(List<ConsumerRecord<byte[], byte[]>> records,
                                  Acknowledgment acknowledgment,
                                  Consumer<?, ?> consumer,
                                  Type payloadType) {
        return (Message<T>) getBatchMessageConverter().toMessage(Collections.unmodifiableList(records), acknowledgment, consumer, payloadType);
    }

    <T> Message<T> toMessage(ConsumerRecord<byte[], byte[]> record,
                             Acknowledgment acknowledgment,
                             Consumer<?, ?> consumer,
                             Type payloadType) {
        return (Message<T>) getMessageConverter().toMessage(record, acknowledgment, consumer, payloadType);
    }
}
