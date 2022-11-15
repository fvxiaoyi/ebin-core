package apps.example.application.service;

import apps.example.application.service.dto.ExampleMessage;
import core.framework.kafka.annotation.KafkaMessageHandler;
import core.framework.kafka.consumer.MessageHandler;
import org.springframework.messaging.Message;

/**
 * @author ebin
 */
@KafkaMessageHandler(topic = "example")
public class ExampleTopicListener implements MessageHandler<ExampleMessage> {
    @Override
    public void handle(Message<ExampleMessage> message) throws Exception {
        System.out.println(message.getPayload());
    }
}
