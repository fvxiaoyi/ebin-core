package core.framework.kafka.consumer;

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
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class DispatcherMessagingMessageListenerAdapter
        extends BatchMessagingMessageListenerAdapter<byte[], byte[]>
        implements BatchAcknowledgingConsumerAwareMessageListener<byte[], byte[]> {
    private static final Log LOGGER = LogFactory.getLog(DispatcherMessagingMessageListenerAdapter.class);

    public DispatcherMessagingMessageListenerAdapter() {
        super(null, null);
        setMessageConverter(new ByteArrayJsonMessageConverter(JSONMapper.OBJECT_MAPPER));
        setBatchMessageConverter(new BatchMessagingMessageConverter(getMessageConverter()));
    }


    @Override
    public void onMessage(List<ConsumerRecord<byte[], byte[]>> kafkaRecords, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        try {
            Map<String, List<ConsumerRecord<?, ?>>> messages = new HashMap<>();     // record in one topic maintains order
            for (ConsumerRecord<?, ?> kafkaRecord : kafkaRecords) {
                messages.computeIfAbsent(kafkaRecord.topic(), key -> new ArrayList<>()).add(kafkaRecord);
            }
            for (Map.Entry<String, List<ConsumerRecord<?, ?>>> entry : messages.entrySet()) {
                String topic = entry.getKey();
                List<ConsumerRecord<?, ?>> records = entry.getValue();
                MessageHandlerAdapter<?> messageHandlerAdapter = MessageHandlerAdapterHolder.get(topic);
                handle(messageHandlerAdapter, records, acknowledgment, consumer);
            }
        } finally {
            acknowledgment.acknowledge();
        }
    }

    <T> void handle(MessageHandlerAdapter<T> messageHandlerAdapter,
                    List<ConsumerRecord<?, ?>> records,
                    Acknowledgment acknowledgment,
                    Consumer<?, ?> consumer) {
        Message<T> message;

        if (messageHandlerAdapter.isBatch()) {
            try {
                message = toBatchMessage(records, acknowledgment, consumer, messageHandlerAdapter.getMessageType());
                messageHandlerAdapter.handle(message);
            } catch (Exception e) {
                String partitions = records.stream().map(m -> String.valueOf(m.topic())).collect(Collectors.joining(","));
                String offsets = records.stream().map(m -> String.valueOf(m.offset())).collect(Collectors.joining(","));
                LOGGER.error("Batch message : " + messageHandlerAdapter.getTopic() + " - " + partitions + " - " + offsets + " are consume failed, error message : " + e.getMessage());
            }
        } else {
            for (ConsumerRecord<?, ?> kafkaRecord : records) {
                try {
                    message = toMessage(kafkaRecord, acknowledgment, consumer, messageHandlerAdapter.getMessageType());
                    messageHandlerAdapter.handle(message);
                } catch (Exception e) {
                    LOGGER.error("Message : " + kafkaRecord.topic() + " - " + kafkaRecord.partition() + " - " + kafkaRecord.offset() + " consume failed, error message : " + e.getMessage());
                }
            }
        }
    }

    <T> Message<T> toBatchMessage(List<ConsumerRecord<?, ?>> kafkaRecords,
                                  Acknowledgment acknowledgment,
                                  Consumer<?, ?> consumer,
                                  Type payloadType) {
        return (Message<T>) getBatchMessageConverter().toMessage(Collections.unmodifiableList(kafkaRecords), acknowledgment, consumer, payloadType);
    }

    <T> Message<T> toMessage(ConsumerRecord<?, ?> kafkaRecord,
                             Acknowledgment acknowledgment,
                             Consumer<?, ?> consumer,
                             Type payloadType) {
        return (Message<T>) getMessageConverter().toMessage(kafkaRecord, acknowledgment, consumer, payloadType);
    }
}
