package core.framework.kafka;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ebin
 */
public final class MessageHandlerAdapterHolder {
    private static final Map<String, MessageHandlerAdapter<?>> MESSAGE_HANDLERS = new HashMap<>();

    private MessageHandlerAdapterHolder() {
    }

    public static synchronized void addMessageHandler(String topic, MessageHandlerAdapter<?> messageHandlerAdapter) {
        MESSAGE_HANDLERS.put(topic, messageHandlerAdapter);
    }

    public static MessageHandlerAdapter<?> get(String topic) {
        return MESSAGE_HANDLERS.get(topic);
    }

}
