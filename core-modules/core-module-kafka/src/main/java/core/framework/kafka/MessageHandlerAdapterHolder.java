package core.framework.kafka;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public class MessageHandlerAdapterHolder {
    private static final Map<String, MessageHandlerAdapter<?>> messageHandlers = new HashMap<>();

    public static synchronized void addMessageHandler(String topic, MessageHandlerAdapter<?> messageHandlerAdapter) {
        messageHandlers.put(topic, messageHandlerAdapter);
    }

    public static MessageHandlerAdapter<?> get(String topic) {
        return messageHandlers.get(topic);
    }
}
