package core.framework.kafka.consumer;

import core.framework.kafka.annotation.KafkaMessageHandler;
import org.springframework.messaging.Message;

import java.lang.reflect.Type;

/**
 * @author ebin
 */
public class MessageHandlerAdapter<T> {
    private String topic;
    private boolean batch;
    private MessageHandler<T> messageHandler;
    private Type messageType;

    public MessageHandlerAdapter(KafkaMessageHandler ann, MessageHandler<T> messageHandler, Type messageType) {
        this.topic = ann.topic();
        this.batch = ann.batch();
        this.messageHandler = messageHandler;
        this.messageType = messageType;
    }

    public String getTopic() {
        return topic;
    }

    public boolean isBatch() {
        return batch;
    }

    public MessageHandler<T> getMessageHandler() {
        return messageHandler;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void handle(Message<T> message) throws Exception {
        messageHandler.handle(message);
    }
}
