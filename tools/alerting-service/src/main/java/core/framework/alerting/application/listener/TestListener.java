package core.framework.alerting.application.listener;

import core.framework.kafka.MessageHandler;
import core.framework.kafka.annotation.KafkaMessageHandler;
import org.springframework.messaging.Message;

/**
 * @author ebin
 */
@KafkaMessageHandler(topic = "test")
public class TestListener implements MessageHandler<TestMessage> {

    @Override
    public void handle(Message<TestMessage> message) throws Exception {
        message.getPayload();
    }
}
