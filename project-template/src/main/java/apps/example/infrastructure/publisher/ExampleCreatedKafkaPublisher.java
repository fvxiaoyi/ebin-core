package apps.example.infrastructure.publisher;

import apps.example.application.publisher.ExampleCreatedPublisher;
import apps.example.interfaces.message.ExampleCreatedMessage;
import core.framework.kafka.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class ExampleCreatedKafkaPublisher implements ExampleCreatedPublisher {
    @Autowired
    private MessagePublisher messagePublisher;

    @Override
    public void publish(ExampleCreatedMessage event) {
        messagePublisher.publish("example", null, event);
    }
}
