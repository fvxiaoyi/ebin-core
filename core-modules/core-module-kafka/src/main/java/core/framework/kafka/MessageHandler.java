package core.framework.kafka;

import org.springframework.messaging.Message;

/**
 * @author ebin
 */
@FunctionalInterface
public interface MessageHandler<T> {
    void handle(Message<T> message) throws Exception;
}
