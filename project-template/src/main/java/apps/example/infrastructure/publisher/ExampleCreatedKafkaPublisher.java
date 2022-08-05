package apps.example.infrastructure.publisher;

import apps.example.application.publisher.ExampleCreatedPublisher;
import apps.example.domain.event.ExampleCreatedEvent;
import org.springframework.stereotype.Service;

/**
 * @author ebin
 */
@Service
public class ExampleCreatedKafkaPublisher implements ExampleCreatedPublisher {
    @Override
    public void publish(ExampleCreatedEvent event) {
        //event to kafka event
        //publish to kafka topic
    }
}
