package core.framework.kafka.test;

import core.framework.kafka.annotation.KafkaMessageHandler;
import core.framework.kafka.consumer.MessageHandler;
import org.springframework.messaging.Message;

/**
 * @author ebin
 */
@KafkaMessageHandler(topic = "demo", batch = false)
public class DemoMessageHandle implements MessageHandler<DemoMessage> {
    @Override
    public void handle(Message<DemoMessage> message) throws Exception {

    }
}
