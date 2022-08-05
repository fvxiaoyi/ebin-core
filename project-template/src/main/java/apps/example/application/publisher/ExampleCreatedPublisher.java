package apps.example.application.publisher;

import apps.example.domain.event.ExampleCreatedEvent;

/**
 * @author ebin
 */
public interface ExampleCreatedPublisher {
    void publish(ExampleCreatedEvent event);
}
